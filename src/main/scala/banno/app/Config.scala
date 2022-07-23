package banno.app

import org.http4s.Uri

case class OpenWeatherConfig(host: Uri, defaultMetric: String)
case class Config(openWeatherConfig: OpenWeatherConfig)

