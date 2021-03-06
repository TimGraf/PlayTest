name := "PlayTest"

version := "1.0"

lazy val `playtest` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc , cache , ws , specs2 % Test )

libraryDependencies ++= {
  Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"
  )
}

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

routesGenerator := InjectedRoutesGenerator

