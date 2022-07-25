package banno.open.weather.codecSpec

import io.circe.Json

import scala.io.Source
import scala.util.Try

class Fixture(filePath: String) {

  lazy val raw: String = Try(Source.fromResource(filePath).mkString)
    .recover { case e =>
      e.printStackTrace()
      readFile(filePath)
    }
    .fold(
      e => throw new RuntimeException(s"Something wrong with file '${filePath}'", e),
      identity,
    )

  lazy val json: Json =
    io.circe.parser.decode[Json](raw).getOrElse(throw new IllegalArgumentException(s"Invalid json ${filePath}"))

  def asRenderedTemplate(values: Map[String, String]): String =
    values.foldLeft(raw) { case (file, (k, v)) =>
      file.replaceAll("{{ " + k + " }}", v)
    }

  private def readFile(filePath: String): String = {
    val bufferedSource = Source.fromFile(filePath)
    try bufferedSource.getLines().mkString
    finally bufferedSource.close
  }
}

trait FixtureReader {
  def fixture(filePath: String) = new Fixture(filePath)
}
