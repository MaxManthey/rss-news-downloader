import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class JsonNews(article: String, source: String, date: String)

object JsonNewsProtocol extends DefaultJsonProtocol {
  implicit val jsonNewsFormat: RootJsonFormat[JsonNews] = jsonFormat3(JsonNews)
}
