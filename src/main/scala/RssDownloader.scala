import com.typesafe.scalalogging.Logger
import spray.json.enrichAny
import JsonNewsProtocol.jsonNewsFormat
import java.time.LocalDateTime
import sttp.client3._
import java.io.{BufferedWriter, File, FileWriter}
import java.security.MessageDigest
import scala.xml.XML


object RssDownloader {

  private val logger: Logger = Logger("RSS Logger")
  private val backend = HttpClientSyncBackend()
  private val googleRssNews = "https://news.google.com/rss?hl=de&gl=DE&ceid=DE:de"
  private val newsFolderPath = "../news-files/"

  def main(args: Array[String]): Unit = {

    val rssText = getXml(googleRssNews)

    val newsLinks: Seq[String] = rssText match {
      case Some(value) => getLinks(value)
      case None => Seq()
    }

    if(newsLinks.nonEmpty) {
      persistSources(newsLinks)
    }
  }


  def getXml(uri: String): Option[String] = {

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
        logger.error("Exception trying to fetch xml with uri: " + uri)
        logger.error(e.toString)
        e.printStackTrace()
        None
      case _ =>
        logger.error("Unknown error trying to fetch xml with uri: " + uri)
        None
    }
    response
  }


  def getLinks(rssText: String): Seq[String] =
    (XML.loadString(rssText) \\ "item" \ "link").map(link => link.text)


  def persistSources(links: Seq[String]): Unit = {

    for(link <- links) {
      getXml(link) match {
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
    val filePath = newsFolderPath + fileName

    //Save html file in folder "../news-files/"
    try {
      val newsFiles = News(article, link, LocalDateTime.now.toString).toJson.prettyPrint
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
