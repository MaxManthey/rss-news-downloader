import com.typesafe.scalalogging.Logger
import java.io.{BufferedWriter, File, FileWriter}
import java.security.MessageDigest
import spray.json.enrichAny
import JsonNewsProtocol.jsonNewsFormat
import java.time.LocalDateTime


class PersistenceHandler {

  private val logger: Logger = Logger("PersistenceHandler Logger")
  private val newsFolderPath = "../news-files/"


  def downloadAndPersistSources(links: Seq[String]): Unit = {

    val xmlHandler = new XmlHandler

    for(link <- links) {
      xmlHandler.downloadXml(link) match {
        case Some(downloadedSource) =>
          saveSourceToFile(downloadedSource, link)
        case None => logger.error("Source will not be persisted, because fetch has failed")
      }
    }
  }


  private def saveSourceToFile(article: String, link: String): Unit = {

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
