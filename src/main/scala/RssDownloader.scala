import com.typesafe.scalalogging.Logger


object RssDownloader {

  private val logger: Logger = Logger("RssDownloader Logger")
  private val rssGoogleNews = "https://news.google.com/rss?hl=de&gl=DE&ceid=DE:de"
  private val xmlHandler = new XmlHandler
  private val persistenceHandler = new PersistenceHandler


  def main(args: Array[String]): Unit = {

    val rssText = xmlHandler.downloadXml(rssGoogleNews)

    val newsLinks: Seq[String] = rssText match {
      case Some(value) => xmlHandler.getLinksFromRssFeed(value)
      case None => Seq()
    }

    if(newsLinks.nonEmpty) {
      persistenceHandler.downloadAndPersistSources(newsLinks)
    } else {
      logger.error("No links could be extracted from RSS feed")
    }
  }
}
