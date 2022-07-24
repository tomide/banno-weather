val http4sVersion = "0.21.1"
val circeVersion = "0.13.0"
val scalaTestVersion = "3.2.12"
val circeConfigVersion = "0.8.0"
val catsCore = "2.8.0"
val pureConfig = "0.14.1"
val log4catsVersion = "1.1.1"
val retry = "2.1.1"
val logback = "1.2.7"
val logbackJson = "0.1.5"
val logbackJackson = "0.1.5"
val jacksonCore = "2.13.3"
val enumCore = "1.6.1"

lazy val root = (project in file("."))
  .settings(
    organization := "jack-henry",
    name := "banno-weather",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.3",
    scalacOptions += "-deprecation",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-dropwizard-metrics" % http4sVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-generic-extras" % circeVersion,
      "io.circe" %% "circe-config" % circeConfigVersion,
      "org.typelevel" %% "cats-core" % catsCore,
      "com.github.pureconfig" %% "pureconfig" % pureConfig,
      "com.github.pureconfig" %% "pureconfig-http4s" % pureConfig,
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
      "com.github.cb372" %% "cats-retry" % retry,
      "org.typelevel" %% "cats-core" % catsCore,
      "io.chrisdavenport" %% "log4cats-core" % log4catsVersion,
      "io.chrisdavenport" %% "log4cats-slf4j" % log4catsVersion,
      "ch.qos.logback" % "logback-classic" % logback,
      "ch.qos.logback.contrib" % "logback-json-classic" % logbackJson,
      "ch.qos.logback.contrib" % "logback-jackson" % logbackJackson,
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonCore,
      "com.beachape" %% "enumeratum" % enumCore,
      "com.beachape" %% "enumeratum-circe" % enumCore,
      "com.olegpy" %% "meow-mtl-core" % "0.5.0"
    ),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(cacheOutput = false)
assemblyJarName in assembly := "messageApiService.jar"

cancelable in Global := true
fork in Global := true
