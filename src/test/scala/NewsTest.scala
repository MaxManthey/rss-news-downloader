import org.scalatest.funsuite.AnyFunSuite
import spray.json.enrichAny
import JsonNewsProtocol.jsonNewsFormat
import java.time.LocalDateTime

class NewsTest extends AnyFunSuite {

  test("Matching JSON Object") {
    val currentTime = LocalDateTime.now.toString
    val result = s"""{
                   |  "article": "article",
                   |  "dateTime": "$currentTime",
                   |  "source": "source"
                   |}""".stripMargin
    val jsonNews = News("article", "source", currentTime).toJson.prettyPrint

    assert(jsonNews === result)
  }


  test("JSON Object not matching") {
    val result = s"""{
                    |  "article": "news",
                    |  "dateTime": "2022-07-15T00:05:02.118687",
                    |  "source": "link"
                    |}""".stripMargin
    val jsonNews = News("article", "source", LocalDateTime.now().toString).toJson.prettyPrint

    assert(jsonNews != result)
  }
}
