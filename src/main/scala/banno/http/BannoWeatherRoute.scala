package banno.http

import banno.model.{Coordinate, _}
import banno.service.BannoWeatherService
import cats.effect.{Clock, Sync}
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.dsl.Http4sDsl

class BannoWeatherRoute[F[_]: Sync: Clock: Logger](
                                                openWeatherSvc: BannoWeatherService[F]
                                              ) extends Http4sDsl[F] {

  private object WeatherUnit extends OptionalQueryParamDecoderMatcher[String]("unit")

    def apply: HttpRoutes[F] = {


    val routes                       = HttpRoutes.of[F] {
      case req @ GET -> Root / "banno" / "weather" :? WeatherUnit(unit)      =>
        for {
          coordinate <- req.as[Coordinate]
          unitMetric <- Sync[F].fromOption(UnitPathMatcher.unapply(unit.getOrElse("standard")), new RuntimeException("weather unit could not be determined"))
          response <- openWeatherSvc.retrieveWeatherData(coordinate, unitMetric)
        } yield response
    }
    routes
  }
}
