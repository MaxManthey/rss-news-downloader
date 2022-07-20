import com.typesafe.scalalogging.Logger
import sttp.client3.{HttpClientSyncBackend, UriContext, basicRequest}
import scala.xml.XML


class XmlHandler {

  private val logger: Logger = Logger("XmlHandler Logger")
  private val backend = HttpClientSyncBackend()


  def getLinksFromRssFeed(rssText: String): Seq[String] =
    (XML.loadString(rssText) \\ "item" \ "link").map(link => link.text)


  def downloadXml(uri: String): Option[String] = {

    val xmlResponse: Option[String] = try {
      basicRequest.get(uri"$uri").send(backend).body match {
        case Right(value) => Some(value)
        case Left(value) =>
          logger.error("Fetching XML has failed: " + value)
          None
      }
    } catch {
      case e: Exception =>
        logger.error("Exception trying to fetch xml with uri: " + uri)
        logger.error(e.toString)
        e.printStackTrace()
        None
      case _ =>
        logger.error("Unknown error trying to fetch xml with uri: " + uri)
        None
    }
    xmlResponse
  }
}
