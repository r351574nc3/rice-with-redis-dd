
import sbt._

object RiceWithRedis extends Build {

  lazy val root = 
    project.in(file(".")).aggregate(app, web, it)

  lazy val app = 
    project.in(file("app")) 

  lazy val it = 
    project.in(file("it")).dependsOn(app)

  lazy val web = 
    project.in(file("web")).dependsOn(app)

}
