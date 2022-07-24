package banno.service

import banno.model.{BannoWeatherAppResponse, TemperatureInfo, WeatherAlert, WeatherData}

object BannoWeatherDataOps {

  implicit class BannoWeatherAppResponseOps(val data: WeatherData) {

    private def currentWeatherCondition: String = data.current.weather.headOption.getOrElse(
      TemperatureInfo("current weather condition is unknown")
    ).main
    private def feelsLikeOutside: String        = "ttt"
    private def alert: Seq[WeatherAlert]        = data.alerts.fold(Seq[WeatherAlert]().empty)(alt => alt)
    val toBannoWeatherAppResponse               = BannoWeatherAppResponse(currentWeatherCondition, feelsLikeOutside, alert)
  }
}
