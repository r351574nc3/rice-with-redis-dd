name := "rice-with-redis-dd-web"

organization := "org.kualigan.rr"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.1"

seq(webSettings :_*)

libraryDependencies += "org.mortbay.jetty" % "jetty" % "6.1.22" % "container"

libraryDependencies += "javax.servlet" % "servlet-api" % "2.5" % "provided"
