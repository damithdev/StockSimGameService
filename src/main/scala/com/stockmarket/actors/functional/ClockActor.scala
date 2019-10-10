package com.stockmarket.actors.functional


import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.stockmarket.actors.middlelevel.PlayMatch.TurnOver


//val Tick = "tick"

object ClockActor{
  def props(parent: ActorRef): Props = Props(new ClockActor(parent))

  final object Tick
  final object UpdateTick
}

class ClockActor(parent : ActorRef) extends Actor with ActorLogging{
  import com.stockmarket.actors.functional.ClockActor._
  var turn : Int = 0

  def receive = {
    case Tick => //Do something
      turn += 1
//      if(turn >= 20){
        parent.tell(TurnOver(turn),ActorRef.noSender)
//      }else{
//        parent.tell(TurnOver(turn),ActorRef.noSender)
//      }

    case UpdateTick =>
      parent.tell(TurnOver(turn),ActorRef.noSender)

  }
}
