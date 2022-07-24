package banno.app

import cats.Show
import cats.data.NonEmptyList
import io.circe.{Encoder, Json}
import io.circe.syntax._
import cats.syntax.show._
import io.circe.generic.auto.exportEncoder

sealed trait ErrorDetails

object ErrorDetails {
  case class Simple(msg: String)                               extends ErrorDetails
  case class Complex(details: NonEmptyList[ErrorDetails])      extends ErrorDetails
  case class WithSource(source: Source, details: ErrorDetails) extends ErrorDetails

  case class Source(name: String, kind: String)

  implicit val encoder: Encoder[ErrorDetails] = {
    case Simple(msg) =>
      Json.fromString(msg)

    case WithSource(Source(name, kind), details) =>
      Json.obj(
        ("source", Json.fromString(name)),
        ("kind", Json.fromString(kind)),
        ("details", encoder.apply(details)),
      )

    case Complex(details) =>
      details.toList.asJson
  }

  implicit val showInstance: Show[ErrorDetails] = {
    case Simple(msg) => msg

    case WithSource(Source(name, kind), details) =>
      show"""
            | source = $name
            | kind = $kind
            | details = ${showInstance.show(details)}
            |""".stripMargin

    case Complex(details) => details.toList.map(showInstance.show).mkString("\n---")
  }
}