package banno.app

import org.http4s.Uri

case class WeatherThreshold(veryHot: Long, hot: Long, moderate: Long, cool:Long,
                           cold: Long, extremeCold: Long)
case class OpenWeatherConfig(host: Uri, defaultMetric: String, weatherThreshold: WeatherThreshold)
case class Config(openWeatherConfig: OpenWeatherConfig)

