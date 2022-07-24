package banno.open.weather.codecSpec

import banno.model.{CurrentTemperatureInfo, TemperatureInfo, WeatherAlert, WeatherData}
import io.circe.Json
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class OpenWeatherCodecSpec extends AnyWordSpec with Matchers with FixtureReader {

  "should decode returned open weather json" in {

    val expectedDecodedData = WeatherData(lat = 36.154f,
      lon = -95.9928f,
      current = CurrentTemperatureInfo(temp = 303.89f, weather = Seq(TemperatureInfo("Clouds"))),
      alerts = Some(Seq(WeatherAlert("Excessive Heat Watch", 1658685600, 1658710800),
        WeatherAlert("Excessive Heat Watch", 1658772000,1658797200))))

    val responseJson        = fixture(
      "fixtures/open-weather.json"
    ).json
    val decodedData         = responseJson.as[WeatherData].toOption.getOrElse(Seq.empty)
    expectedDecodedData shouldBe decodedData
  }
}
