package banno.service

import banno.client.OpenWeatherClient
import banno.model.{BannoWeatherAppResponse, Coordinate, TemperatureInfo, WeatherAlert, WeatherData, WeatherUnit}
import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger

trait BannoWeatherService[F[_]] {
  def retrieveWeatherData(coordinate: Coordinate, weatherUnit: WeatherUnit): F[BannoWeatherAppResponse]
}

object BannoWeatherService {
  implicit class BannoWeatherAppResponseOps(val data: WeatherData) {
    private def currentWeatherCondition: String = data.current.weather.headOption.getOrElse(
      TemperatureInfo("current weather condition is unknown")).main
    private def feelsLikeOutside: String = "ttt"
    private def alert: Seq[WeatherAlert] = data.alerts.fold(Seq[WeatherAlert]().empty)(alt => alt)
   val toBannoWeatherAppResponse = BannoWeatherAppResponse(currentWeatherCondition, feelsLikeOutside, alert)
  }
  class Impl[F[_]: Sync: Logger](owc: OpenWeatherClient[F]) extends BannoWeatherService[F] {
    override def retrieveWeatherData(coordinate: Coordinate, weatherUnit: WeatherUnit): F[BannoWeatherAppResponse] = {
      owc.getWeatherInformation(coordinate, weatherUnit).flatMap {
        case Right(value) => value.toBannoWeatherAppResponse.pure[F]
        case Left(error) => Sync[F].raiseError(error)
      }
    }
  }
}
