package com.xantoria.flippy_standalone

import scala.concurrent.ExecutionContext

import akka.actor._
import akka.io.{IO => AkkaIO}
import net.liftweb.json.Formats
import org.slf4j.LoggerFactory
import spray.can.Http

import com.xantoria.flippy.api.{API => FlippyAPI}
import com.xantoria.flippy.db._
import com.xantoria.flippy.serialization.DefaultFormats
import com.xantoria.flippy_standalone.config.Config

object Main {
  private val logger = LoggerFactory.getLogger("main")

  def main(args: Array[String]): Unit = {
    val cfg = Config.parser.parse(args, Config()).get
    logger.info(s"Starting flippy service on ${cfg.interface}:${cfg.port}...")

    implicit val ec = ExecutionContext.global
    implicit val system = ActorSystem("flippy")
    implicit val formats: Formats = DefaultFormats

    // TODO: Support more than just redis
    val backend: Backend = cfg.backend match {
      case "redis" => {
        if (cfg.backendHost.isEmpty || cfg.backendPort.isEmpty) {
          throw new IllegalArgumentException("Redis requires a backend host and port")
        }

        // TODO: Allow configuring prefix
        new RedisBackend(cfg.backendHost.get, cfg.backendPort.get, "flippy")
      }
      case "mirror" => ???
      case "in-memory" => ???
    }

    val service = system.actorOf(Props(new FlippyAPI(backend)))
    AkkaIO(Http) ! Http.Bind(service, interface = cfg.interface, port = cfg.port)
  }
}
