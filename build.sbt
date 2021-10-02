import com.typesafe.sbt.SbtNativePackager.autoImport.packageName
import sbt.project

val scalaV = "2.13.6"

lazy val common = project
  .in(file("base/common"))
  .settings(options)
  .settings(
    name := "common",
    version := "1.0",
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      Dependencies.logback,
      Dependencies.cats,
      Dependencies.catsEffect,
      Dependencies.circe,
      Dependencies.circeGeneric,
      Dependencies.circeGenericExtras,
      Dependencies.circeParser,
      Dependencies.redisson
    ),
    Universal / packageName := "common"
  )

lazy val rpc = project
  .in(file("base/rpc"))
  .settings(options)
  .settings(
    name := "rpc",
    version := "1.0",
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      Dependencies.endpointsHttp4sClient,
      Dependencies.endpointsHttp4sServer,
      Dependencies.circeGeneric,
      Dependencies.circeGenericExtras,
      Dependencies.endpoints,
      Dependencies.http4sServer,
      Dependencies.http4sClient,
      Dependencies.http4sCirce,
      Dependencies.endpointsCirce
    ),
    Universal / packageName := "rpc",
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(common)

lazy val user_service = project
  .in(file("user_service"))
  .settings(options)
  .settings(
    name := "user_service",
    version := "1.0",
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      Dependencies.doobie,
      Dependencies.doobiePostgres,
      Dependencies.http4sServer,
      Dependencies.http4sClient,
      Dependencies.pureConfig,
      Dependencies.http4sCirce,
      Dependencies.http4sS,
      Dependencies.logCatsSlf4,
      Dependencies.joda
    ),
    Universal / packageName := "user_service",
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(common, rpc)


lazy val socket_service = project
  .in(file("socket_service"))
  .settings(options)
  .settings(
    name := "socket_service",
    version := "1.0",
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      Dependencies.doobie,
      Dependencies.doobiePostgres,
      Dependencies.http4sServer,
      Dependencies.http4sClient,
      Dependencies.pureConfig,
      Dependencies.http4sCirce,
      Dependencies.http4sS,
      Dependencies.logCatsSlf4,
      Dependencies.joda
    ),
    Universal / packageName := "socket_service",
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(common, rpc)

lazy val user_daemon = project
  .in(file("user_daemon"))
  .settings(options)
  .settings(
    name := "user_daemon",
    version := "1.0",
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      Dependencies.http4sServer,
      Dependencies.http4sClient,
      Dependencies.http4sCirce,
      Dependencies.logCatsSlf4,
      Dependencies.pureConfig,
      Dependencies.joda
    ),
    Universal / packageName := "user_daemon",
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(common, rpc)



val options = scalacOptions ++= Seq(
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-language:existentials",
  "-language:postfixOps",
  "-Ywarn-unused",
  "-Ywarn-dead-code",
  "-Yrangepos",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-encoding", "utf8"
)

//lazy val root = project.in(file("."))