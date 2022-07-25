package banno.app.errors

import cats.Show
import io.circe.{Encoder, Json}

sealed trait ErrorDetails

object ErrorDetails {
  case class Simple(msg: String) extends ErrorDetails

  implicit val encoder: Encoder[ErrorDetails] = {
    case Simple(msg) =>
      Json.fromString(msg)
  }

  implicit val showInstance: Show[ErrorDetails] = {
    case Simple(msg) => msg
  }
}
