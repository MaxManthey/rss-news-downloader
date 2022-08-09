import com.typesafe.scalalogging.Logger
import java.io.{BufferedWriter, File, FileWriter}
import java.security.MessageDigest
import spray.json.enrichAny
import JsonNewsProtocol.jsonNewsFormat
import java.time.LocalDateTime


case class PersistenceHandler(newsFilesFolderPath: String) {
  private val logger: Logger = Logger("PersistenceHandler Logger")
  private val fileEnding = ".json"


  def downloadAndPersistSources(links: Seq[String]): Unit = {
    val xmlHandler = new XmlHandler

    for(link <- links) {
      xmlHandler.downloadXml(link) match {
        case Some(downloadedSource) => saveSourceToFile(downloadedSource, link)
        case None => logger.error("Source will not be persisted, because fetch has failed")
      }
    }
  }


  private def saveSourceToFile(article: String, link: String): Unit = {
    // File name and path from hashed link
    val fileName = MessageDigest.getInstance("MD5")
      .digest(link.getBytes).map("%02x".format(_)).mkString
    val filePath = newsFilesFolderPath + fileName + fileEnding

    //Save html file in provided folder
    try {
      val newsJson = News(article, link, LocalDateTime.now.toString).toJson.prettyPrint
      val jsonFile = new File(filePath)
      val bw = new BufferedWriter(new FileWriter(jsonFile))
      bw.write(newsJson)
      bw.close()
      logger.info(s"Successfully saved file: $fileName, with link: $link")
    } catch {
      case ex: Exception =>
        logger.error(s"Failed to save file: $fileName, with link: $link")
        ex.printStackTrace()
    }
  }
}
