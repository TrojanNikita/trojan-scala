import sbt._

object Dependencies {

  object Versions {
    val catsV         = "3.2.8"
    val circeV        = "0.14.1"
    val doobieV       = "1.0.0-M5"
    val endpointsV    = "1.5.0"
    val http4sV       = "0.23.3"
    val pureConfigV   = "0.16.0"
  }

  import Versions._

  val cats                  = "org.typelevel"              %% "cats-core"                     % "2.6.1"
  val catsEffect            = "org.typelevel"              %% "cats-effect"                   % "3.2.8"
  val circe                 = "io.circe"                   %% "circe-core"                    % circeV
  val circeGeneric          = "io.circe"                   %% "circe-generic"                 % circeV
  val circeGenericExtras    = "io.circe"                   %% "circe-generic-extras"          % circeV

  val fs2 = "co.fs2" %% "fs2-core" % "3.1.2"

  val doobie                = "org.tpolecat"               %% "doobie-core"                   % doobieV
  val doobieHikari          = "org.tpolecat"               %% "doobie-hikari"                 % doobieV
  val doobieQuill           = "org.tpolecat"               %% "doobie-quill"                  % doobieV
  val doobiePostgres        = "org.tpolecat"               %% "doobie-postgres"               % doobieV

  val endpoints             = "org.endpoints4s"            %% "algebra"                       % endpointsV
  val endpointsCirce        = "org.endpoints4s"            %% "algebra-circe"                 % endpointsV
  val endpointsHttp4sClient = "org.endpoints4s"            %% "http4s-client"                 % "5.0.0"
  val endpointsHttp4sServer = "org.endpoints4s"            %% "http4s-server"                 % "7.0.0"

  val enumeratum            = "com.beachape"               %% "enumeratum"                    % "1.6.1"
  val enumeratumCirce       = "com.beachape"               %% "enumeratum-circe"              % "1.6.1"
  val enumeratumDoobie      = "com.beachape"               %% "enumeratum-doobie"             % "1.6.0"

  val http4sCirce           = "org.http4s"                 %% "http4s-circe"                  % http4sV
  val http4sClient          = "org.http4s"                 %% "http4s-blaze-client"           % http4sV
  val http4sServer          = "org.http4s"                 %% "http4s-blaze-server"           % http4sV

  val logback               = "ch.qos.logback"              % "logback-classic"               % "1.2.3"
  val logbackGelf           = "biz.paluch.logging"          % "logstash-gelf"                 % "1.14.1"

  val logCatsSlf4           = "org.typelevel"              %% "log4cats-slf4j"                % "2.1.1"

  val joda                  = "com.github.nscala-time"     %% "nscala-time"                   % "2.22.0"

  val pureConfig            = "com.github.pureconfig"      %% "pureconfig"                    % pureConfigV
  val pureConfigCats        = "com.github.pureconfig"      %% "pureconfig-cats-effect"        % pureConfigV
}