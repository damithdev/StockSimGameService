package com.stockmarket.actors.middlelevel

import akka.NotUsed
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl.Source
import com.stockmarket.actors.middlelevel.PlayMatch.{FinalizeGame, MatchResults}
import com.stockmarket.actors.stream.ActorBasedSource
import com.stockmarket.actors.toplevel.Game
import com.stockmarket.actors.toplevel.Game.{PlayerPurchasedMarketStock, PlayerSoldMarketStock}
import com.stockmarket.messages.IsSell
//import com.stockmarket.actors.toplevel.Game.PlayerPurchasedMarketStock

import scala.concurrent.duration._
import com.stockmarket.messages.JsonMapper

object Player{
  def props(matchId:String,playerId:String): Props = Props(new Player(matchId,playerId))

  sealed trait Trading

  final case class BuyStock(requestId: Long, stockId: String, count: Int) extends Trading
  final case class StockBought(requestId: Long)

  final case class SellStock(requestId: Long, stockId: String, count: Int) extends Trading
  final case class StockSold(requestId:Long)
  final case class StockSellFailed(requestId:Long)

  case object PlayerNotAvailable extends Trading
  case object MoneyNotAvailable extends Trading
  case object PlayerTimedOut extends Trading


  case class SendResponse(msg: String)
  case class GetStream(matchId:String, playerId:String)

  case class SendPlayerAssetData()


}

class Player(matchId: String,playerId: String) extends Actor with ActorLogging{
  import Player._

  var map:Map[String,Int] = Map()
//  var sourceMap:Map[String,ActorRef] = Map()
  var wallet: Int = 1000
  var assets: Map[String,Map[String,IsSell]] = Map()
  var sourceActor : ActorRef= context.actorOf(Props[ActorBasedSource])

  override def preStart(): Unit ={
    log.info("Player Actor {}-{} started",matchId,playerId)
//    playerName = playerId
  }
  override def postStop(): Unit =log.info("Player Actor {}-{} stopped",matchId,playerId)

  override def receive: Receive = {
    case Game.RequestTrackPlayer(`matchId`,`playerId`) =>
      sender() ! JsonMapper.getGameCreatedResponse("0x7000",matchId)
//        sourceActor
//      sender() !

    case Game.RequestTrackPlayer(matchId,playerId) => log.warning("Ignoring TrackDevice Request for {}-{} This actor is responsible for {}-{}",matchId,playerId,this.matchId,this.playerId)

    case BuyStock(id, stock,count) =>
      log.info("Buying {} Stocks from {} stock",count,stock)
      map += (stock -> count)
      sender() ! StockBought(id)
    case SellStock(id,stock,count) =>
      log.info("Selling {} Stocks from {} stock", count,stock)
      var x:Int =  map.getOrElse(stock,0)
      if(x<count){
        map += (stock -> (x-count))
        sender() ! StockSellFailed(id)
      }else{
        map += (stock -> (x-count))
        sender() ! StockSold(id)
      }
    case GetStream(matchId,playerId) =>
      sourceActor = context.actorOf(Props[ActorBasedSource])
      sender() ! sourceActor

    case SendResponse(msg) =>

      if(sourceActor != null) sourceActor ! msg

    case PlayerPurchasedMarketStock(username,hash,sector,stock,qty,rate,turn) =>{

      if(assets.contains(sector)){
        var x :IsSell =assets(sector)(stock)
        var s = assets(sector)
        s += (stock -> IsSell(x.stock,(x.qty+ qty.toInt),x.sell,rate.toInt))
        assets += (sector -> s)
        wallet -= qty.toInt * rate.toInt
      }else{
        assets += (sector -> Map(stock -> IsSell(stock,qty.toInt,0,rate.toInt)))
        wallet -= qty.toInt * rate.toInt
      }

      self.tell(SendPlayerAssetData,ActorRef.noSender)

    }


    case PlayerSoldMarketStock(username,hash,sector,stock,qty,rate,turn) =>{

      if(assets.contains(sector)){
        var x :IsSell =assets(sector)(stock)
        var s = assets(sector)
        var l :Int= x.qty- qty.toInt
        if(l <= 0){
          s -= stock
          assets += (sector -> s)
        }else{
          s += (stock -> IsSell(x.stock,l,x.sell,rate.toInt))
          assets += (sector -> s)
        }

        wallet += qty.toInt * rate.toInt
      }

      self.tell(SendPlayerAssetData,ActorRef.noSender)

    }

    case SendPlayerAssetData =>
      var x = JsonMapper.getPlayerAssetData(assets,wallet)
      self.tell(SendResponse(x),ActorRef.noSender)


    case FinalizeGame =>
      var assetValue : Int = 0
      assets.foreach(a=>{
        a._2.foreach(u=>{
          assetValue += (u._2.qty * u._2.rate)
        })
      })

      var total = assetValue + wallet

      sender() ! MatchResults(playerId,total)
  }



  //                pushMessege("Player Joined")

}
