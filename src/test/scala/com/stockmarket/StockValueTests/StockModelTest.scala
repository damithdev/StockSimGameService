package com.stockmarket.StockValueTests

import com.stockmarket.models.{CONSUMER_SERVICES, FINANCE, MANUFACTURING, TECHNOLOGY}
import com.stockmarket.utilities.Utils
import org.scalatest.FunSuite

class StockModelTest extends FunSuite{

  test("Listing Stocks to a String map size") {
    var map: Map[String,List[String]] = Map()
    // Use this map to generate the Stock Values using the algorithm(Parameter


//    map += (TECHNOLOGY.name -> getMapFromEnum(TECHNOLOGY))
//    map += (FINANCE.name -> getMapFromEnum(FINANCE))
//    map += (CONSUMER_SERVICES.name -> getMapFromEnum(CONSUMER_SERVICES))
//    map += (MANUFACTURING.name -> getMapFromEnum(MANUFACTURING))


    println(map)
    assert(map.size == 4)

  }

  test("Mapping Stocks to a Stock Value map") {

    var map: Map[String,Map[String,Int]] = Map()
    // This is the type of map that should be returned with values (Return Type)


//    map += (TECHNOLOGY.name -> getMapFromEnum(TECHNOLOGY))
//    map += (FINANCE.name -> getMapFromEnum(TECHNOLOGY))
//    map += (CONSUMER_SERVICES.name -> getMapFromEnum(TECHNOLOGY))
//    map += (MANUFACTURING.name -> getMapFromEnum(TECHNOLOGY))
//
//    println(map)
//    assert(map.size == 4)
//
//    map.foreach((m)=>{
//      m._1 match {
//        case TECHNOLOGY.name => assert(m._2.size == 4)
//        case FINANCE.name => assert(m._2.size == 4)
//        case CONSUMER_SERVICES.name => assert(m._2.size == 4)
//        case MANUFACTURING.name => assert(m._2.size == 4)
//      }
//    })

  }

  def getMapFromEnum(enumeration: Enumeration) : Map[String,Int] ={

    var map: Map[String,Int] = Map()
    val r = scala.util.Random
    for (s <- enumeration.values) {
      map += (s.toString -> r.nextInt(10))
    }
    return map
  }

}
