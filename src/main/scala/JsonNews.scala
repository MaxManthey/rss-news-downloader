import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class JsonNews(article: String, source: String, date: String)

object JsonNewsProtocol extends DefaultJsonProtocol {
  implicit val jsonNewsFormat: RootJsonFormat[JsonNews] = jsonFormat3(JsonNews)
}

//Transforming Dates with JSON
//implicit val localDateTimeFormat: JsonFormat[LocalDateTime] =
//  new JsonFormat[LocalDateTime] {
//    override def write(obj: LocalDateTime): JsValue = JsString(obj.toString)
//
//    override def read(json: JsValue): LocalDateTime = json match {
//      case JsString(s) => Try(LocalDateTime.parse(s)) match {
//        case Success(result) => result
//        case Failure(exception) =>
//          deserializationError(s"could not parse $s as Joda LocalDateTime", exception)
//      }
//      case notAJsString =>
//        deserializationError(s"expected a String but got a $notAJsString")
//    }
//  }