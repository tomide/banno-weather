package banno.service

import banno.model.{BannoWeatherAppResponse, Coordinate, WeatherData, WeatherUnit}

trait BannoWeatherService[F[_]] {
  def retrieveWeatherData(coordinate: Coordinate, metrics: WeatherUnit): F[BannoWeatherAppResponse]
}
