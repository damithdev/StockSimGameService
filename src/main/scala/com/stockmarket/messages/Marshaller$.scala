package com.stockmarket.messages

import com.stockmarket.actors.middlelevel.Broker
import de.heikoseeberger.akkahttpplayjson._
import play.api.libs.json._

// message containing the initial number of stocks for the stockitem
case class StockItemDescription(stocks: Int) {
  require(stocks > 0)
}

// message containing the required number of stocks
case class StockRequest(stocks: Int) {
  require(stocks > 0)
}

// message containing the required number of stocks
case class UserCreateRequest(username: String,password:String,fullName:String) {
  require(username!=null && password != null && fullName != null)
}

// message containing the required number of stocks
case class UserRequest(username: String,password:String) {
  require(username!=null && password != null)
}

case class UserNameUpdateRequest(username: String,password:String,fullName:String) {
  require(username!=null && password != null && fullName != null )
}

case class UserPasswordUpdateRequest(username: String,password:String,newPassword:String) {
  require(username!=null && password != null && newPassword != null )
}

// message containing the required number of stocks
case class GameRequest(username: String,password:String) {
  require(username!=null && password != null)
}

case class JoinRequest(username: String,hash:String) {
  require(username!=null && hash != null)
}

case class PurchaseMarket(username:String,hash:String,sector:String,stockName:String,qty:String,turn:String, status: String, cps: String, total: String, income: String) {
  require(username!=null && hash != null && sector != null && stockName != null &&  qty.toInt > 0 )
}

case class SellMarket(username:String,hash:String,sector:String,stockName:String,qty:String,turn:String) {
  require(username!=null && hash != null && sector != null && stockName != null &&  qty.toInt > 0 )
}

// message containing the required number of stocks
case class GameJoin(gameid: String, gamekey: String) {
  require(gameid!=null)
}

// message containing an error
case class Error(message: String)

// convert our case classes from and to JSON
trait Marshaller$$ extends PlayJsonSupport {

  implicit val purchaseFromMarketMarsh: OFormat[PurchaseMarket] = Json.format[PurchaseMarket]
  implicit val stockitemDescriptionFormat: OFormat[StockItemDescription] = Json.format[StockItemDescription]
  implicit val stockRequests: OFormat[StockRequest] = Json.format[StockRequest]
  implicit val gameRequests: OFormat[GameRequest] = Json.format[GameRequest]
  implicit val joinRequests: OFormat[JoinRequest] = Json.format[JoinRequest]
  implicit val userRequests: OFormat[UserRequest] = Json.format[UserRequest]
  implicit val userCreateRequests: OFormat[UserCreateRequest] = Json.format[UserCreateRequest]
  implicit val userNameUpdateRequests: OFormat[UserNameUpdateRequest] = Json.format[UserNameUpdateRequest]
  implicit val userPasswordUpdateRequests: OFormat[UserPasswordUpdateRequest] = Json.format[UserPasswordUpdateRequest]
  implicit val errorFormat: OFormat[Error] = Json.format[Error]
  implicit val sellToMarketMarsh: OFormat[SellMarket] = Json.format[SellMarket]
//  implicit val stockitemFormat: OFormat[StockItem] = Json.format[StockItem]
//  implicit val stockitemsFormat: OFormat[StockItems] = Json.format[StockItems]
//  implicit val stockFormat: OFormat[Broker.Stock] = Json.format[Broker.Stock]
//  implicit val stocksFormat: OFormat[Broker.Stocks] = Json.format[Broker.Stocks]
}

object Marshaller$$ extends Marshaller$$
