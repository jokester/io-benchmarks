// val scala2Version = "2.13.13"
val scala3Version = "3.4.2" // to use fs2-compress

addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.13.3" cross CrossVersion.full
)
addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

lazy val jvmDemo = project
  .in(file("scala-multi"))
  .settings(
    mainClass := None, // let native-packager create script for each entrypoint
    organization := "io.jokester",
    maintainer := "me@jokester.io",
    name := "scala-io-benchmark",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.12.0",
      "org.typelevel" %% "cats-effect" % "3.5.4",
      "co.fs2" %% "fs2-core" % "3.10.2",
      "co.fs2" %% "fs2-io" % "3.10.2",
      "org.apache.opendal" % "opendal-java" % "0.45.2"
    ),
    graalVMNativeImageCommand := "/usr/lib/jvm/java-11-graalvm/bin/native-image",
//    graalVMNativeImageOptions:= Seq("FS2wc")
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(GraalVMNativeImagePlugin)

// this lets sbt-run handle Ctrl-C
Global / cancelable := true
Global / fork := true
