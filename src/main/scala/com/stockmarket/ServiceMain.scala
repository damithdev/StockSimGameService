package com.stockmarket

import java.net.InetAddress

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.stockmarket.routes.RestApi
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.{ExecutionContextExecutor, Future}
import java.net.InetAddress

import com.stockmarket.actors.toplevel.Supervisor


object ServiceMain extends App with RequestTimeout {

  // this configs are in the application.conf file
  val config = ConfigFactory.load()
  val host = config.getString("http.host") // Gets the host and a port from the configuration
  val port = config.getInt("http.port")
  val localhost: InetAddress = InetAddress.getLocalHost
  val localIpAddress: String = localhost.getHostAddress



  implicit val system: ActorSystem = ActorSystem("stockmarket-system") // ActorMaterializer requires an implicit ActorSystem
  implicit val ec: ExecutionContextExecutor = system.dispatcher // bindingFuture.map requires an implicit ExecutionContext
  implicit val materializer: ActorMaterializer = ActorMaterializer() // bindAndHandle requires an implicit materializer

  val api = new RestApi(system, requestTimeout(config)).routes // the RestApi provides a Route


  val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(api, host, port) // starts the HTTP server

  val log = Logging(system.eventStream, "stockmarket")

  try {
    //    Here we start the HTTP server and log the info
    bindingFuture.map { serverBinding ⇒
      log.info(s"RestApi bound to ${serverBinding.localAddress}")
      println(s"Server is Online at http://" + localIpAddress + ":" + port+"/")
    }

    var gamesupervisor  = system.actorOf(Supervisor.props(),"game-supervisor")

    gamesupervisor.tell(Supervisor.Begin, akka.actor.ActorRef.noSender)
  } catch {
    //    If the HTTP server fails to start, we throw an Exception and log the error and close the system
    case ex: Exception ⇒
      log.error(ex, "Failed to bind to {}:{}!", host, port)
      //      System shutdown
      system.terminate()
  }
}

trait RequestTimeout {

  import scala.concurrent.duration._

  def requestTimeout(config: Config): Timeout = {
    val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }
}



