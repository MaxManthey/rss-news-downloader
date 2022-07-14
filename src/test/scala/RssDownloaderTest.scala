import org.scalatest.funsuite.AnyFunSuite
import sttp.client3.{HttpClientSyncBackend, Identity, SttpBackend}

class RssDownloaderTest extends AnyFunSuite {

  val backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()

  test("Correct XML being downloaded") {
    val cernXML = """<html><head></head><body><header>
                    |<title>http://info.cern.ch</title>
                    |</header>
                    |
                    |<h1>http://info.cern.ch - home of the first website</h1>
                    |<p>From here you can:</p>
                    |<ul>
                    |<li><a href="http://info.cern.ch/hypertext/WWW/TheProject.html">Browse the first website</a></li>
                    |<li><a href="http://line-mode.cern.ch/www/hypertext/WWW/TheProject.html">Browse the first website using the line-mode browser simulator</a></li>
                    |<li><a href="http://home.web.cern.ch/topics/birth-web">Learn about the birth of the web</a></li>
                    |<li><a href="http://home.web.cern.ch/about">Learn about CERN, the physics laboratory where the web was born</a></li>
                    |</ul>
                    |</body></html>""".stripMargin
    val cernDownload = RssDownloader.getXml("http://info.cern.ch/", backend) match {
      case Some(value) => value
      case None => "Failed"
    }
    assert(cernXML.split("\n") sameElements cernDownload.split("\n"))
  }


  test("Correct amount of links") {
    val googleRssNews = "https://news.google.com/rss?hl=de&gl=DE&ceid=DE:de"
    val googleResponse = RssDownloader.getXml(googleRssNews, backend)

    val newsLinks: Seq[String] = googleResponse match {
      case Some(value) => RssDownloader.getLinks(value)
      case None => Seq()
    }

    assert(newsLinks.length === 34)
  }
}
