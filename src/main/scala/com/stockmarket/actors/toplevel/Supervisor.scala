package com.stockmarket.actors.toplevel

import akka.actor.{Actor, ActorLogging, Props}
import com.stockmarket.actors.toplevel.Supervisor.Begin

object Supervisor {
  def props(): Props = Props(new Supervisor)

  case class Begin()
}

class Supervisor extends Actor with ActorLogging{

  override def preStart(): Unit = {
    super.preStart()
    log.info("Market Application Started")
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info("Market Application Stopped")
  }

  override def receive = {
    case Begin =>
      log.info("Creating game actor for gameplay")
      val gameActor = context.actorOf(Game.props(),"thegame")
      context.watch(gameActor)
  }
}
