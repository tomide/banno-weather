package banno.service

import banno.app.OpenWeatherConfig
import banno.client.OpenWeatherClient
import banno.client.OpenWeatherOps.OpenWeatherErrorResponseOps
import banno.model.{BannoWeatherAppResponse, Coordinate, WeatherUnit}
import banno.service.BannoWeatherDataOps.BannoWeatherAppResponseOps
import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger

trait BannoWeatherService[F[_]] {
  def retrieveWeatherData(coordinate: Coordinate, weatherUnit: WeatherUnit): F[BannoWeatherAppResponse]
}

object BannoWeatherService {

  class Impl[F[_]: Sync: Logger](owc: OpenWeatherClient[F], config: OpenWeatherConfig) extends BannoWeatherService[F] {

    override def retrieveWeatherData(coordinate: Coordinate, weatherUnit: WeatherUnit): F[BannoWeatherAppResponse] = {
      for {
        d <- owc.getWeatherInformation(coordinate, weatherUnit)
        c  = d.map(_.toBannoWeatherAppResponse(config.WeatherThreshold))
        f <- c match {
               case Right(v) => v.pure[F]
               case Left(e)  => Sync[F].raiseError(e.toAppError)
             }
      } yield f
    }
  }
}
