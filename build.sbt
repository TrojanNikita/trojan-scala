import com.typesafe.sbt.SbtNativePackager.autoImport.packageName
import sbt.project

val scalaV = "2.12.2"

lazy val sharedUser = project
  .in(file("shared/user"))
  .settings(
    name := "shared_user",
    version := "1.0",
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      Dependencies.cats,
      Dependencies.catsEffect,
      Dependencies.logback,
      Dependencies.diStage,
      Dependencies.diStage,
      Dependencies.http4sServer,
      Dependencies.http4sClient,
      Dependencies.http4sDsl,
      Dependencies.http4s,
      Dependencies.endpointsHttp4sClient,
      Dependencies.endpointsHttp4sServer,
      Dependencies.http4sCirce,
      Dependencies.logbackGelf,
      Dependencies.circeGeneric,
      Dependencies.circeGenericExtras,
      Dependencies.endpoints,
      Dependencies.endpointsCirce,
      Dependencies.joda
    ),
    Universal / packageName := "shared_user",
  )
  .enablePlugins(JavaAppPackaging)

lazy val service1 = project
  .in(file("service1"))
  .settings(
    name := "service1",
    version := "1.0",
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      Dependencies.cats,
      Dependencies.catsEffect,
      Dependencies.logback,
      Dependencies.http4sServer,
      Dependencies.http4sClient,
      Dependencies.http4sDsl,
      Dependencies.pureConfig,
      Dependencies.http4s,
      Dependencies.http4sCirce,
      Dependencies.logbackGelf,
      Dependencies.joda
    ),
    Universal / packageName := "service1",
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(sharedUser)

lazy val daemon1 = project
  .in(file("daemon1"))
  .settings(
    name := "daemon1",
    version := "1.0",
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      Dependencies.cats,
      Dependencies.catsEffect,
      Dependencies.diStage,
      Dependencies.diStage,
      Dependencies.http4sServer,
      Dependencies.http4sClient,
      Dependencies.http4sDsl,
      Dependencies.http4s,
      Dependencies.http4sCirce,
      Dependencies.joda
    ),
    Universal / packageName := "daemon1",
  )
  .enablePlugins(JavaAppPackaging)

//lazy val root = project.in(file("."))