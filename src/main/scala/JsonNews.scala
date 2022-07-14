import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import java.time.LocalDateTime

case class JsonNews(article: String, source: String, dateTime: LocalDateTime) {

  def convertToJsonFormat(): JsonNewsFormat =
    JsonNewsFormat(this.article, this.source, this.dateTime.toString)
}

private case class JsonNewsFormat(article: String, source: String, dateTime: String)

object JsonNewsProtocol extends DefaultJsonProtocol {
  implicit val jsonNewsFormat: RootJsonFormat[JsonNewsFormat] = jsonFormat3(JsonNewsFormat)
}
