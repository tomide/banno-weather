package banno

import cats.data.NonEmptyList
import cats.implicits._
import enumeratum.EnumEntry.Lowercase
import enumeratum.{Enum, EnumEntry}
import io.circe.Codec
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

package object model {

  case class Coordinate(longitude: Long, latitude: Long)

  sealed trait Metrics extends EnumEntry with Lowercase {
    val value: String
    override def toString: String = value
  }

  object Metrics extends Enum[Metrics] {

    case object FOR_YOU extends Metrics {
      val value = "for_you"
    }

    case object LOCATION extends Metrics {
      val value = "location"
    }

    case object TOPIC extends Metrics {
      val value = "topic"
    }

    val values: IndexedSeq[Metrics] = findValues

    def parse(value: String): Option[Metrics] = values.find(_.entryName.equalsIgnoreCase(value))
  }

  object MetricsPathMatcher {

    def unapply(str: String): Option[NonEmptyList[Metrics]] =
      str.split(",").toList.traverse(Metrics.parse).flatMap(_.toNel)
  }


  case class WeatherData()
  case class BannoWeatherResponse()
  implicit val codecConfiguration: Configuration =
    Configuration.default.withSnakeCaseMemberNames.withDefaults

  implicit val weatherEncoder: Codec[WeatherData] =
    deriveConfiguredCodec

  implicit val coordinateDecoder: Codec[Coordinate] =
    deriveConfiguredCodec

}