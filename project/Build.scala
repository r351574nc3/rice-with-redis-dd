
import sbt._

object RiceWithRedis extends Build {

  lazy val root = 
    Project("rice-with-redis-dd", file(".")).aggregate(app, web, it)

  lazy val app = 
    Project("rice-with-redis-dd-app", file("app")) 

  lazy val it = Project("rice-with-redis-dd-it", file("it")).dependsOn(app)

    //  lazy val web = Project("", file("web")).dependsOn(app)
 lazy val web = Project("rice-with-redis-dd-web", file("web"), settings = Project.defaultSettings ++ com.earldouglas.xsbtwebplugin.WebPlugin.webSettings).dependsOn(app)
}
