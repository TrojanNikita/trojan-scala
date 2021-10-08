import com.typesafe.sbt.SbtNativePackager.autoImport.packageName
import sbt.project


ThisBuild / scalaVersion := "2.13.6"
ThisBuild / maintainer := "nikitatrojan@mail.ru"
ThisBuild / version := "1.0"


lazy val redis = project
  .in(file("base/redis"))
  .settings(options)
  .settings(
    name := "redis",
    libraryDependencies ++= Seq(
      Dependencies.cats,
      Dependencies.catsEffect,
      Dependencies.circe,
      Dependencies.circeGeneric,
      Dependencies.circeGenericExtras,
      Dependencies.circeParser,
      Dependencies.redisson
    ),
    Universal / packageName := "redis",
  )
  .enablePlugins(JavaAppPackaging)

lazy val utils = project
  .in(file("base/utils"))
  .settings(options)
  .settings(
    name := "utils",
    libraryDependencies ++= Seq(
      Dependencies.circeGeneric,
      Dependencies.pureConfig,
      Dependencies.circeGenericExtras
    ),
    Universal / packageName := "utils",
  )
  .enablePlugins(JavaAppPackaging)

lazy val sql = project
  .in(file("base/sql"))
  .settings(options)
  .settings(
    name := "sql",
    libraryDependencies ++= Seq(
      Dependencies.doobie,
      Dependencies.circeGeneric,
      Dependencies.circeGenericExtras
    ),
    Universal / packageName := "sql",
  )
  .enablePlugins(JavaAppPackaging)

lazy val models = project
  .in(file("base/models"))
  .settings(options)
  .settings(
    name := "models",
    libraryDependencies ++= Seq(
      Dependencies.circeGeneric,
      Dependencies.circeGenericExtras
    ),
    Universal / packageName := "models",
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(redis)

lazy val rpc = project
  .in(file("base/rpc"))
  .settings(options)
  .settings(
    name := "rpc",
    libraryDependencies ++= Seq(
      Dependencies.endpointsCirce
    ),
    Universal / packageName := "rpc",
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(models)

lazy val user_service = project
  .in(file("user_service"))
  .settings(options)
  .settings(
    name := "user_service",
    libraryDependencies ++= Seq(
      Dependencies.endpointsHttp4sClient,
      Dependencies.endpointsHttp4sServer,
      Dependencies.doobie,
      Dependencies.doobiePostgres,
      Dependencies.doobieHikari,
      Dependencies.http4sServer,
      Dependencies.http4sClient,
      Dependencies.http4sCirce,
      Dependencies.http4sS,
      Dependencies.logCatsSlf4,
      Dependencies.joda
    ),
    Universal / packageName := "user_service",
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(models, rpc, sql, utils)


lazy val socket_service = project
  .in(file("socket_service"))
  .settings(options)
  .settings(
    name := "socket_service",
    libraryDependencies ++= Seq(
      Dependencies.endpointsHttp4sClient,
      Dependencies.endpointsHttp4sServer,
      Dependencies.doobie,
      Dependencies.doobiePostgres,
      Dependencies.http4sServer,
      Dependencies.http4sClient,
      Dependencies.http4sCirce,
      Dependencies.http4sS,
      Dependencies.logCatsSlf4,
      Dependencies.joda
    ),
    Universal / packageName := "socket_service",
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(models, rpc, utils)

lazy val user_daemon = project
  .in(file("user_daemon"))
  .settings(options)
  .settings(
    name := "user_daemon",
    libraryDependencies ++= Seq(
      Dependencies.endpointsHttp4sClient,
      Dependencies.endpointsHttp4sServer,
      Dependencies.http4sServer,
      Dependencies.http4sClient,
      Dependencies.http4sCirce,
      Dependencies.logCatsSlf4,
      Dependencies.joda
    ),
    Universal / packageName := "user_daemon",
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(models, rpc, utils)



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