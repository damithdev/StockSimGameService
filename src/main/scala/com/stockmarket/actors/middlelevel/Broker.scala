package com.stockmarket.actors.middlelevel

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.stockmarket.actors.middlelevel.Broker._
import com.stockmarket.actors.middlelevel.Market.GetStockMarketValues
import com.stockmarket.actors.toplevel.Game.{PurchaseMarketStock, SellToMarketStock}

object Broker {

  def props(matchId: String) = Props(new Broker(matchId))

//  case class Add(stocks: Vector[Stock]) // message to add stocks to the StockBroker
//  case class Buy(stocks: Int) // message to buy stocks from the StockBroker
//  case class Sell(stocks: Vector[Stock]) // message to buy stocks from the StockBroker
//  case class Stock(id: Int) // A stock
//  case class Stocks(stockitem: String,
//                    entries: Vector[Stock] = Vector.empty[Stock]) // a list of stocks for an stockitem
//  case object GetStockItem // a message containing the remaining stocks for an stockitem
//  case object Cancel // a message to cancel the stockitem
}

class Broker(matchId: String) extends Actor with ActorLogging{

  override def preStart(): Unit = {
    log.info("Broker Actor for match {} started",matchId)
  }

  override def postStop(): Unit = {
    log.info("Broker Actor for match {} stopped",matchId)
  }

  val marketRef : ActorRef = context.actorOf(Market.props())


  override def receive: Receive = {
    case GetStockMarketValues(turn: Int) =>
      marketRef.forward(GetStockMarketValues(turn: Int))


    case PurchaseMarketStock(username,hash,sector,stock,qty,turn) =>
      marketRef.forward(PurchaseMarketStock(username,hash,sector,stock,qty,turn))

    case SellToMarketStock(username,hash,sector,stock,qty,turn) =>
      marketRef.forward(PurchaseMarketStock(username,hash,sector,stock,qty,turn))


  }

//  var totalStocks = Vector.empty[Stock]
//  //  list of stocks
//  var stocks = Vector.empty[Stock]
//
//  def receive: PartialFunction[Any, Unit] = {
//    // Adds the new stocks to the existing list of stocks when Stocks message is received
//    case Add(newStocks) ⇒
//      stocks = stocks ++ newStocks
//      totalStocks = stocks
//
//    case Buy(numberOfStocks) ⇒
//      // Takes a number of stocks off the list
//      val entries = stocks.takeRight(numberOfStocks)
//
//      if (entries.size >= numberOfStocks) {
//        // if there are enough stocks available, responds with a Stocks message containing the stocks
//        sender() ! Stocks(stockitem, entries)
//        stocks = stocks.dropRight(numberOfStocks)
//        //   otherwise respond with an empty Stocks message
//      } else sender() ! Stocks(stockitem)
//
//    case Sell(sellingstocks) ⇒
//      // Takes a number of stocks off the list
////      val totalEntries = totalStocks.take(sellingstocks.size)
//
//      val tempstock = stocks ++ sellingstocks
//
//      if (totalStocks.size >= tempstock.size) {
//        stocks = stocks ++ sellingstocks
//        val entries = stocks.takeRight(sellingstocks.size)
//        // if there are enough stocks available, responds with a Stocks message containing the stocks
//        sender() ! Stocks(stockitem, entries)
//
//        //   otherwise respond with an empty Stocks message
//      } else sender() ! Stocks(stockitem)
//
//
//    // returns an StockItem containing the number of stocks left when GetStockItem is received
//    case GetStockItem ⇒ sender() ! Some(Market.StockItem(stockitem, stocks.size))
//
//    case Cancel ⇒ sender() ! Some(Market.StockItem(stockitem, stocks.size))
//      self ! PoisonPill
//  }
}
