import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "TAPtoBUYsrv"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "postgresql" % "postgresql" % "9.1-901.jdbc4",
    "org.json"%"org.json"%"chargebee-1.0",
    javaCore,
    javaJdbc,
    javaEbean
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
