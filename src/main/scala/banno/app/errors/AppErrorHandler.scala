package banno.app.errors

import banno.app.errors.ErrorType.Unexpected
import cats.MonadError
import cats.data.{Kleisli, OptionT}
import cats.implicits.{catsSyntaxApplicativeError, showInterpolator}
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import org.http4s.circe.jsonEncoderOf
import org.http4s.server.HttpMiddleware
import org.http4s.{EntityEncoder, Response}

object AppErrorHandler {

  def apply[F[_]: Logger](
    errorTypeToStatus: ErrorTypeToHttpStatus = ErrorTypeToHttpStatus.default()
  )(implicit
    ev: MonadError[F, AppError]
  ): HttpMiddleware[F] = {
    implicit val entityEncoder: EntityEncoder[F, AppError] = jsonEncoderOf

    route =>
      Kleisli { req =>
        route.run(req).recoverWith(e =>
          OptionT.liftF {
            val isUnexpected = e.errorType == Unexpected
            val body         = if (isUnexpected) e.copy(errorMsg = "server_error") else e
            Logger[F].error(show"${e.errorMsg}").whenA(isUnexpected) *>
              ev.pure(Response[F](errorTypeToStatus(e.errorType)).withEntity(body))
          }
        )
      }
  }
}
