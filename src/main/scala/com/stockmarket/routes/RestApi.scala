package com.stockmarket.routes

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.util.Timeout
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import akka.NotUsed
import akka.http.scaladsl.Http
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.model.sse.ServerSentEvent

import scala.concurrent.duration._
import java.time.LocalTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME
import java.util.Calendar

import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.stream.actor.ActorPublisher
import com.stockmarket.actors.middlelevel.{Broker, Market, PlayMatch}
import com.stockmarket.actors.stream.ActorBasedSource
import com.stockmarket.actors.toplevel.Game.{GetGameList, InvalidMatchId, PlayerJoined, PurchaseMarketStock, RequestCreatedMatch, RequestTrackPlayer, SellToMarketStock, StartGame}
import com.stockmarket.actors.toplevel.{Game, Supervisor}
import com.stockmarket.db.DatabaseHandler
import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.RawHeader
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import com.stockmarket.actors.management.UserManager
import com.stockmarket.actors.management.UserManager.{AddUser, Authenticate, UpdateUserData, User}
import com.stockmarket.utilities.Utils
//import com.stockmarket.actors.middlelevel.Market.PurchaseMarketStock
import com.stockmarket.actors.middlelevel.Player.GetStream
//import com.stockmarket.actors.middlelevel.Market.{CancelStockItem, CreateStockItem, GetStockItem, GetStockItems, GetStocks, SellStocks, StockItem, StockItems, StockResponse}
import com.stockmarket.messages._

import scala.util.Random


class RestApi(system: ActorSystem, timeout: Timeout) extends RestRoutes {
  implicit val requestTimeout: Timeout = timeout

  implicit def executionContext: ExecutionContextExecutor = system.dispatcher

  def createMarket(): ActorRef = system.actorOf(Market.props)
  def createUserManager(): ActorRef = system.actorOf(UserManager.props, "user-actor")
  def createGameManager(): ActorRef = system.actorOf(Game.props())
  def createSourceManager(): ActorRef = system.actorOf(Props[ActorBasedSource])

//  var actorRef = system.actorOf(Props(ActorBasedSource))




}

trait RestRoutes extends MarketApi with Marshaller$$ {
  val service = "comp3005l"/"wolverine"/"stock-simulation"

//  private val Cors = new CORSHandler {}
  //  endpoint for creating an event with stocks
//  protected val createStockItemRoute: Route = {
//    pathPrefix(service / "stocks" / Segment) { item ⇒
//      post {
//        //    POST stockmarket/stocks/stock_item_name
//        pathEndOrSingleSlash {
//          entity(as[StockItemDescription]) { ed =>
//            onSuccess(createStockItem(item, ed.stocks)) {
//              case Market.ItemCreated(item) => complete(Created, item)
//              case Market.ItemExists =>
//                val err = Error(s"$item Stock item already exists!")
//                complete(BadRequest, err)
//            }
//          }
//        }
//      }
//    }
//  }
//  protected val getAllStockItemsRoute: Route = {
//    pathPrefix(service / "stocks") {
//      get {
//        // GET stockmarket/stocks
//        pathEndOrSingleSlash {
//          onSuccess(getStockItems()) { items ⇒
//            complete(OK, items)
//          }
//        }
//      }
//    }
//  }
//  protected val getStockItemRoute: Route = {
//    pathPrefix(service / "stocks" / Segment) { item ⇒
//      get {
//        // GET stockmarket/stocks/:stock_item
//        pathEndOrSingleSlash {
//          onSuccess(getStockItem(item)) {
//            _.fold(complete(NotFound))(e ⇒ complete(OK, e))
//          }
//        }
//      }
//    }
//  }
//  protected val deleteStockItemRoute: Route = {
//    pathPrefix(service / "stocks" / Segment) { item ⇒
//      delete {
//        // DELETE stockmarket/stocks/:stock_item
//        pathEndOrSingleSlash {
//          onSuccess(cancelStockItem(item)) {
//            _.fold(complete(NotFound))(e => complete(OK, e))
//          }
//        }
//      }
//    }
//  }
  protected val purchaseStockRoute: Route = {
    pathPrefix(service / "stocks" / Segment / "purchase") { item ⇒
      post {
        // POST stockmarket/stocks/:stock_item/purchase
        pathEndOrSingleSlash {
          entity(as[StockRequest]) { request ⇒
            complete("")
            //            onSuccess(requestStocks(item, request.stocks)) { stocks ⇒
//              if (stocks.entries.isEmpty) complete(NotFound)
//              else complete(Created, stocks)
//            }
          }
        }
      }
    }
  }

  protected val sellStockRoute: Route = {
    pathPrefix(service / "stocks" / Segment / "sell") { item ⇒
      post {
        // POST stockmarket/stocks/:stock_item/sell
        pathEndOrSingleSlash {
          entity(as[StockRequest]) { request ⇒
            complete("")
//            onSuccess(publishStocks(item, request.stocks)) { stocks ⇒
//              if (stocks.entries.isEmpty) complete(NotFound)
//              else complete(Created, stocks)
//            }
          }
        }
      }
    }
  }

  protected val createUserRoute: Route = cors()  {
    pathPrefix(service / "authentication"  / "sign-up") {
      post {
        // POST stockmarket/stocks/:stock_item/sell
        println("Sign Up Received!")
        pathEndOrSingleSlash {
          entity(as[UserCreateRequest]) { request ⇒
            onSuccess(createUser(request.username, request.password, request.fullName)) { status ⇒
              if (status == null) complete(NotFound)
              else complete(Created, status)
            }
          }
        }
      }
    }
  }

  protected val userSignInRoute: Route = cors(){
    pathPrefix(service / "authentication"  / "sign-in") {
      post {
        // POST stockmarket/stocks/:stock_item/sell
        println("Sign In Received!")

        pathEndOrSingleSlash {
          entity(as[UserRequest]) { request ⇒
            onSuccess(authenticateUser(request.username, request.password)) { status ⇒
              if (status == null) complete(NotFound)
              else complete(Created, status)
            }
          }
        }
      }
    }
  }

  protected val userNameUpdateRoute: Route = cors(){
    pathPrefix(service / "authentication"  / "manage-account-name") {
      post {
        // POST stockmarket/stocks/:stock_item/sell
        pathEndOrSingleSlash {
          entity(as[UserNameUpdateRequest]) { request ⇒
            onSuccess(updateUserFullName(request.username, request.password,request.fullName)) { status ⇒
              if (status == null) complete(NotFound)
              else complete(Created, status)
            }
          }
        }
      }
    }
  }

  protected val userPassUpdateRoute: Route = cors(){
    pathPrefix(service / "authentication"  / "manage-account-pass") {
      post {
        // POST stockmarket/stocks/:stock_item/sell
        pathEndOrSingleSlash {
          entity(as[UserPasswordUpdateRequest]) { request ⇒
            onSuccess(updateUserPassword(request.username, request.password,request.newPassword)) { status ⇒
              if (status == null) complete(NotFound)
              else complete(Created, status)
            }
          }
        }
      }
    }
  }


//  var htmlLanding = "<img style=\"display: block; margin-left: auto; margin-right: auto; width: 30%;\" src=\"https://comp3005l-stock-market-simulator.s3.us-east-2.amazonaws.com/serverrunning.gif\" alt=\"Server Running Image\" class=\"center\"><h1 style='text-align: center;margin: 0 auto;position: relative;top: 10%;-ms-transform: translateY(-50%);'>Say Hello To Akka based Stock Market Simulation Service</h1><img style=\"display: block; margin-left: auto; margin-top: 50; margin-right: auto; width: 30%;\" src=\"https://comp3005l-stock-market-simulator.s3.us-east-2.amazonaws.com/akka.png\" alt=\"Akka Image\" class=\"center\">"

  var htmlLandingNew = "<body style=\"margin: 0\"> <div style=\"background-image: url('https://comp3005l-stock-market-simulator.s3.us-east-2.amazonaws.com/server_up.jpg'); height: 100%; background-position: center;background-repeat: no-repeat;background-size: cover;\"> </div></body>";
  protected val helloFromServerRoute: Route = cors(){
    path(""){
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,htmlLandingNew))
    }
  }

  protected val serverSentTestRoute: Route = cors(){
    pathPrefix(service / "serversent"  / "events") {
      get {
        complete {
          Source
            .tick(2.seconds, 2.seconds, NotUsed)
            .map(_ => LocalTime.now())
            .map(time => ServerSentEvent(ISO_LOCAL_TIME.format(time)))
            .keepAlive(1.second, () => ServerSentEvent.heartbeat)
        }
      }
    }
  }

  protected val createGameRoute: Route = cors()  {
    pathPrefix(service / "gameplay"  / "create") {

      post {
        println("Create Game Received")
        // POST stockmarket/stocks/:stock_item/sell
        pathEndOrSingleSlash {
          entity(as[GameRequest]) { request ⇒
            onSuccess(createMatch(request.username, request.password)) { status ⇒
              println("Status " + status)
              complete{
                status
                //
                //
//                var key: String = status.keySet.head
//                var ref: ActorRef = status(key)
//                playerJoined(request.username,key)
//                //                sourceMap += (request.username -> ref)
//                var pub = ActorPublisher[String](ref)
//                def eventSource : Source[ServerSentEvent,NotUsed] = Source.fromPublisher(pub)
//                  .map(x => ServerSentEvent(x))
//                  .keepAlive(1.second, () => ServerSentEvent("alive"))
//                eventSource
              }
            }
          }
        }
      }
    }
  }

  protected val listGameRoute: Route = cors()  {
    pathPrefix(service / "gameplay"  / "list") {
      get {
        // POST stockmarket/stocks/:stock_item/sell
        pathEndOrSingleSlash {
          onSuccess(getGameList()) { status ⇒
            if (status == null) complete(NotFound)
            else complete(status)
          }
        }
      }
    }
  }



  protected val joinGameRoute: Route = cors()  {

    pathPrefix(service / "gameplay"  / "join") {
      post {
        // POST stockmarket/stocks/:stock_item/sell
        pathEndOrSingleSlash {
          entity(as[JoinRequest]) { request ⇒
            onSuccess(joinMatch(request.username, request.hash)) { status ⇒
              playerJoined(request.username,request.hash)
              complete{

                status

//
//
//                var ref: ActorRef = status
////                sourceMap += (request.username -> ref)
//                var pub = ActorPublisher[String](ref)
//                def eventSource : Source[ServerSentEvent,NotUsed] = Source.fromPublisher(pub)
//                  .map(x => ServerSentEvent(x))
//                  .keepAlive(1.second, () => ServerSentEvent("alive"))
//                eventSource
              }


            }
          }
        }
      }
    }
  }


  protected val requestStreamRoute: Route = cors()  {


    pathPrefix(service / "gameplay"  / "stream") {

      get {
        println("Stream Request Received")
        // POST stockmarket/stocks/:stock_item/sell
        pathEndOrSingleSlash {
          parameters('username.as[String], 'hash.as[String]) { (username, hash) =>
            onSuccess(requestStream(username, hash)) { status ⇒
              respondWithHeader(RawHeader("Access-Control-Allow-Origin","*")){
                complete{
                  //                playerJoined(request.username,request.hash)
                  //
                  //
                  var ref: ActorRef = status
                  //                sourceMap += (request.username -> ref)
                  var pub = ActorPublisher[String](ref)
                  def eventSource : Source[ServerSentEvent,NotUsed] = Source.fromPublisher(pub)
                    .map(x => ServerSentEvent(x))
                    .keepAlive(1.second, () => ServerSentEvent("l"))
                  eventSource
                }
              }
            }
          }
//          entity(as[JoinRequest]) { request ⇒
//            onSuccess(requestStream(request.username, request.hash)) { status ⇒
//
//              complete{
////                playerJoined(request.username,request.hash)
//                //
//                //
//                var ref: ActorRef = status
//                //                sourceMap += (request.username -> ref)
//                var pub = ActorPublisher[String](ref)
//                def eventSource : Source[ServerSentEvent,NotUsed] = Source.fromPublisher(pub)
//                  .map(x => ServerSentEvent(x))
//                  .keepAlive(1.second, () => ServerSentEvent("alive"))
//                eventSource
//              }
//
//
//            }
//          }
        }
      }
    }
  }

  protected val startGameRoute: Route = cors(){
    pathPrefix(service / "gameplay"  / "start") {
      post {
        println("start game Received!")
        // POST stockmarket/stocks/:stock_item/sell
        pathEndOrSingleSlash {
          entity(as[JoinRequest]) { request ⇒
            onSuccess(startGame(request.username, request.hash)) { status ⇒
              complete(status)
            }
          }
        }
      }
    }
  }


  protected val serverPing: Route = cors(){
    pathPrefix(service / "ping") {
      get {
        // POST stockmarket/stocks/:stock_item/sell
        pathEndOrSingleSlash {
//          entity(as[JoinRequest]) { request ⇒
//            onSuccess(startGame(request.username, request.hash)) { status ⇒
//              complete(status)
//            }
//          }
          complete{
            println("Ping Received!")
            JsonMapper.getStatusResponse("0x7000")
          }
        }
      }
    }
  }


  protected val purchaseFromMarket: Route = cors(){
    pathPrefix(service / "gameplay"  / "purchase") {
      post {
        println("PurchaseFromMarket!")
        // POST stockmarket/stocks/:stock_item/sell
        pathEndOrSingleSlash {
          entity(as[PurchaseMarket]) { request ⇒
            onSuccess(purchaseFromMarketStock(request.username, request.hash,request.sector,request.stockName,request.qty,request.turn)) { status ⇒
              complete(status)
            }
          }
        }
      }
    }
  }

  protected val sellToMarket: Route = cors(){
    pathPrefix(service / "gameplay"  / "sell") {
      post {
        println("SellToMarket!")
        // POST stockmarket/stocks/:stock_item/sell
        pathEndOrSingleSlash {
          entity(as[SellMarket]) { request ⇒
            onSuccess(purchaseFromMarketStock(request.username, request.hash,request.sector,request.stockName,request.qty,request.turn)) { status ⇒
              complete(status)
            }
          }
        }
      }
    }
  }







  val gameRequestRoutes: Route = createGameRoute ~ joinGameRoute ~ startGameRoute ~ listGameRoute ~ requestStreamRoute ~ purchaseFromMarket
  val serverSentEventRoutes: Route = serverSentTestRoute
  val userManagementRoutes: Route = userSignInRoute ~ createUserRoute ~ userNameUpdateRoute ~ userPassUpdateRoute
  var stockManagementRoutes: Route =  purchaseStockRoute ~ sellStockRoute
  val routes: Route = serverPing ~ helloFromServerRoute ~ userManagementRoutes ~ stockManagementRoutes ~ serverSentEventRoutes ~ gameRequestRoutes

}

trait MarketApi {

  lazy val market: ActorRef = createMarket()
  lazy val userManager: ActorRef = createUserManager()
  lazy val gameManager: ActorRef = createGameManager()
//  lazy val sourceManager: ActorRef = createSourceManager()





  def eventSourceOld = Source
    .tick(2.seconds, 2.seconds, NotUsed)
    .map(_ => LocalTime.now())
    .map(time => ServerSentEvent(ISO_LOCAL_TIME.format(time)))
    .keepAlive(1.second, () => ServerSentEvent.heartbeat)


  implicit def executionContext: ExecutionContext

  implicit def requestTimeout: Timeout

  def createMarket(): ActorRef
  def createUserManager(): ActorRef
  def createGameManager(): ActorRef
  def createSourceManager(): ActorRef

//  def createStockItem(stockitem: String, numberOfStocks: Int): Future[StockResponse] = {
//    market.ask(CreateStockItem(stockitem, numberOfStocks))
//      .mapTo[StockResponse]
//  }
//
//  def getStockItems(): Future[StockItems] = market.ask(GetStockItems).mapTo[StockItems]
//
//  def getStockItem(stockitem: String): Future[Option[StockItem]] = market.ask(GetStockItem(stockitem)).mapTo[Option[StockItem]]
//
//  def cancelStockItem(stockitem: String): Future[Option[StockItem]] = market.ask(CancelStockItem(stockitem)).mapTo[Option[StockItem]]
//
//  def requestStocks(stockitem: String, stocks: Int): Future[Broker.Stocks] = {
//    market.ask(GetStocks(stockitem, stocks)).mapTo[Broker.Stocks]
//  }
//
//  def publishStocks(stockitem: String, stocks: Int): Future[Broker.Stocks] = {
//    market.ask(SellStocks(stockitem, stocks)).mapTo[Broker.Stocks]
//  }

  def createUser(username: String, password: String, fullname: String): Future[String] = {
    userManager.ask(AddUser(User(username, password,fullname))).mapTo[String]
  }

  def authenticateUser(username: String, password: String): Future[String] = {
    userManager.ask(Authenticate(username, password)).mapTo[String]
  }

  def updateUserFullName(username: String, password: String,fullname: String): Future[String] = {
    userManager.ask(UpdateUserData(username, password,fullname,null)).mapTo[String]
  }

  def updateUserPassword(username: String, password: String, newpassword: String): Future[String] = {
    userManager.ask(UpdateUserData(username, password,null,newpassword)).mapTo[String]
  }

  def createMatch(username: String, password: String):Future[String]= {
//    val auth = DatabaseHandler.ValidateUser(username,password)
//    if(JsonMapper.parseUserResponse(auth)){
      val now = Calendar.getInstance().getTime().hashCode().toString + new Random().nextInt(10000).toString()
      gameManager.ask(RequestTrackPlayer(now, username)).mapTo[String]
//      var x = Await.result(gameManager.ask(RequestTrackPlayer(now, username)).mapTo[ActorRef],10.seconds)
//      val map : Future[Map[String,ActorRef]] = Future{
//        var map : Map[String,ActorRef] = Map()
//
//        map += (now -> x)
//        map
//      }
//      map

//    }else{
//      val map : Future[Map[String,ActorRef]] = Future{
//        var map : Map[String,ActorRef] = Map()
//        map += ("9000" -> null)
//        map
//      }
//      map
//
//    }
  }

  def requestStream(playerid: String, matchid: String):Future[ActorRef]= {
    //    val auth = DatabaseHandler.ValidateUser(username,password)
    //    if(JsonMapper.parseUserResponse(auth)){
//    val now = Calendar.getInstance().getTime().hashCode().toString + new Random().nextInt(1000000000).toString()
//    var x = Await.result(gameManager.ask(RequestTrackPlayer(now, username)).mapTo[ActorRef],10.seconds)
//    val map : Future[Map[String,ActorRef]] = Future{
//      var map : Map[String,ActorRef] = Map()
//
//      map += (now -> x)
//      map
//    }
//    map

    gameManager.ask(GetStream(matchid, playerid)).mapTo[ActorRef]


    //    }else{
    //      val map : Future[Map[String,ActorRef]] = Future{
    //        var map : Map[String,ActorRef] = Map()
    //        map += ("9000" -> null)
    //        map
    //      }
    //      map
    //
    //    }
  }

  def joinMatch(username: String, gamehash: String):Future[String] ={
    gameManager.ask(RequestTrackPlayer(gamehash, username)).mapTo[String]

  }


  def playerJoined(username: String, gamehash: String)={
    gameManager.ask(PlayerJoined(username,gamehash))

  }

  def startGame(username: String, gamehash: String):Future[String] ={
    gameManager.ask(StartGame(gamehash, username)).mapTo[String]

  }

  def getGameList(): Future[String] = {
    gameManager.ask(GetGameList()).mapTo[String]
  }



  def purchaseFromMarketStock(username:String,hash:String,sector:String,stock:String,qty:String,turn:String): Future[String] = {
    gameManager.ask(PurchaseMarketStock(username,hash,Utils.GetEnumNamefromName(sector),stock,qty,turn)).mapTo[String]
  }

  def sellToMarket(username:String,hash:String,sector:String,stock:String,qty:String,turn:String): Future[String] = {
    gameManager.ask(SellToMarketStock(username,hash,sector,stock,qty,turn)).mapTo[String]
  }





//  def authenticateUser(username: String, password: String): Future[String] = {
//    userManager.ask(Authenticate(username, password)).mapTo[String]
//  }
}
