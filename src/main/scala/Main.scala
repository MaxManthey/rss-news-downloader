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

//    /* Extractor experiments
//    val link = "https://news.google.com/__i/rss/rd/articles/CBMiOmh0dHBzOi8vd3d3LnRhZ2Vzc2NoYXUuZGUvd2lydHNjaGFmdC9ldS10YXhvbm9taWUtMTA3Lmh0bWzSAQA?oc=5"
//    val link = "https://news.google.com/__i/rss/rd/articles/CBMie2h0dHBzOi8vd3d3LmZhei5uZXQvYWt0dWVsbC9wb2xpdGlrL2F1c2xhbmQvYm9yaXMtam9obnNvbi1zdGltbXQtcnVlY2t0cml0dC1hbHMtYnJpdGlzY2hlci1wcmVtaWVybWluaXN0ZXItenUtMTgxNTU2OTIuaHRtbNIBfWh0dHBzOi8vbS5mYXoubmV0L2FrdHVlbGwvcG9saXRpay9hdXNsYW5kL2JvcmlzLWpvaG5zb24tc3RpbW10LXJ1ZWNrdHJpdHQtYWxzLWJyaXRpc2NoZXItcHJlbWllcm1pbmlzdGVyLXp1LTE4MTU1NjkyLmFtcC5odG1s?oc=5"
    val link = "https://news.google.com/__i/rss/rd/articles/CBMiU2h0dHBzOi8vd3d3LnN1ZWRkZXV0c2NoZS5kZS9wb2xpdGlrL2cyMC1naXBmZWwtYmFsaS1hdXNzZW5taW5pc3Rlci1sYXdyb3ctMS41NjE2MDc50gEA?oc=5"
    println(link)
    val article: String = getXml(link, backend) match {
      case Some(value) => value
      case None => "failed"
    }
    val defaultEx = DefaultExtractor.INSTANCE.getText(article)
    println("\n\nDEFAULT EXTRACTOR")
    println(defaultEx)
    val canolaEx = CanolaExtractor.INSTANCE.getText(article)
    println("\n\nCANOLA EXTRACTOR")
    println(canolaEx.split('\n').mkString("", " ", "").split(" ").filter(el => el.nonEmpty && !Stopwort.stopwortList.contains(el)).toList)
    val test = canolaEx.split('\n').mkString("", " ", "").split(" ")
    println(canolaEx.split('\n').mkString("", " ", "").split(" ").toList.length)
    val test2 = test.map(el => {
      if(el(el.length-1) == '.' || el(el.length-1) == '!' || el(el.length-1) == '?' || el(el.length-1) == ',' || el(el.length-1) == ':' || el(el.length-1) == ')') el.dropRight(1)
      else el
    }).map(el => if(el.head == '(') el.drop(1) else el).filter(el => !Stopwort.stopwortList.contains(el.toLowerCase)).toList
    println(test2)
    println(test2.length)

//    println(canolaEx.split("\n").mkString("", ", ", ")").split(" ").mkString("Array(", ", ", ")"))
//    */


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

    val jsonNews = JsonNews(article, link, LocalDateTime.now.toString).toJson.prettyPrint

    // File name and path from hashed link
    val fileName = MessageDigest.getInstance("MD5")
      .digest(link.getBytes).map("%02x".format(_)).mkString + ".json"
    println(s"file name: $fileName, link: $link")
    val filePath = "../news-files/" + fileName

    //Save file in folder "../news-files/"
    /*
    try {
      val file = new File(filePath)
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write(jsonNews)
      bw.close()
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    }
//     */

  }
}
