package banno.app

import cats.data.{Kleisli, OptionT}
import cats.effect.{Clock, Sync}
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import org.http4s._
import org.http4s.server.HttpMiddleware
import org.http4s.util.CaseInsensitiveString

import java.util.concurrent.TimeUnit

object MiddlewareLogger {
  def apply[F[_]: Sync: Clock: Logger]: HttpMiddleware[F] = {
    val logger: String => F[Unit] = (info: String) => {
      Logger[F].info(info)
    }
    handler =>
    Kleisli { request: Request[F] =>
      OptionT(message(request)(logHeaders = false).flatMap { requestInfo =>
        for {
          _        <- logger(requestInfo)
          time1    <- Clock[F].monotonic(TimeUnit.NANOSECONDS)
          response <- handler.run(request).value
          _        <- response.fold(Sync[F].unit)(responseInfo =>
            for {
              _     <- message(responseInfo)(logHeaders = false)
              time2 <- Clock[F].monotonic(TimeUnit.NANOSECONDS)
              _     <- logger(s"$requestInfo - $responseInfo - ${TimeUnit.NANOSECONDS.toMillis(time2 - time1)} ms")
            } yield ()
          )
        } yield response
      })
    }
  }

  def message[F[_]: Sync](message: Message[F])(
    logHeaders: Boolean,
    redactHeadersWhen: CaseInsensitiveString => Boolean = Headers.SensitiveHeaders.contains,
  ): F[String] = {
    val prelude = message match {
      case req: Request[F]       =>
        Sync[F].pure(s"${req.method} ${req.uri}")
      case Status.Successful(rs) => Sync[F].pure(s"${rs.status}")
      case rs: Response[F]       =>
        // log response body in case of failure
        val isBinary   = message.contentType.exists(_.mediaType.binary)
        val isJson     = message.contentType.exists { mT =>
          mT.mediaType == MediaType.application.json ||
            mT.mediaType == MediaType.application.`vnd.hal+json`
        }
        val isText     = !isBinary || isJson
        val bodyStream = if (isText) {
          message.bodyText
        } else {
          message.body.map(x => java.lang.Integer.toHexString(x & 0xff))
        }
        bodyStream.compile.string.map(text => s"""${rs.status} body="$text"""")
    }
    val headers =
      if (logHeaders) {
        message.headers.redactSensitive(redactHeadersWhen).toList.mkString("Headers(", ", ", ")")
      } else {
        ""
      }
    prelude.map { p => s"$p $headers" }
  }
}
