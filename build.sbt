name := "rice-with-redis"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.2"

ivyValidate := false

libraryDependencies ++= Seq(
                     "org.kuali.rice" % "rice-core-api" % "2.3.1" exclude("commons-transaction", "commons-transaction"), 
                     "org.kuali.rice" % "rice-core-framework" % "2.3.1",
                     "org.kuali.rice" % "rice-core-impl" % "2.3.1",
                     "org.kuali.rice" % "rice-core-web" % "2.3.1",
                     "org.kuali.rice" % "rice-core-service-api" % "2.3.1",
                     "org.kuali.rice" % "rice-core-service-framework" % "2.3.1",
                     "org.kuali.rice" % "rice-impl" % "2.3.1",
                     "org.kuali.rice" % "rice-core-service-impl" % "2.3.1",
                     "org.kuali.rice" % "rice-core-service-web" % "2.3.1",
                     "org.kuali.rice" % "rice-edl-framework" % "2.3.1",
                     "org.kuali.rice" % "rice-edl-impl" % "2.3.1",
                     "org.kuali.rice" % "rice-ken-api" % "2.3.1",
                     "org.kuali.rice" % "rice-kim-api" % "2.3.1",
                     "org.kuali.rice" % "rice-kim-framework" % "2.3.1",
                     "org.kuali.rice" % "rice-krad-app-framework" % "2.3.1",
                     "org.kuali.rice" % "rice-krad-web-framework" % "2.3.1",
                     "org.kuali.rice" % "rice-krms-api" % "2.3.1",
                     "org.kuali.rice" % "rice-krms-impl" % "2.3.1",
                     "org.kuali.rice" % "rice-ksb-api" % "2.3.1",
                     "org.kuali.rice" % "rice-ksb-client-impl" % "2.3.1",
                     "org.kuali.rice" % "rice-ksb-server-impl" % "2.3.1",
                     "org.kuali.rice" % "rice-location-api" % "2.3.1",
                     "org.kuali.rice" % "rice-location-framework" % "2.3.1",
                     "org.kuali.rice" % "rice-location-impl" % "2.3.1",
                     "org.kuali.rice" % "rice-location-web" % "2.3.1",
                     "org.kuali.rice" % "rice-web" % "2.3.1",
                     "commons-transaction" % "commons-transaction" % "1.2"
)

// resolvers += "Kuali Nexus Repositories" at "http://nexus.kuali.org/content/groups/public"
