package banno.app.errors

import io.circe.Json

case class AppError(
  errorMsg: String,
  errorType: ErrorType,
  errorDetails: Option[ErrorDetails] = None,
) extends RuntimeException(errorMsg)

object AppError {
  import io.circe.Encoder
  import io.circe.syntax._

  def apply(
    errorMsg: String,
    errorType: ErrorType,
    errorDetails: ErrorDetails,
  ): AppError = AppError(errorMsg, errorType, Some(errorDetails))

  implicit val encoder: Encoder[AppError] = { appError =>
    Json
      .obj(
        ("code", appError.errorType.id.asJson),
        ("message", appError.errorMsg.asJson),
        ("details", appError.errorDetails.asJson),
      )
      .dropNullValues
  }
}
