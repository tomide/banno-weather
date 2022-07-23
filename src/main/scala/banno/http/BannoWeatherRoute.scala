package banno.http

import banno.model.{Coordinate, _}
import banno.service.BannoWeatherService
import cats.effect.{Clock, Sync}
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import org.http4s.HttpRoutes
import org.http4s.blaze.http.HttpService
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.dsl.Http4sDsl

class BannoWeatherRoute[F[_]: Sync: Clock: Logger](
                                                openWeatherSvc: BannoWeatherService[F]
                                              ) extends HttpService
  with Http4sDsl[F] {

//  implicit val metricsDecoder: QueryParamDecoder[Option[NonEmptyList[Metrics]]] = {
//    QueryParamDecoder[String].map(x => MetricsPathMatcher.unapply(x))
//  }
  private object Metrics extends OptionalQueryParamDecoderMatcher[String]("metrics")

  override def httpService: HttpRoutes[F] = {
    val routes                       = HttpRoutes.of[F] {
      case req @ GET -> Root / "banno" / "weather" :? Metrics(metrics)      =>
        for {
          coordinate <- req.as[Coordinate]
          unitMetric = MetricsPathMatcher.unapply(metrics.getOrElse("standard"))
          response <- openWeatherSvc.retrieveWeatherData(coordinate, metrics.getOrElse("standard"))
        } yield response
    }
  }
}
