package banno.app

import banno.app.errors.{AppErrorHandler, ErrorTypeToHttpStatus}
import cats.data.Kleisli
import cats.effect.{Clock, Sync}
import com.olegpy.meow.hierarchy.deriveMonadErrorFromThrowable
import io.chrisdavenport.log4cats.Logger
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT

object MiddleWare {

  def apply[F[_]: Sync: Logger: Clock](routes: HttpRoutes[F]): Kleisli[F, Request[F], Response[F]] = {
    implicit val logger: String => F[Unit] = (info: String) => {
      Logger[F].info(info)
    }

    val middleware: HttpRoutes[F] => HttpRoutes[F] = {
      import com.olegpy.meow.hierarchy._

      { http: HttpRoutes[F] =>
        val handler = AppErrorHandler.apply[F](ErrorTypeToHttpStatus.default())
        handler(http)
      }
    }
    middleware(routes).orNotFound
  }
}
