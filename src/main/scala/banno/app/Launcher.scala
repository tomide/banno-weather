package banno.app

import banno.client.OpenWeatherClient
import banno.http.BannoWeatherRoute
import banno.service.BannoWeatherService
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.chrisdavenport.log4cats.{Logger, SelfAwareStructuredLogger, StructuredLogger}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderException

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object Launcher extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val resource: Resource[IO, (SelfAwareStructuredLogger[IO], Client[IO], Config)] =
      for {
        implicit0(logger: StructuredLogger[IO]) <- Resource.eval(Slf4jLogger.fromName[IO]("banno"))
        http                                    <- BlazeClientBuilder[IO](ExecutionContext.global)
                                                     .withConnectTimeout(20.second)
                                                     .withRequestTimeout(20.second)
                                                     .resource
        config                                  <- Resource.eval(IO.fromEither(ConfigSource.default.load[Config].leftMap(e =>
                                                     new Throwable(ConfigReaderException(e))
                                                   )))

      } yield (logger, http, config)

    resource.use { case (l, h, c) =>
      implicit val logger: Logger[IO] = l
      val openWeatherClient           = new OpenWeatherClient[IO](c.openWeatherConfig, h)
      val bannoWeatherService         = new BannoWeatherService.Impl[IO](openWeatherClient)
      val route                       = new BannoWeatherRoute[IO](bannoWeatherService, c.openWeatherConfig)
      val routeService                = MiddleWare[IO](route.apply)
      BlazeServerBuilder[IO]
        .withoutBanner
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(routeService)
        .withNio2(false)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    }
  }
}
