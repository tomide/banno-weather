package banno.service

import banno.app.errors.ErrorType.IllegalInput
import banno.app.errors
import banno.app.errors.AppError
import banno.client.OpenWeatherClient
import banno.model.{
  BannoWeatherAppResponse,
  Coordinate,
  OpenWeatherError,
  TemperatureInfo,
  WeatherAlert,
  WeatherData,
  WeatherUnit,
}
import banno.service.BannoWeatherDataOps.BannoWeatherAppResponseOps
import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger

trait BannoWeatherService[F[_]] {
  def retrieveWeatherData(coordinate: Coordinate, weatherUnit: WeatherUnit): F[BannoWeatherAppResponse]
}

object BannoWeatherService {

  class Impl[F[_]: Sync: Logger](owc: OpenWeatherClient[F]) extends BannoWeatherService[F] {

    override def retrieveWeatherData(coordinate: Coordinate, weatherUnit: WeatherUnit): F[BannoWeatherAppResponse] =
      for {
        d <- owc.getWeatherInformation(coordinate, weatherUnit)
        c  = d.map(_.toBannoWeatherAppResponse)
        f <- c match {
               case Right(v) => v.pure[F]
               case Left(_)  => Sync[F].raiseError(errors.AppError("testing error", IllegalInput))
             }
      } yield f
  }
}
