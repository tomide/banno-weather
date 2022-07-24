package banno.client

import banno.app.OpenWeatherConfig
import banno.model.{AppError, Coordinate, WeatherData, WeatherUnit}
import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.circe.syntax.EncoderOps
import org.http4s.Method.GET
import org.http4s.Request
import org.http4s.Status.Successful
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.client.Client

class OpenWeatherClient[F[_] : Sync : Logger](config: OpenWeatherConfig,
                                              http: Client[F]) {
  def getWeatherInformation(coordinate: Coordinate, units: WeatherUnit): F[Either[AppError, WeatherData]] = {
    val requestUrl = config.host
      .withPath("/data/2.5/onecall")
      .withQueryParam("lat", coordinate.latitude)
      .withQueryParam("lon", coordinate.longitude)
      .withQueryParam("units", units.value)
      .withQueryParam("appid", config.apiKey)
    val request = Request[F](uri = requestUrl, method = GET)

    http.run(request).use {
      case Successful(response) => for {
        possibleData <- response.as[WeatherData].attempt
        resp <- possibleData.fold(e => for {
          _ <- Logger[F].info(s"failed to parse returned openWeather data := $e")
          appError <- Left(AppError(500, "server_error")).pure[F]
        } yield appError,
          v => Right(v).pure[F])
      } yield resp

      case response =>
        for {
          code <- Sync[F].delay(response.status.code)
          body <- response.body.through(fs2.text.utf8Decode).compile.string
          response <- AppError(code, body).pure[F] <*
            Logger[F].info(s"request to openWeather failed with responses := $body")
        } yield Left(response)
    }
  }
}

