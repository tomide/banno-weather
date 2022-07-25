package banno.client

import banno.app.errors.AppError
import banno.app.errors.ErrorType.{IllegalInput, NoAccess, Unexpected}
import banno.model.{OpenWeatherError, WeatherData}

object OpenWeatherOps {

  implicit class OpenWeatherErrorResponseOps(val error: OpenWeatherError) {
    def toAppError: AppError = {
      error.code match {
        case c if c == 400 => AppError("bad request", IllegalInput)
        case c if c == 401 => AppError("server_error", Unexpected)
        case c if c == 404 => AppError("request limit exceeded", NoAccess)
        case c if c >= 500 => AppError("server_error", Unexpected)
      }
    }
  }
}
