package com.stockmarket.AnalystTests

import java.util

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit, TestProbe}
import com.stockmarket.StockValue.StockPriceList
import com.stockmarket.System.StopSystemAfterAll
import com.stockmarket.actors.middlelevel.{PlayMatch, Player}
import com.stockmarket.actors.toplevel.Game
import com.stockmarket.actors.toplevel.Game.RequestCreatedMatch
import com.stockmarket.models.{CONSUMER_SERVICES, FINANCE, MANUFACTURING, TECHNOLOGY}
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer



class AnalystTest extends TestKit(ActorSystem("testAnalyst"))
  with WordSpecLike
  with Matchers
  with ImplicitSender
  with DefaultTimeout with StopSystemAfterAll{
  "StockValueAlgorithm" must {
    "return a key value pair" in {
      val probe = TestProbe()

      var map : Map[String,List[String]] = Map()


      map += (FINANCE.toString() -> getStocksToList(FINANCE))
      map += (TECHNOLOGY.toString() -> getStocksToList(TECHNOLOGY))
      map += (CONSUMER_SERVICES.toString() -> getStocksToList(CONSUMER_SERVICES))
      map += (MANUFACTURING.toString() -> getStocksToList(MANUFACTURING))


      //      asList(ListBuffer(List(1,2,3): _*))

      var jMap : java.util.Map[String,java.util.List[String]] = new util.HashMap[String,java.util.List[String]]()
      map.foreach(m =>{
        jMap += (m._1 -> ListBuffer(m._2: _*))
      })

      //      val l: java.util.List[String] = ListBuffer(list: _*)

      var x  = StockPriceList.getMockStockValueList(jMap,20)

      var response : Map[String,Map[String,Int]] = Map()

      //      val map = ScalaToJava.getMapFromConfiguration.asInstanceOf[java.util.Map[String, String]].asScala

      //      println(x)
      val listBuff : ListBuffer[Map[String,Map[String,Int]]] = ListBuffer()
      x.forEach(m => {
        var outermap : Map[String,Map[String,Int]] = Map()
        //        println(m)
        m.entrySet().forEach(n=>{
          var innermap: Map[String,Int] = Map()
          println(n)
          n.getValue.forEach((k,v) =>{
            innermap += (k -> v)
          })
          outermap += (n.getKey -> innermap)
          //          n.ent.forEach(o =>{
          //            println(o)
          //          })

        })

        listBuff += (outermap)

      })




    }


  }

  //  public class ScalaToJava {
  //    static Map getMapFromConfiguration() {
  //      HashMap map = new HashMap();
  //      map.put("key", "value");
  //      return map;
  //    }
  //  }


  def getStocksToList(enumeration: Enumeration): List[String] ={
    var l  = new ListBuffer[String]()
    enumeration.values.foreach(v =>{
      l += v.toString
    })

    return l.toList;
  }
}


