import com.typesafe.scalalogging.Logger


object RssDownloader {
  private val logger: Logger = Logger("RssDownloader Logger")
  private val xmlHandler = new XmlHandler


  def main(args: Array[String]): Unit = {
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
