package banno

import enumeratum.EnumEntry.Lowercase
import enumeratum.{Enum, EnumEntry}
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredCodec, deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Codec, Decoder, Encoder}

package object model {

  case class Coordinate(longitude: Long, latitude: Long)

  sealed trait WeatherUnit extends EnumEntry with Lowercase {
    val value: String
    override def toString: String = value
  }

  object Metrics extends Enum[WeatherUnit] {

    case object STANDARD extends WeatherUnit {
      val value = "standard"
    }

    case object METRIC extends WeatherUnit {
      val value = "metric"
    }

    case object IMPERIAL extends WeatherUnit {
      val value = "imperial"
    }

    val values: IndexedSeq[WeatherUnit] = findValues

    def parse(value: String): Option[WeatherUnit] = values.find(_.entryName.equalsIgnoreCase(value))
  }

  object UnitPathMatcher {

    def unapply(str: String): Option[WeatherUnit] =
      Metrics.parse(str)
  }

  case class TemperatureInfo(main: String)
  case class CurrentTemperatureInfo(temp: Long, weather: TemperatureInfo)
  case class WeatherAlert(event: String, start: Long, end: Long)

  case class WeatherData(lat: Float, lon: Float,
                         currently: CurrentTemperatureInfo, alerts: WeatherAlert)

  case class BannoWeatherAppResponse(
                                      currentWeatherCondition : String,
                                      feelsLikeOutside: String,
                                      Alert: Option[WeatherAlert]
                                    )

  implicit val codecConfiguration: Configuration =
    Configuration.default.withSnakeCaseMemberNames.withDefaults

  implicit val temperatureInfoDecoder: Decoder[TemperatureInfo] =
    deriveConfiguredDecoder

  implicit val currentTemperatureInfoDecoder: Decoder[CurrentTemperatureInfo] =
    deriveConfiguredDecoder

  implicit val weatherAlertDecoder: Decoder[WeatherAlert] =
    deriveConfiguredDecoder

  implicit val weatherDataDecoder: Decoder[WeatherData] =
    deriveConfiguredDecoder

  implicit val coordinateDecoder: Codec[Coordinate] =
    deriveConfiguredCodec

  implicit val bannoWeatherAppResponseEncoder: Encoder[BannoWeatherAppResponse] =
    deriveConfiguredEncoder

}