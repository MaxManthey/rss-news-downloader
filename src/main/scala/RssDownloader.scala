import JsonNewsProtocol.jsonNewsFormat
import com.typesafe.scalalogging.Logger
import spray.json.enrichAny
import java.time.LocalDateTime
import sttp.client3._
import java.io.{BufferedWriter, File, FileWriter}
import java.security.MessageDigest
import scala.xml.XML


object RssDownloader {
  val logger: Logger = Logger("RSS Logger")

  //TODO create continuous loop
  def main(args: Array[String]): Unit = {

    val backend = HttpClientSyncBackend()

    val googleRssNews = "https://news.google.com/rss?hl=de&gl=DE&ceid=DE:de"
    val response = getXml(googleRssNews, backend)

    val newsLinks: Seq[String] = response match {
      case Some(value) => getLinks(value)
      case None => Seq()
    }

    if(newsLinks.nonEmpty) {
      persistSources(newsLinks, backend)
    }
  }


  def getXml(uri: String, backend: SttpBackend[Identity, Any]): Option[String] = {

    val response:  Option[String] = try {
      val getRequest = basicRequest.get(uri"$uri").send(backend).body
      getRequest match {
        case Right(value) => Some(value)
        case Left(value) =>
          logger.error("Fetching XML has failed: " + value)
          None
      }
    } catch {
      case e: Exception =>
        logger.error("Exception trying to fetch xml")
        e.printStackTrace()
        None
      case _ =>
        logger.error("Unknown error trying to fetch xml")
        None
    }
    response
  }


  def getLinks(response: String): Seq[String] = {

    val xml = XML.loadString(response)
    val linkNodes = (xml \\ "item" \ "link")
    val newsLinks = for {
      t <- linkNodes
    } yield t.text

    newsLinks
  }


  def persistSources(links: Seq[String], backend: SttpBackend[Identity, Any]): Unit = {

    for(link <- links) {
      getXml(link, backend) match {
        case Some(downloadedSource) =>
          saveSource(downloadedSource, link)
        case None => logger.error("Source will not be persisted, because fetch has failed")
      }
    }
  }


  def saveSource(article: String, link: String): Unit = {

    // File name and path from hashed link
    val fileName = MessageDigest.getInstance("MD5")
      .digest(link.getBytes).map("%02x".format(_)).mkString + ".json"
    val filePath = "../news-files/" + fileName

    //Save html file in folder "../news-files/"
    try {
      val newsFiles = JsonNews(article, link, LocalDateTime.now.toString).toJson.prettyPrint
      val file = new File(filePath)
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write(newsFiles)
      bw.close()
      logger.info(s"Successfully saved file: $fileName, with link: $link")
    } catch {
      case ex: Exception =>
        logger.error(s"Failed to save file: $fileName, with link: $link")
        ex.printStackTrace()
    }
  }
}
