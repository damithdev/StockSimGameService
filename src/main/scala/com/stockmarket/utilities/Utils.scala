package com.stockmarket.utilities

import com.stockmarket.StockValue.StockPriceList
import com.stockmarket.models.{CONSUMER_SERVICES, FINANCE, MANUFACTURING, TECHNOLOGY}

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._



object Utils {

//  def getListFromEnum(sector: String, enumeration: Enumeration) : List[String] ={
//
//    val list = new ListBuffer[String]()
//    for (s <- enumeration.values) {
//      list += s.toString
//    }
//    return list.toList
//  }


  def getStockMarketValues(): List[Map[String,Map[String,Int]]] ={
    var map : Map[String,List[String]] = Map()
    map = generateStockMarketStocks()

    var jMap : java.util.Map[String,java.util.List[String]] = new java.util.HashMap[String,java.util.List[String]]()
    map.foreach(m =>{
      jMap += (m._1 -> ListBuffer(m._2: _*))
    })

    var x  = StockPriceList.getMockStockValueList(jMap,20)

//    var response : Map[String,Map[String,Int]] = Map()

    val listBuff : ListBuffer[Map[String,Map[String,Int]]] = ListBuffer()
    x.forEach(m => {
      var outermap : Map[String,Map[String,Int]] = Map()
      //        println(m)
      m.entrySet().forEach(n=>{
        var innermap: Map[String,Int] = Map()
//        println(n)
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


    return listBuff.toList

  }

  def generateStockMarketStocks(): Map[String,List[String]] ={
    var map : Map[String,List[String]] = Map()


    map += (FINANCE.toString() -> getStocksToList(FINANCE))
    map += (TECHNOLOGY.toString() -> getStocksToList(TECHNOLOGY))
    map += (CONSUMER_SERVICES.toString() -> getStocksToList(CONSUMER_SERVICES))
    map += (MANUFACTURING.toString() -> getStocksToList(MANUFACTURING))

    return map
  }

  def getStocksToList(enumeration: Enumeration): List[String] ={
    var l  = new ListBuffer[String]()
    enumeration.values.foreach(v =>{
      l += v.toString
    })

    return l.toList;
  }


  def GetSectorStyle(sector: String): String ={

    sector match {
      case "FINANCE" => return FINANCE.style
      case "TECHNOLOGY" => return TECHNOLOGY.style
      case "CONSUMER_SERVICES" => return CONSUMER_SERVICES.style
      case "MANUFACTURING" => return MANUFACTURING.style
    }
  }


  def GetEnumNamefromName(string: String): String ={
    string match {
      case FINANCE.name => return FINANCE.id
      case TECHNOLOGY.name => return TECHNOLOGY.id
      case CONSUMER_SERVICES.name => return CONSUMER_SERVICES.id
      case MANUFACTURING.name => return MANUFACTURING.id
    }
  }

  def GetNamefromEnumName(string: String):String={
    string match {
      case FINANCE.id => return FINANCE.name
      case TECHNOLOGY.id => return TECHNOLOGY.name
      case CONSUMER_SERVICES.id => return CONSUMER_SERVICES.name
      case MANUFACTURING.id => return MANUFACTURING.name
    }
  }
}
