package com.stockmarket.ActorSystemTests

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import com.stockmarket.System.StopSystemAfterAll
import com.stockmarket.actors.middlelevel.Broker._
import com.stockmarket.actors.middlelevel.Market
import com.stockmarket.actors.middlelevel.Market._
import org.scalatest.{MustMatchers, WordSpecLike}

class MarketTest extends TestKit(ActorSystem("testBoxOffice"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with DefaultTimeout
  with StopSystemAfterAll {
  "Market" must {

    "Create an stock item and get stocks from the correct Stock Broker" in {

      val stockmarket = system.actorOf(Market.props)
      val itemName = "Yahoo"
//      stockmarket ! CreateStockItem(itemName, 10)
//      expectMsg(ItemCreated(StockItem(itemName, 10)))
//
//      stockmarket ! GetStockItems
//      expectMsg(StockItems(Vector(StockItem(itemName, 10))))
//
//      stockmarket ! Market.GetStockItem(itemName)
//      expectMsg(Some(StockItem(itemName, 10)))
////
//      stockmarket ! GetStocks(itemName, 1)
//      expectMsg(Stocks(itemName, Vector(Stock(10))))
//
//      stockmarket ! GetStocks("Google", 1)
//      expectMsg(Stocks("Google"))
    }

    "Create a child actor when an stock item is created and sends it a Stocks message" in {
      val stockmarket = system.actorOf(Props(
        new Market  {
//          override def createStockBroker(name: String): ActorRef = testActor
        }
      )
      )

      val tickets = 3
      val itemName = "Yahoo"
//      val expectedTickets = (1 to tickets).map(Stock).toVector
//      stockmarket ! CreateStockItem(itemName, tickets)
//      expectMsg(Add(expectedTickets))
//      expectMsg(ItemCreated(StockItem(itemName, tickets)))
    }

    "Get and cancel an stock item that is not created yet" in {
      val stockmarket = system.actorOf(Market.props)
      val noneExitEventName = "noExitEvent"
//      stockmarket ! Market.GetStockItem(noneExitEventName)
//      expectMsg(None)
//
//      stockmarket ! CancelStockItem(noneExitEventName)
//      expectMsg(None)
    }

    "Cancel a Stock which StockItem is not created " in {
      val stockmarket = system.actorOf(Market.props)
      val noneExitStockName = "noExitStock"

//      stockmarket ! CancelStockItem(noneExitStockName)
//      expectMsg(None)
    }

    "Cancel a Stock which Stock Item is created" in {
      val stockmarket = system.actorOf(Market.props)
      val itemName = "Yahoo"
//      val stocks = 10
//      stockmarket ! CreateStockItem(itemName, stocks)
//      expectMsg(ItemCreated(StockItem(itemName, stocks)))
//
//      stockmarket ! CancelStockItem(itemName)
//      expectMsg(Some(StockItem(itemName, stocks)))
    }
    "Purchase System Stocks" in {
//      val stockmarket = system.actorOf(Market.props)
//      val itemName = "Yahoo"
      //      val stocks = 10
      //      stockmarket ! CreateStockItem(itemName, stocks)
      //      expectMsg(ItemCreated(StockItem(itemName, stocks)))
      //
      //      stockmarket ! CancelStockItem(itemName)
      //      expectMsg(Some(StockItem(itemName, stocks)))
      
    }
  }


}
