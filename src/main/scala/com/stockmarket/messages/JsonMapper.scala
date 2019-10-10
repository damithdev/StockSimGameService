package com.stockmarket.messages

import com.google.gson.{Gson, JsonElement, JsonObject, JsonParser}
import com.stockmarket.StockValue.StockPriceList
import com.stockmarket.utilities.Utils
import net.liftweb.json._
import play.api.libs.json.Json

import scala.collection.mutable.ListBuffer


//case class Bar(xs: Vector[String]) extends JsonMapper
//case class Qux(i: Int, d: Option[Double]) extends JsonMapper
case class StatusResponse(status: String)
case class UserResponse(status: String,fullName: String,username:String)
case class GameCreatedResponse(status: String, matchid: String)
case class PlayerJoinedResponse(status: String, user: String, gameHashKey: String, playerCount : String, gameName: String, players: List[String],hoster: String)
case class EventTimeoutResponse(status: String, matchid: String, owner: String, turn: String)
case class MatchListData(gamehash: String, owner: String)
case class TurnResponse(status: String, turn: String, shareMarket: List[StockDataResponse], prediction: List[PredictionData])
case class StockDataResponse(sectorName: String, style: String, previewData: String, data: List[StockData])
case class StockData(stockName: String, stocksQty: Int, ratePerQty: Int)
case class PlayerAssetData(status: String,map : Map[String,Map[String,IsSell]], wallet: Int)
case class PredictionData(sector: String, stock: String)
case class IsSell(stock:String,qty:Int,sell: Int, rate: Int)
case class WinnerData(status:String, data: String)

object JsonMapper{
  val gson = new Gson()

  def getStatusResponse(str: String): String ={
    val foo = gson.toJsonTree(StatusResponse(str))
    return foo.toString()
  }

  def getUserResponse(status:String,fullname: String,username:String): String ={
    val foo = gson.toJsonTree(UserResponse(status,fullname,username))
    return foo.toString()
  }

  def getPlayerJoinedResponse(status: String, user: String, matchid: String, count: String, owner: String,names: List[String],hoster: String): String ={
    implicit val playerJoinedResponse = Json.format[PlayerJoinedResponse]
    val foo = Json.toJson(PlayerJoinedResponse(status,user,matchid,count,owner,names,hoster))
    return foo.toString()
  }

  def getEventTimeoutResponse( status: String, matchid: String, owner: String, turn: String): String ={
    val foo = gson.toJsonTree(EventTimeoutResponse(status,matchid,owner,turn))
    return foo.toString()
  }

  def getMatchStartedResponse(status: String, user: String, matchid: String, count: String, owner: String,names: List[String],hoster: String): String ={
    implicit val playerJoinedResponse = Json.format[PlayerJoinedResponse]
    val foo = Json.toJson(PlayerJoinedResponse(status,user,matchid,count,owner,names,hoster))
    return foo.toString()
  }

  def parseUserResponse(json: String): Boolean ={
    val jValue = parse(json)
    val status = jValue.children.head.values

    if(status == "0x7000"){
      return true
    }else{
      return false
    }
  }

  def getGameCreatedResponse(status: String, matchid: String): String ={
    val foo = gson.toJsonTree(GameCreatedResponse(status,matchid))
    return foo.toString()
  }


  def getGameListJson(map : Map[String,String]): String ={

    implicit val matchListFormat = Json.format[MatchListData]

    var listBuffer : ListBuffer[MatchListData] = ListBuffer()

    map.foreach(u =>{
      listBuffer += MatchListData(u._1,u._2)
    })

    val list : List[MatchListData] = listBuffer.toList
    val users = Json.obj("games" -> list)


    return Json.stringify(users);
//    return gson.toJson(list.toList)
  }

  def GetTurnMarketValues(map : Map[String,Map[String,StockData]],turn: Int, predictions: List[PredictionData]): String ={
    implicit val isSellData = Json.format[IsSell]

    implicit val stockdata = Json.format[StockData]
    implicit val stockdataresponse = Json.format[StockDataResponse]
    implicit val predictionDataImp = Json.format[PredictionData]
    implicit val trunResponse = Json.format[TurnResponse]


    val stockdataResponseList : ListBuffer[StockDataResponse] = ListBuffer()

    map.foreach(m =>{
      var stockdataList : ListBuffer[StockData] = ListBuffer()

      m._2.foreach(n=>{
        stockdataList += n._2
      })

      stockdataResponseList += StockDataResponse(Utils.GetNamefromEnumName(m._1),Utils.GetSectorStyle(m._1),null,stockdataList.toList)
    })

    val turnResponseObj : TurnResponse = TurnResponse("0x7002",turn.toString,stockdataResponseList.toList,predictions)

    val turnResJson = Json.toJson(turnResponseObj)






    return Json.stringify(turnResJson)
  }


  def getPlayerAssetData(map : Map[String,Map[String,IsSell]], wallet: Int): String ={
    implicit val isSellData = Json.format[IsSell]
    implicit val playerAssetData = Json.format[PlayerAssetData]
    val assetData : PlayerAssetData = PlayerAssetData("0x7003",map,wallet)

    val assetResJson = Json.toJson(assetData)

    return  Json.stringify(assetResJson)

  }

  def getJsonFromStringIntMap(map: Map[String,Int]): String ={
    implicit val winnerData = Json.format[WinnerData]

    val jsonMap = Json.toJson(map)
    val winnders = Json.toJson(WinnerData("0x7700",Json.stringify(jsonMap)))
    return Json.stringify(winnders)
  }




//  def StockMarketValues(): String ={
////    val x = StockPriceList.getMockStockValueList();
//  }




}