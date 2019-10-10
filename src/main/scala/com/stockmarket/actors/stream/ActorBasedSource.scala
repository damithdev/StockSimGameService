package com.stockmarket.actors.stream

import akka.actor.Actor
import akka.stream.actor.ActorPublisher

class ActorBasedSource extends Actor with ActorPublisher[String]{
  import akka.stream.actor.ActorPublisherMessage._
  var items:List[String] = List.empty

  def receive = {
    case s:String =>
      if (totalDemand == 0)
        items = items :+ s
      else
        onNext(s)

    case Request(demand) =>
      if (demand > items.size){
        items foreach (onNext)
        items = List.empty
      }
      else{
        val (send, keep) = items.splitAt(demand.toInt)
        items = keep
        send foreach (onNext)
      }


    case other =>
      println(s"got other $other")
  }


}