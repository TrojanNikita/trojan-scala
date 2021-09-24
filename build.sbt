import com.typesafe.sbt.SbtNativePackager.autoImport.packageName
import sbt.project

val scalaV = "2.13.6"

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
      Dependencies.http4sServer,
      Dependencies.http4sClient,
      Dependencies.endpointsHttp4sClient,
      Dependencies.endpointsHttp4sServer,
      Dependencies.http4sCirce,
      Dependencies.circeGeneric,
      Dependencies.circeGenericExtras,
      Dependencies.endpoints,
      Dependencies.endpointsCirce,
      Dependencies.joda
    ),
    Universal / packageName := "shared_user",
  )
  .enablePlugins(JavaAppPackaging)

lazy val user_service = project
  .in(file("user_service"))
  .settings(
    name := "user_service",
    version := "1.0",
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      Dependencies.cats,
      Dependencies.catsEffect,
      Dependencies.logback,
      Dependencies.doobie,
      Dependencies.doobieHikari,
      Dependencies.doobieQuill,
      Dependencies.doobiePostgres,
      Dependencies.http4sServer,
      Dependencies.http4sClient,
      Dependencies.pureConfig,
      Dependencies.http4sCirce,
      Dependencies.logCatsSlf4,
      Dependencies.joda
    ),
    Universal / packageName := "user_service",
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(sharedUser)

lazy val user_daemon = project
  .in(file("user_daemon"))
  .settings(
    name := "user_daemon",
    version := "1.0",
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      Dependencies.logback,
      Dependencies.cats,
      Dependencies.catsEffect,
      Dependencies.http4sServer,
      Dependencies.http4sClient,
      Dependencies.http4sCirce,
      Dependencies.logCatsSlf4,
      Dependencies.joda
    ),
    Universal / packageName := "user_daemon",
  )
  .enablePlugins(JavaAppPackaging)

//lazy val root = project.in(file("."))