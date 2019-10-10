package com.stockmarket.StockValueTests

import java.util

import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import com.stockmarket.StockValue.Trends.MarketTrendModel
import com.stockmarket.StockValue.Trends.SectorTrendModel
import com.stockmarket.StockValue.Trends.RandomTrendModel
import com.stockmarket.System.StopSystemAfterAll
import com.stockmarket.models.{CONSUMER_SERVICES, FINANCE, MANUFACTURING, TECHNOLOGY}
import org.scalatest.{Matchers, WordSpecLike}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

class TestMethods extends TestKit(ActorSystem("testPlayer")) with WordSpecLike
  with Matchers
  with ImplicitSender
  with DefaultTimeout with StopSystemAfterAll {


  var map : Map[String,List[String]] = Map()


  map += (FINANCE.toString() -> getStocksToList(FINANCE))
  map += (TECHNOLOGY.toString() -> getStocksToList(TECHNOLOGY))
  map += (CONSUMER_SERVICES.toString() -> getStocksToList(CONSUMER_SERVICES))
  map += (MANUFACTURING.toString() -> getStocksToList(MANUFACTURING))

 var jMap : java.util.Map[String,java.util.List[String]] = new util.HashMap[String,java.util.List[String]]()
  map.foreach(m =>{
    jMap += (m._1 -> ListBuffer(m._2: _*))
  })

    var x  = RandomTrendModel.getMockStockValueList(jMap,20)
    var z  = MarketTrendModel.updateMarketTrendArray(20)
    var y  = SectorTrendModel.getMockStockValueList(jMap,20)





  def getStocksToList(enumeration: Enumeration): List[String] ={
    var l  = new ListBuffer[String]()
    enumeration.values.foreach(v =>{
      l += v.toString
    })

    return l.toList;
  }
}
