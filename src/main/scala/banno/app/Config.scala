package banno.app

import org.http4s.Uri
import pureconfig.ConfigReader

import scala.concurrent.duration.Duration

object Type {
  type WeatherThreshold = Map[String, Long]
}
case class ServerConfig(host: String, port: Int)
case class BlazeClientConfig(connectionTimeout: Duration, requestTimeout: Duration)
case class OpenWeatherConfig(apiKey: String, host: Uri, defaultMetric: String, WeatherThreshold: Type.WeatherThreshold)

case class Config(
  openWeatherConfig: OpenWeatherConfig,
  serverConfig: ServerConfig,
  blazeClientConfig: BlazeClientConfig,
)

object Config {
  import pureconfig.generic.auto._
  import pureconfig.module.http4s._

  implicit val reader: ConfigReader[Config] = exportReader[Config].instance
}
