ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "rss-news-downloader"
  )

libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.6.2"
libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0"
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.6"
libraryDependencies += "de.l3s.boilerpipe" % "boilerpipe" % "1.1.0"
//libraryDependencies += "xerces" % "dom3-xml-apis" % "1.0"
//libraryDependencies += "xerces" % "xmlParserAPIs" % "2.6.2"
//libraryDependencies += "xerces" % "xerces" % "2.4.0"
//libraryDependencies += "xerces" % "xercesSamples" % "2.8.0"
//libraryDependencies += "xerces" % "xercesImpl" % "2.9.1"
