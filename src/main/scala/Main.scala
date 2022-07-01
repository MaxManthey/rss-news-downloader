import JsonNewsProtocol.jsonNewsFormat
import spray.json.enrichAny
import java.time.LocalDateTime
import sttp.client3._
import java.io.{BufferedWriter, File, FileWriter}
import java.security.MessageDigest
import scala.xml.XML


object Main {

  def main(args: Array[String]): Unit = {

    val backend = HttpClientSyncBackend()

    val googleRssNews = "https://news.google.com/rss?hl=de&gl=DE&ceid=DE:de"

    val response = getXml(googleRssNews, backend)

    val newsLinks: Option[Seq[String]] = response match {
      case Some(value) => Some(getLinks(value))
      case None =>
        println("Fetching the google news feed has failed.")
        None
    }

    newsLinks match {
      case Some(links) => persistSources(links, backend)
    }

  }


  def getXml(uri: String, backend: SttpBackend[Identity, Any]): Option[String] = {

    val response:  Option[String] = try {
      val getRequest = basicRequest.get(uri"$uri").send(backend).body
      getRequest match {
        case Right(value) => Some(value)
        case Left(value) =>
          println("Fetch failed: " + value)
          None
      }
    } catch {
      case e: Exception =>
        println("Exception trying to reach fetch xml: " + e)
        None
      case _ =>
        println("Unknown error trying to fetch xml")
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

    println("Amount of links: " + links.length)
    for(link <- links) {
      getXml(link, backend) match {
        case Some(downloadedSource) =>
          saveSource(downloadedSource, link)
        case None => println("Fetch failed")
      }
    }
  }

  def saveSource(article: String, link: String) = {

    val jsonNews = JsonNews(article, link, LocalDateTime.now.toString).toJson.prettyPrint

    // File name and path from hashed link
    val fileName = MessageDigest.getInstance("MD5")
      .digest(link.getBytes).map("%02x".format(_)).mkString + ".json"
    println(s"file name: $fileName")
    val filePath = "../news-files/" + fileName

    try {
      val file = new File(filePath)
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write(jsonNews)
      bw.close()
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    }
  }
}
