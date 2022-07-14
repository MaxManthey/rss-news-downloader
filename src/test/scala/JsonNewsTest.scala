import org.scalatest.funsuite.AnyFunSuite

class JsonNewsTest extends AnyFunSuite {

  test("Correct JSON Object") {
    val test = JsonNews("article", "source", "date")
    println(test)
    println(test.convertToJsonString())
  }
}
