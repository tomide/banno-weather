package banno.service

import banno.app.Type
import banno.model.{BannoWeatherAppResponse, TemperatureInfo, WeatherAlert, WeatherData}

import scala.collection.immutable.ListMap

object BannoWeatherDataOps {

  implicit class BannoWeatherAppResponseOps(val data: WeatherData) {

    private def currentWeatherCondition: String = data.current.weather.headOption.getOrElse(
      TemperatureInfo("the current weather condition in this location is unknown")
    ).main

    private def alert: Seq[WeatherAlert] = data.alerts.fold(Seq[WeatherAlert]().empty)(alt => alt)

    def toBannoWeatherAppResponse(threshold: Type.WeatherThreshold): BannoWeatherAppResponse = {

      /**
       * the feelsLikeOutside method below is a static implementation assuming all units have the same time range.
       * so please ignore the implementation as it is just a random piece of code
       */
      def feelsLikeOutside(threshold: Type.WeatherThreshold): String = {
        val orderedThreshold = ListMap(threshold.toSeq.sortWith(_._1 < _._1): _*)
        orderedThreshold.reduce { (a, b) =>
          if (a._2 <= data.current.temp && b._2 < data.current.temp) a else b
        }._1
      }
      BannoWeatherAppResponse(currentWeatherCondition, feelsLikeOutside(threshold), alert)
    }
  }
}
