package banno.http

import banno.app.errors.ErrorType.IllegalInput
import banno.app.OpenWeatherConfig
import banno.app.errors.AppError
import banno.model.{Coordinate, _}
import banno.service.BannoWeatherService
import cats.effect.{Clock, Sync}
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.{HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl

class BannoWeatherRoute[F[_]: Sync: Clock: Logger](
  openWeatherSvc: BannoWeatherService[F],
  config: OpenWeatherConfig,
) extends Http4sDsl[F] {

  private object WeatherUnit    extends OptionalQueryParamDecoderMatcher[String]("unit")
  private object LatitudeParam  extends QueryParamDecoderMatcher[Float]("lat")
  private object LongitudeParam extends QueryParamDecoderMatcher[Float]("lon")

  def apply: HttpRoutes[F] = {
    val routes = HttpRoutes.of[F] {
      case GET -> Root / "banno" / "weather" :? LatitudeParam(lat) +& LongitudeParam(lon) +& WeatherUnit(unit) =>
        for {
          unitMetric <- Sync[F].fromOption(
                          WeatherUnitPathMatcher.unapply(unit.getOrElse(config.defaultMetric)),
                          AppError("weather unit could not be determined", IllegalInput),
                        )
          response   <- openWeatherSvc.retrieveWeatherData(Coordinate(lat, lon), unitMetric)
        } yield Response[F]().withStatus(Ok).withEntity(response)
    }
    routes
  }
}
