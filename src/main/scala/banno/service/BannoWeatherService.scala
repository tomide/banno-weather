package banno.service

import banno.model.{BannoWeatherResponse, Coordinate, WeatherData}
import jdk.internal.platform.Metrics

trait BannoWeatherService[F[_]] {
  def retrieveWeatherData(coordinate: Coordinate, metrics: Metrics): F[BannoWeatherResponse] = ???

}
