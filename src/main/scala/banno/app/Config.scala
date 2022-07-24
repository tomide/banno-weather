package banno.app

import org.http4s.Uri
import pureconfig.ConfigReader

case class WeatherThreshold(veryHot: Long, hot: Long, cool: Long, cold: Long, extremeCold: Long)
case class OpenWeatherConfig(apiKey: String, host: Uri, defaultMetric: String, weatherThreshold: WeatherThreshold)
case class Config(openWeatherConfig: OpenWeatherConfig)

object Config {
  import pureconfig.generic.auto._
  import pureconfig.module.http4s._

  implicit val reader: ConfigReader[Config] = exportReader[Config].instance
}
