import JsonNewsProtocol.jsonNewsFormat
import spray.json.enrichAny
import java.time.LocalDateTime
import sttp.client3._
import java.io.{BufferedWriter, File, FileWriter}
import java.security.MessageDigest
import scala.xml.XML
import de.l3s.boilerpipe.extractors._


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

  def saveSource(article: String, link: String): Unit = {

    // File name and path from hashed link
    val fileName = MessageDigest.getInstance("MD5")
      .digest(link.getBytes).map("%02x".format(_)).mkString + ".json"
    println(s"file name: $fileName, link: $link")

    //Save html file in folder "../news-files/"
    /*
    val filePath = "../news-files/" + fileName
    val newsFiles = JsonNews(article, link, LocalDateTime.now.toString).toJson.prettyPrint
    try {
      val file = new File(filePath)
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write(newsFiles)
      bw.close()
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    }
//    */

    //Save filtered string in folder "../news-json"
    /*
    val filePath = "../news-json/" + fileName
    val processedString = htmlToContentString(article)
    val newsJson = JsonNews(processedString, link, LocalDateTime.now.toString).toJson.prettyPrint
    try {
      val file = new File(filePath)
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write(newsJson)
      bw.close()
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    }
//    */
  }

  def htmlToContentString(htlmlArticle: String): String = {

    val canolaEx = CanolaExtractor.INSTANCE.getText(htlmlArticle)
    val filteredText = canolaEx.split('\n').mkString("", " ", "").split(" ")
      .map(el => {
        if(el.nonEmpty && Stopwort.miscList.contains(el(el.length-1))) el.dropRight(1)
        else el
      })
      .map(el => if(el.nonEmpty && Stopwort.miscList.contains(el.head)) el.drop(1) else el)
      .filter(el => el.nonEmpty && !Stopwort.stopwortList.contains(el.toLowerCase)).mkString("", " ", "")
    filteredText
  }
}
