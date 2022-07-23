package banno.client

import banno.app.OpenWeatherConfig
import banno.model.{Coordinate, WeatherData, WeatherUnit}
import cats.effect.Sync
import io.chrisdavenport.log4cats.Logger
import org.http4s.Request
import org.http4s.client.Client

class OpenWeatherClient[F[_]: Sync: Logger](config: OpenWeatherConfig,
                                            http: Client[F]) {
    def getWeatherInformation(coordinate: Coordinate, metrics: WeatherUnit): F[Option[WeatherData]] = ???


    private def buildRequest(coordinate: Coordinate, metrics: WeatherUnit): Request[F] = ???
}
