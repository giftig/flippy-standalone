package com.xantoria.flippy_standalone.config

import scopt.OptionParser

case class Config(
  interface: String = "0.0.0.0",
  port: Int = 80,
  backend: String = "redis",
  backendHost: Option[String] = None,
  backendPort: Option[Int] = None
)

object Config {
  private val supportedBackends = List("redis", "mirror", "in-memory")

  val parser = new OptionParser[Config]("flippy") {
    head("flippy", "0.1.2")

    opt[String]('i', "interface").action {
      (v, c) => c.copy(interface = v)
    }.text("Interface (host) on which to serve API. Default 0.0.0.0")

    opt[Int]('p', "port").action {
      (v, c) => c.copy(port = v)
    }.text("Port on which to serve API. Default 80")

    opt[String]('b', "backend").action {
      (v, c) => c.copy(backend = v)
    }.validate {
      v => if (supportedBackends.contains(v)) {
        success
      } else {
        failure(s"Backend $v is not supported")
      }
    }.text("Database backend to use. Default is redis.")

    opt[String]("backend-host").action {
      (v, c) => c.copy(backendHost = Some(v))
    }.text("Host where the backend runs, if applicable")

    opt[Int]("backend-port").action {
      (v, c) => c.copy(backendPort = Some(v))
    }.text("Port on which the backend runs, if applicable")
  }
}
