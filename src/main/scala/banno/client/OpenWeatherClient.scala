package banno.client

import banno.app.errors.ErrorType.Unexpected
import banno.app.OpenWeatherConfig
import banno.model.{Coordinate, OpenWeatherError, WeatherData, WeatherUnit}
import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import org.http4s.Method.GET
import org.http4s.Request
import org.http4s.Status.Successful
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.client.Client

class OpenWeatherClient[F[_]: Sync: Logger](config: OpenWeatherConfig, http: Client[F]) {

  def getWeatherInformation(coordinate: Coordinate, units: WeatherUnit): F[Either[OpenWeatherError, WeatherData]] = {
    val requestUrl = config.host
      .withPath("/data/2.5/onecall")
      .withQueryParam("lat", coordinate.latitude)
      .withQueryParam("lon", coordinate.longitude)
      .withQueryParam("units", units.value)
      .withQueryParam("appid", config.apiKey)
    val request    = Request[F](uri = requestUrl, method = GET)

    http.run(request).use {
      case Successful(response) => for {
          possibleData <- response.as[WeatherData].attempt
          resp         <- possibleData.fold(
                            e =>
                              for {
                                _        <- Logger[F].info(s"failed to parse returned openWeather data := $e")
                                appError <- Left(OpenWeatherError(500, Unexpected.id)).pure[F]
                              } yield appError,
                            v => Right(v).pure[F],
                          )
        } yield resp

      case response =>
        for {
          code <- Sync[F].delay(response.status.code)
          body <- response.body.through(fs2.text.utf8Decode).compile.string
          _    <- Logger[F].info(s"request to openWeather failed with responses := $body")
          resp <- Left(OpenWeatherError(code, body)).pure[F]
        } yield resp
    }
  }
}
