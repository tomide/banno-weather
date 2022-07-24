package banno.app

import io.circe.Encoder
import io.circe.syntax.EncoderOps
import org.http4s.Status

sealed trait ErrorType {
  def id: String
}

object ErrorType {

  case class Custom(id: String) extends ErrorType
  object NotFound               extends Custom("not_found")
  object IllegalInput           extends Custom("illegal_input")
  object NoAccess               extends Custom("forbidden")
  object Unexpected             extends Custom("unexpected")

  implicit val encoder: Encoder[ErrorType] = _.id.asJson
}

trait ErrorTypeToHttpStatus {
  def apply(code: ErrorType): Status
}

object ErrorTypeToHttpStatus {

  private[this] val DefaultCustomErrorToStatus: ErrorType.Custom => Status =
    _ => Status.InternalServerError

  def default(onCustom: ErrorType.Custom => Status = DefaultCustomErrorToStatus): ErrorTypeToHttpStatus = {
    case ErrorType.NotFound       => Status.NotFound
    case ErrorType.IllegalInput   => Status.BadRequest
    case ErrorType.NoAccess       => Status.Forbidden
    case ErrorType.Unexpected     => Status.InternalServerError
    case custom: ErrorType.Custom => onCustom(custom)
  }
}
