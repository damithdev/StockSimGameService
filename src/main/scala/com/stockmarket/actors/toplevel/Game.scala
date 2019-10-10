package com.stockmarket.actors.toplevel

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
//import com.stockmarket.actors.middlelevel.Market.PurchaseMarketStock
import com.stockmarket.actors.middlelevel.PlayMatch
import com.stockmarket.actors.middlelevel.PlayMatch.GetGameOwner
import com.stockmarket.actors.middlelevel.Player.GetStream
import com.stockmarket.messages.JsonMapper

import scala.concurrent.duration._
import scala.concurrent.Await

object Game{
  def props(): Props = Props(new Game)

  final case class RequestTrackPlayer(matchId:String, playerId:String)
  case object PlayerRegistered

  final case class RequestCreatedMatch(matchId: String)
  final case class RequestStream(matchId: String)
  final case class PlayerJoined(username:String,matchId: String)
  final case class StartGame(matchId:String, playerId:String)
  final case class GetGameList()
  final case class PurchaseMarketStock(username:String,hash:String,sector:String,stock:String,qty:String,turn:String)
  final case class SellToMarketStock(username:String,hash:String,sector:String,stock:String,qty:String,turn:String)
  final case class PlayerPurchasedMarketStock(username:String,hash:String,sector:String,stock:String,qty:String,rate: String,turn:String)
  final case class PlayerSoldMarketStock(username:String,hash:String,sector:String,stock:String,qty:String,rate: String,turn:String)


  case object InvalidMatchId
}
class Game extends Actor with ActorLogging{
  import Game._

  var matchIdToOwner = Map.empty[String,String]
  var matchIdToActor = Map.empty[String,ActorRef]
  var actorToMatchId = Map.empty[ActorRef,String]

  override def preStart(): Unit = log.info("Game Started")
  override def postStop(): Unit = log.info("Game Stopped")

  def receive: Receive = {
    case trackMsg @ RequestTrackPlayer(matchId, _) =>
      matchIdToActor.get(matchId) match {
        case Some(ref) =>
          ref.forward((trackMsg))
        case None=>
          log.info("Creating device group actor for {}",matchId)
          val matchActor = context.actorOf(PlayMatch.props(matchId),"match-" + matchId)
          context.watch(matchActor)
          matchActor.forward(trackMsg)
          matchIdToActor += matchId -> matchActor
          actorToMatchId += matchActor -> matchId
          matchIdToOwner += matchId -> trackMsg.playerId

      }
    case Terminated(matchActor) =>{
      val matchId = actorToMatchId(matchActor)
      log.info("Match actor for {} has been terminated", matchId)
      actorToMatchId -= matchActor
      matchIdToActor -= matchId
      matchIdToOwner -= matchId
    }

    case InvalidMatchId => {
      sender() ! JsonMapper.getStatusResponse("0x9000")
    }

    case RequestCreatedMatch(matchId) =>{
      try{

        val matchRef = matchIdToActor(matchId)
        if(matchRef != null){
          sender() ! matchRef
        }else{
          sender() ! InvalidMatchId
        }

      }catch {
        case x: Exception =>
        {

          // Display this if exception is found
          log.info("Request Created Match Exception:  {}.",x )
        }
      }
    }

    case PlayerJoined(username,matchId) =>{

      val actor = matchIdToActor(matchId)
//      context.watch(actor)

      if(actor != null) actor.tell(PlayerJoined(username,matchId),ActorRef.noSender)

    }

    case StartGame(matchId,playerId) => {
      val actor = matchIdToActor.getOrElse(matchId,null)
      //      context.watch(actor)

      if(actor != null) actor.forward(StartGame(matchId,playerId))else{
        sender() ! JsonMapper.getStatusResponse("0x9000")
      }

    }

    case GetStream(matchId,playerId) =>
      var actorRef = matchIdToActor(matchId)
      actorRef.forward(GetStream(matchId,playerId))

    case GetGameList() =>{
      sender() ! JsonMapper.getGameListJson(matchIdToOwner)
//      matchIdToOwner.foreach(m =>{
//        var owner = actor.(GetGameOwner(),context.self)
//      })
//      //      context.watch(actor)

//      if(actor != null) actor.forward(StartGame(matchId,playerId))else{
//        sender() ! JsonMapper.getStatusResponse("0x9000")
//      }
    }

    case PurchaseMarketStock(username,hash,sector,stock,qty,turn) =>{
      var actorRef = matchIdToActor(hash)
      actorRef.forward(PurchaseMarketStock(username,hash,sector,stock,qty,turn))
    }

    case SellToMarketStock(username,hash,sector,stock,qty,turn) =>{
      var actorRef = matchIdToActor(hash)
      actorRef.forward(SellToMarketStock(username,hash,sector,stock,qty,turn))
    }


  }


}
