import JsonNewsProtocol.{jsonFormat4, jsonNewsFormat}
import spray.json.{DefaultJsonProtocol, RootJsonFormat, enrichAny}

import java.time.LocalDateTime
//import spray.json.DefaultJsonProtocol._
import sttp.client3._
import scala.xml.XML


object Main {

  def main(args: Array[String]): Unit = {

    val backend = HttpClientSyncBackend()

    val response = getXml("https://news.google.com/rss?hl=de&gl=DE&ceid=DE:de", backend)
    println(response)

    val newsLinks: Option[List[String]] = response match {
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


  def getLinks(response: String): List[String] = {

    val xml = XML.loadString(response)
    val linkNodes = (xml \\ "item" \ "link")
    val newsLinks = for {
      t <- linkNodes
    } yield t.text
//    newsLinks.foreach(println)

    newsLinks.toList
  }


  def persistSources(links: List[String], backend: SttpBackend[Identity, Any]): Unit = {

    println("Amount of links: " + links.length)
    var count = 1
    for(link <- links) {
      getXml(link, backend) match {
        case Some(downloadedSource) =>
          saveSource(downloadedSource, link, count)
          count += 1
        case None => println("Fetch failed")
      }
    }
  }

  def saveSource(article: String, link: String, count: Int) = {

    //TODO remove link and count
    println(s"Saving source $count: $link")
    //TODO persist data source
//    val json = source.toJson
    val news = JsonNews(article, link, LocalDateTime.now.toString)

    println(news.toJson)
  }
}
