package com.stockmarket.actors.middlelevel

import akka.actor._
import akka.pattern.{ask, pipe}
import com.stockmarket.actors.middlelevel.Market._
import com.stockmarket.actors.middlelevel.PlayMatch.SendStockMarketValues
import com.stockmarket.actors.toplevel.Game.{PlayerPurchasedMarketStock, PlayerSoldMarketStock, PurchaseMarketStock, SellToMarketStock}
import com.stockmarket.messages.PredictionData

import scala.collection.immutable.ListMap
import scala.collection.mutable.ListBuffer
//import com.stockmarket.actors.toplevel.Game.{PlayerPurchasedMarketStock, PurchaseMarketStock}
import com.stockmarket.messages.{JsonMapper, StockData}
import com.stockmarket.utilities.Utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Market {

  def props() = Props(new Market)

  sealed trait StockResponse // message response to create an stockitem

  case class GetStockMarketValues(turn : Int)



  //
//  case class CreateStockItem(name: String, stocks: Int) // message to create an stockitem
//
//  case class GetStockItem(name: String) // message to get an stockitem
//
//  case class GetStocks(item: String, stocks: Int) // message to get stocks for an stockitem
//  case class SellStocks(item: String, stocks: Int) // message to sell stocks of an stockitem
//
//  case class CancelStockItem(name: String) // message to cancel an stockitem
//
//  case class StockItem(name: String, stocks: Int) // message describing the stockitem
//  case class StockItems(items: Vector[StockItem]) // message describing a list of stockitems
//
//  case class ItemCreated(item: StockItem) extends StockResponse // message to indicate the stockitem was created
//
//  case object GetStockItems // message to request all stocks
//
//  case object ItemExists extends StockResponse // message to indicate that the stockitem already exists




}



class Market extends Actor with ActorLogging{



  var currentStockValues : Map[String,Map[String,StockData]] = Map()
  val values : List[Map[String,Map[String,Int]]] = Utils.getStockMarketValues()


  override def preStart(): Unit = {

  }

  override def postStop(): Unit = super.postStop()

  override def receive: Receive = {
    case GetStockMarketValues(turn: Int) =>{
      sender() ! SendStockMarketValues(JsonMapper.GetTurnMarketValues(calculateCurrentTurnValues(turn),turn,getNextTurnPredictions(turn)))
    }

    case PurchaseMarketStock(username,hash,sector,stock,qt,turn) =>{
      if(currentStockValues.size == 0){
        sender() ! JsonMapper.getStatusResponse("0x9000")
      }else{
        try{
          var qty = qt.toInt
          var ratePerQ: Int = 0
          var outerMap: Map[String,Map[String,StockData]] = Map()

          var sectorD = currentStockValues(sector)
          var stockD = currentStockValues(sector)(stock)
          if(stockD.stocksQty < qty){
            throw new Exception("Invalid Quantity"){}
          }else{
//            var x : Map[String,Map[String,StockData]] = Map()


//            currentStockValues.get(sector) += (stock -> StockData(stockD.stockName,stockD.stocksQty - qty,stockD.ratePerQty))
            stockD = StockData(stockD.stockName,stockD.stocksQty - qty,stockD.ratePerQty)
            sectorD += (stock -> stockD)

            currentStockValues += (sector -> sectorD)

            println(sender().path)

//            currentStockValues(sector) += (stock -> StockData(stockD.stockName,(stockD.stocksQty -qty),stockD.ratePerQty))
            var rate : Int = stockD.ratePerQty
            sender() ! PlayerPurchasedMarketStock(username,hash,sector,stock,qt,rate.toString,turn)
//            sender() ! GetGameList
//            sender().actorRef.tell(GetStockMarketValues(4),self)
          }
//          currentStockValues.foreach(m =>{
//            var innerMap: Map[String,StockData] = Map()
//            m._2.foreach(n=>{
//              if(n._2.stocksQty < qty){
//                throw new Exception("Invalid Quantity"){}
//              }else{
//                innerMap += (n._1 -> StockData(n._1,n._2.stocksQty - qty,n._2.ratePerQty))
//                sender() ! JsonMapper.getStatusResponse("0x7000")
//              }
//
//            })
//            outerMap += (m._1 -> innerMap)
//          })
//          currentStockValues = outerMap

        }catch {
          case x: Exception => sender() ! JsonMapper.getStatusResponse("0x9000")
        }
      }
    }

    case SellToMarketStock(username,hash,sector,stock,qt,turn) =>{
      if(currentStockValues.size == 0){
        sender() ! JsonMapper.getStatusResponse("0x9000")
      }else {
        try {
          var qty = qt.toInt
          var ratePerQ: Int = 0
          var outerMap: Map[String, Map[String, StockData]] = Map()

          var sectorD = currentStockValues(sector)
          var stockD = currentStockValues(sector)(stock)

          //            var x : Map[String,Map[String,StockData]] = Map()


          //            currentStockValues.get(sector) += (stock -> StockData(stockD.stockName,stockD.stocksQty - qty,stockD.ratePerQty))
          stockD = StockData(stockD.stockName, stockD.stocksQty + qty, stockD.ratePerQty)
          sectorD += (stock -> stockD)

          currentStockValues += (sector -> sectorD)

          println(sender().path)

          //            currentStockValues(sector) += (stock -> StockData(stockD.stockName,(stockD.stocksQty -qty),stockD.ratePerQty))
          var rate: Int = stockD.ratePerQty
          sender() ! PlayerSoldMarketStock(username, hash, sector, stock, qt, rate.toString, turn)
          //            sender() ! GetGameList
          //            sender().actorRef.tell(GetStockMarketValues(4),self)

          //          currentStockValues.foreach(m =>{
          //            var innerMap: Map[String,StockData] = Map()
          //            m._2.foreach(n=>{
          //              if(n._2.stocksQty < qty){
          //                throw new Exception("Invalid Quantity"){}
          //              }else{
          //                innerMap += (n._1 -> StockData(n._1,n._2.stocksQty - qty,n._2.ratePerQty))
          //                sender() ! JsonMapper.getStatusResponse("0x7000")
          //              }
          //
          //            })
          //            outerMap += (m._1 -> innerMap)
          //          })
          //          currentStockValues = outerMap

        } catch {
          case x: Exception => sender() ! JsonMapper.getStatusResponse("0x9000")
        }
      }}
  }



    def calculateCurrentTurnValues(turn : Int): Map[String,Map[String,StockData]] ={
      var currentTurnMap = values(turn)
      if(currentStockValues.size == 0){
        var outerMap: Map[String,Map[String,StockData]] = Map()
        currentTurnMap.foreach(m =>{
          var innerMap: Map[String,StockData] = Map()
          m._2.foreach(n=>{
            innerMap += (n._1 -> StockData(n._1,1000,n._2))
          })
          outerMap += (m._1 -> innerMap)
        })
        currentStockValues = outerMap
        return currentStockValues
      }else{

        var outerMap: Map[String,Map[String,StockData]] = Map()
        currentTurnMap.foreach(m =>{
          var innerMap: Map[String,StockData] = Map()
          m._2.foreach(n=>{
            var x =  currentStockValues.get(m._1).get(n._1)
            innerMap += (n._1 -> StockData(n._1,x.stocksQty,n._2 + x.ratePerQty ))
          })
          outerMap += (m._1 -> innerMap)
        })
        currentStockValues = outerMap
        return currentStockValues

      }





    }


  def getNextTurnPredictions(turn:Int): List[PredictionData] ={
    var predictionData : ListBuffer[PredictionData] = ListBuffer()
    var diff : Map[String,Int] = Map()
    var map : Map[String, PredictionData] = Map()
    if(turn > 3){
      var last = values(turn-1)
      val current = values(turn)

      if(last!= null){
        last.foreach((u=>{
          u._2.foreach(v=>{
            var x: Int = current(u._1)(v._1)
            diff += (v._1 -> (v._2 - x))
            map += (v._1 -> PredictionData(v._1,u._1))
          })
        }))
      }

      var sorted = ListMap(diff.toSeq.sortWith(_._2 > _._2):_*)
      var firstfive = sorted.take(5)

      firstfive.foreach(m=>{
        predictionData += map(m._1)
      })



    }
      return predictionData.toList



  }


}


