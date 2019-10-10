package com.stockmarket.actors.middlelevel

import java.util.{Timer, TimerTask}
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, PoisonPill, Props, Scheduler, Terminated}
import com.stockmarket.actors.functional.ClockActor
import com.stockmarket.actors.functional.ClockActor.{Tick, UpdateTick}
import com.stockmarket.actors.middlelevel.Market.GetStockMarketValues
import com.stockmarket.actors.middlelevel.PlayMatch.{FinalizeGame, GetGameOwner, MatchResults, ReplyPlayerList, RequestPlayerList, ResendPlayerJoined, SendStockMarketValues, TurnOver}
import com.stockmarket.actors.middlelevel.Player.{GetStream, SendResponse}
import com.stockmarket.actors.toplevel.Game.{PlayerJoined, PlayerPurchasedMarketStock, PlayerSoldMarketStock, PurchaseMarketStock, RequestTrackPlayer, SellToMarketStock, StartGame}
import com.stockmarket.db.DatabaseHandler
import com.stockmarket.messages.JsonMapper

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._


//import com.stockmarket.routes.{StreamApi, StreamTrait}

object PlayMatch{
  def props(matchId: String): Props = Props(new PlayMatch(matchId))

  final case class RequestPlayerList(requestId: Long)
  final case class TurnOver(turn:Int)
  final case class ReplyPlayerList(requestId: Long, ids: Set[String])

  final case class SendStockMarketValues(values : String)
  final case class GetGameOwner()
  final case class FinalizeGame()
  final case class MatchResults(player: String, score: Int)
  final case class ResendPlayerJoined(status: String, user: String, matchid: String, count: String, owner: String,names: List[String],hoster: String)



}

class PlayMatch(matchId: String) extends Actor with ActorLogging{


  var playerIdToActor = Map.empty[String,ActorRef]
  var actorToPlayerId = Map.empty[ActorRef,String]
  var matchOwner : String  = null
  var matchOwnerName: String = null
  var results: Map[String,Int] = Map()

  val tickActor = context.actorOf(ClockActor.props(context.self))
  val brokerRef = context.actorOf(Broker.props(matchId))

  val timer = new Timer
  def delay(f: () => Unit, n: Long) = timer.schedule(new TimerTask() { def run = f() }, n)


//  def scheduler: Scheduler
  val system = akka.actor.ActorSystem("stockmarket-system")

  import system.dispatcher
  var cancellable : Cancellable= null

//  var turnCount: Int = 0

//  val ex = new ScheduledThreadPoolExecutor(1)
//  val task = new Runnable {
//
//    def run() = {
//      turnCount += 1
//      val msg : String = JsonMapper.getEventTimeoutResponse("0x7100",matchId,matchOwner,turnCount.toString);
//      playerIdToActor.foreach(map =>{
//
//        val actor = map._2
//        actor.forward(SendResponse(msg))
//        //        context.watch(actor)
//        //        pushMessege(map._1,msg)
//      }
//      )
//
//      if(turnCount >= 30){
//
//        turnCount = 0
////        f.cancel(true)
//      }
//    }
//  }



//  val f = ex.scheduleAtFixedRate(task, 1, 30, TimeUnit.SECONDS)
//  f.cancel(false)

//  val broker : ActorRef = context.actorOf()

  override def preStart(): Unit = log.info("Match {} started",matchId)

  override def postStop(): Unit = log.info("Match {} stopped", matchId)

  override def receive: Receive = {
    case trackMsg @ RequestTrackPlayer(`matchId`, _) =>
      playerIdToActor.get(trackMsg.playerId) match {
        case Some(playerActor) =>
          playerActor.forward(trackMsg)
        case None =>
          if(actorToPlayerId.size >= 4){
            sender() ! JsonMapper.getStatusResponse("0x9000")
          }else{
            log.info("Creating player actor for {}", trackMsg.playerId)
            val playerActor = context.actorOf(Player.props(matchId, trackMsg.playerId), s"player-${trackMsg.playerId}")
            context.watch(playerActor)
            actorToPlayerId += playerActor -> trackMsg.playerId
            playerIdToActor += trackMsg.playerId -> playerActor
            if(matchOwner == null)matchOwner = trackMsg.playerId
            playerActor.forward(trackMsg)
          }

      }

    case RequestTrackPlayer(groupId, deviceId) =>
      log.warning("Ignoring TrackPlayer request for {}. This player is responsible for {}.", groupId, this.matchId)

    case RequestPlayerList(requestId) =>
      sender() ! ReplyPlayerList(requestId, playerIdToActor.keySet)

    case Terminated(playerActor) =>
      val playerId = actorToPlayerId(playerActor)
      log.info("Device actor for {} has been terminated", playerId)
      actorToPlayerId -= playerActor
      playerIdToActor -= playerId


    case PlayerJoined(username,matchId) =>
//      println("keys"+playerIdToActor.keys.toList)
//      println("keyset"+playerIdToActor.keySet.toList)
      var nameList :ListBuffer[String] = ListBuffer()
      playerIdToActor.foreach(
        map =>{
          var x = DatabaseHandler.getFullNameByUserName(map._1)
          if(x != null)nameList += x
        }
      )
      if(matchOwnerName == null){
        var x = DatabaseHandler.getFullNameByUserName(matchOwner)
        if(x != null)matchOwnerName = x
      }

      val msg : String = JsonMapper.getPlayerJoinedResponse("0x7001",username,matchId,playerIdToActor.size.toString,matchOwner,nameList.toList,matchOwnerName);
      playerIdToActor.foreach(map =>{

        val actor = map._2
        actor.forward(SendResponse(msg))
//        context.watch(actor)
//        pushMessege(map._1,msg)
      }
      )

      delay(() => self.tell(ResendPlayerJoined("0x7001",username,matchId,playerIdToActor.size.toString,matchOwner,nameList.toList,matchOwnerName),ActorRef.noSender), 1000L)
      delay(() => self.tell(ResendPlayerJoined("0x7001",username,matchId,playerIdToActor.size.toString,matchOwner,nameList.toList,matchOwnerName),ActorRef.noSender), 3000L)
      delay(() => self.tell(ResendPlayerJoined("0x7001",username,matchId,playerIdToActor.size.toString,matchOwner,nameList.toList,matchOwnerName),ActorRef.noSender), 5000L)
      delay(() => self.tell(ResendPlayerJoined("0x7001",username,matchId,playerIdToActor.size.toString,matchOwner,nameList.toList,matchOwnerName),ActorRef.noSender), 7000L)
      delay(() => self.tell(ResendPlayerJoined("0x7001",username,matchId,playerIdToActor.size.toString,matchOwner,nameList.toList,matchOwnerName),ActorRef.noSender), 10000L)



    case ResendPlayerJoined(status ,username,matchId,count,owner,names,hoster) =>
      //      println("keys"+playerIdToActor.keys.toList)
      //      println("keyset"+playerIdToActor.keySet.toList)
//      var nameList :ListBuffer[String] = ListBuffer()
//      playerIdToActor.foreach(
//        map =>{
//          var x = DatabaseHandler.getFullNameByUserName(map._1)
//          if(x != null)nameList += x
//        }
//      )
//      if(matchOwnerName == null){
//        var x = DatabaseHandler.getFullNameByUserName(matchOwner)
//        if(x != null)matchOwnerName = x
//      }

      val msg : String = JsonMapper.getPlayerJoinedResponse(status ,username,matchId,count,owner,names,hoster);
      playerIdToActor.foreach(map =>{

        val actor = map._2
        actor.forward(SendResponse(msg))
        //        context.watch(actor)
        //        pushMessege(map._1,msg)
      }
      )



    case StartGame(matchId,playerId) => {
      if(playerId == matchOwner){
        var ownerplayer = playerIdToActor.getOrElse(playerId,null)
        if(ownerplayer != null && playerIdToActor.size >= 1){

//          var nameList :ListBuffer[String] = ListBuffer()
//          playerIdToActor.foreach(
//            map =>{
//              var x = DatabaseHandler.getFullNameByUserName(map._1)
//              if(x != null)nameList += x
//            }
//          )
//          if(matchOwnerName == null){
//            var x = DatabaseHandler.getFullNameByUserName(matchOwner)
//            if(x != null)matchOwnerName = x
//          }

//          val msg : String = JsonMapper.getPlayerJoinedResponse("0x7001",playerId,matchId,playerIdToActor.size.toString,matchOwner,nameList.toList,matchOwnerName);
//          playerIdToActor.foreach(map =>{
//
//            val actor = map._2
//            actor.forward(SendResponse(msg))
//          })
          cancellable = system.scheduler.schedule(1 milliseconds, 30 seconds, tickActor, Tick)
          sender() ! JsonMapper.getStatusResponse("0x7000")
          brokerRef.forward(GetStockMarketValues)
        }else{
          sender() ! JsonMapper.getStatusResponse("0x9000")
        }
      }else{
        sender() ! JsonMapper.getStatusResponse("0x9000")
      }

    }

    case TurnOver(turn) => {
//      var ownerplayer = playerIdToActor.getOrElse(playerId,null)
//      if(ownerplayer != null && playerIdToActor.size >= 3){
        if(turn > 20){
          self.tell(FinalizeGame, ActorRef.noSender)
          cancellable.cancel()
          context.self ! PoisonPill
        }
        val msg : String = JsonMapper.getEventTimeoutResponse("0x7005",matchId,matchOwner,turn.toString)
        playerIdToActor.foreach(map =>{

          val actor = map._2
          actor.forward(SendResponse(msg))
        })
        brokerRef.tell(GetStockMarketValues(turn),self)
//        self ! SendStockMarketValues()

    }

    case FinalizeGame => {
      playerIdToActor.foreach(map =>{

        val actor = map._2
        actor.forward(FinalizeGame)
      })
    }



    case SendStockMarketValues(values : String) =>{
      playerIdToActor.foreach(map =>{

        val actor = map._2
        actor.forward(SendResponse(values))
      })
    }

    case GetStream(matchId,playerId) =>
      var actorRef = playerIdToActor(playerId)
      actorRef.forward(GetStream(matchId,playerId))


    case GetGameOwner() =>{
      sender() ! matchOwner
    }

    case PurchaseMarketStock(username,hash,sector,stock,qty,turn) =>
      var actorRef = playerIdToActor(username)
      if(actorRef != null){
        brokerRef.tell(PurchaseMarketStock(username,hash,sector,stock,qty,turn),self)
        sender() ! JsonMapper.getStatusResponse("0x7000")
      }else{
        sender() ! JsonMapper.getStatusResponse("0x9000")
      }

    case SellToMarketStock(username,hash,sector,stock,qty,turn) =>
      var actorRef = playerIdToActor(username)
      if(actorRef != null){
        brokerRef.tell(SellToMarketStock(username,hash,sector,stock,qty,turn),self)
        sender() ! JsonMapper.getStatusResponse("0x7000")
      }else{
        sender() ! JsonMapper.getStatusResponse("0x9000")
      }
//
    case PlayerPurchasedMarketStock(username,hash,sector,stock,qty,rate,turn) =>
      println("Player Purchased received")
      var actorRef = playerIdToActor(username)
      actorRef.tell(PlayerPurchasedMarketStock(username,hash,sector,stock,qty,rate,turn),self)
      tickActor.tell(UpdateTick,self)


    case PlayerSoldMarketStock(username,hash,sector,stock,qty,rate,turn) =>
      println("Player Sold received")
      var actorRef = playerIdToActor(username)
      actorRef.tell(PlayerSoldMarketStock(username,hash,sector,stock,qty,rate,turn),self)
      tickActor.tell(UpdateTick,self)

    case MatchResults(playerId,total)=>
      results += (playerId -> total)
      if(results.size >= playerIdToActor.size){
        playerIdToActor.foreach(map =>{

          var values = JsonMapper.getJsonFromStringIntMap(results)
          val actor = map._2
          actor.forward(SendResponse(values))
        })
      }



//



  }
}
