package com.stockmarket.routes

import akka.actor.ActorRef

class StreamApi {
  var sourceMap:Map[String,ActorRef] = Map()

  def pushMessege(player: String,msg: String): Unit ={
    try{
      var ref = sourceMap(player)
      ref ! msg
    }catch {
      case  x: Throwable => println(x.getMessage)
    }

  }

}


//trait StreamTrait {
//
//  def pushMessege(player: String,msg: String)
//  def addToSourceMap(key: String, ref: ActorRef)
//
//}