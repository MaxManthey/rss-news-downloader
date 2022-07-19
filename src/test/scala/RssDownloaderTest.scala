import org.scalatest.funsuite.AnyFunSuite

class RssDownloaderTest extends AnyFunSuite {

  test("Correct XML being downloaded") {

    //TODO mock download
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
    val cernDownload = new XmlHandler().downloadXml("http://info.cern.ch/") match {
      case Some(value) => value
      case None => "Failed"
    }
    assert(cernXML.split("\n") sameElements cernDownload.split("\n"))
  }


  test("Correct amount of links") {

    //TODO mock download
    val googleRssNews = "https://news.google.com/rss?hl=de&gl=DE&ceid=DE:de"
    val googleResponse = new XmlHandler().downloadXml(googleRssNews)

    val newsLinks: Seq[String] = googleResponse match {
      case Some(value) => new XmlHandler().getLinksFromRssFeed(value)
      case None => Seq()
    }

    assert(newsLinks.length === 34)
  }
}