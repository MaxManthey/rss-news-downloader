import com.typesafe.scalalogging.Logger


object RssDownloader {
  private val logger: Logger = Logger("RssDownloader Logger")
  private val xmlHandler = new XmlHandler


  def main(args: Array[String]): Unit = {
    if(args.length != 2) {
      println("Amount of args incorrect. For more details, please refer to the readme.")
      logger.error("Amount of args incorrect. For more details, please refer to the readme.")
      sys.exit(1)
    }

    val rssText = xmlHandler.downloadXml(args(0))

    val newsLinks: Seq[String] = rssText match {
      case Some(value) => xmlHandler.getLinksFromRssFeed(value)
      case None => Seq()
    }

    if(newsLinks.nonEmpty) {
      PersistenceHandler(args(1)).downloadAndPersistSources(newsLinks)
    } else {
      logger.error("No links could be extracted from RSS feed")
    }
  }
}
