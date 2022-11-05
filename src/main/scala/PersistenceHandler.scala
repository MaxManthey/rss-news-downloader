import com.typesafe.scalalogging.Logger
import java.io.{BufferedWriter, File, FileWriter}
import java.security.MessageDigest
import spray.json.enrichAny
import JsonNewsProtocol.jsonNewsFormat
import java.time.LocalDateTime


case class PersistenceHandler(newsFilesFolderPath: String) {
  private val logger: Logger = Logger("PersistenceHandler Logger")
  private val fileEnding = ".json"
  private val existingFiles = existingSources()


  def downloadAndPersistSources(links: Seq[String]): Unit = {
    val xmlHandler = new XmlHandler

    existingSources()
    for(link <- links) {
      val filePath = getFilePath(link)

      if(!existingFiles.contains(filePath))
        xmlHandler.downloadXml(link) match {
          case Some(downloadedSource) => saveSourceToFile(downloadedSource, filePath, link)
          case None => logger.error("Source will not be persisted, because fetch has failed")
        }
    }
  }


  private def existingSources(): Seq[String] =
    new File(newsFilesFolderPath)
      .listFiles
      .filter(_.isFile)
      .filter(_.getName.endsWith(".json"))
      .map(file => file.toString)


  private def getFilePath(link: String): String = {
    // File name and path from hashed link
    val fileName = MessageDigest.getInstance("MD5")
      .digest(link.getBytes).map("%02x".format(_)).mkString

    newsFilesFolderPath + fileName + fileEnding
  }


  private def saveSourceToFile(article: String, filePath: String, link: String): Unit = {


    //Save html file in provided folder
    try {
      val newsJson = News(article, link, LocalDateTime.now.toString).toJson.prettyPrint
      val jsonFile = new File(filePath)
      val bw = new BufferedWriter(new FileWriter(jsonFile))
      bw.write(newsJson)
      bw.close()
      logger.info(s"Successfully saved file: $filePath, with link: $link")
    } catch {
      case ex: Exception =>
        logger.error(s"Failed to save file: $filePath, with link: $link")
        ex.printStackTrace()
    }
  }
}
