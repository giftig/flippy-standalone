package com.xantoria.flippy_standalone

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import akka.actor._
import akka.io.{IO => AkkaIO}
import akka.pattern.ask
import akka.util.Timeout
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
    implicit val formats: Formats = DefaultFormats
    implicit val timeout = Timeout(5.seconds)

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

    implicit val system = ActorSystem("flippy")
    val service = system.actorOf(Props(new FlippyAPI(backend)))
    val bindResult = AkkaIO(Http) ? Http.Bind(service, interface = cfg.interface, port = cfg.port)
    bindResult foreach {
      case failure: Http.CommandFailed => {
        logger.error("Failed to bind to the interface! Shutting down...")
        logger.error(s"Details: $failure")
        System.exit(1)
      }
      case _ => ()
    }
  }
}
