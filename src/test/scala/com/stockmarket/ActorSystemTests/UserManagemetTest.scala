package com.stockmarket.ActorSystemTests

import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import com.stockmarket.System.StopSystemAfterAll
import com.stockmarket.actors.management.UserManager
import com.stockmarket.actors.management.UserManager._
import com.stockmarket.messages.JsonMapper
import org.scalatest.{MustMatchers, WordSpecLike}

class UserManagemetTest extends TestKit(ActorSystem("testBoxOffice"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with DefaultTimeout
  with StopSystemAfterAll {
  "UserActor" must {

    // Test Must be run One by One Not All together
    "Create An User" in {
      val userActor = system.actorOf(UserManager.props)
      val userName = "Jhon"
      val passWord = "Secret"
      val fullName = "Jhon Walker"
      userActor ! AddUser(User(userName,passWord,fullName))
      expectMsg(JsonMapper.getStatusResponse("0x7000"))

//      stockmarket ! GetStockItems
//      expectMsg(StockItems(Vector(StockItem(itemName, 10))))
//
//      stockmarket ! Market.GetStockItem(itemName)
//      expectMsg(Some(StockItem(itemName, 10)))
//      //
//      stockmarket ! GetStocks(itemName, 1)
//      expectMsg(Stocks(itemName, Vector(Stock(10))))
//
//      stockmarket ! GetStocks("Google", 1)
//      expectMsg(Stocks("Google"))
    }

    "Not create an existing user" in {
      val userActor = system.actorOf(UserManager.props)
      val userName = "Jhon"
      val passWord = "Secret"
      val fullName = "Jhon Walker"
      userActor ! AddUser(User(userName,passWord,fullName))
      expectMsg(JsonMapper.getStatusResponse("0x7000"))
      userActor ! AddUser(User(userName,passWord,fullName))
      expectMsg(JsonMapper.getStatusResponse("0x8000"))

    }

    "Provide a list of all available users" in {
      val userActor = system.actorOf(UserManager.props)
      val userName = "Jhon"
      val passWord = "Secret"
      val fullName = "Jhon Walker"
      userActor ! AddUser(User(userName,passWord,fullName))
      expectMsg(JsonMapper.getStatusResponse("0x7000"))
      userActor ! AddUser(User("Steve",passWord, "Steve Rogers"))
      expectMsg(JsonMapper.getStatusResponse("0x7000"))

      userActor ! GetAllUsers
      expectMsg(UserListResponse(List(("Jhon","Jhon Walker"), ("Steve","Steve Rogers"))))
    }

    "Authenticate Valid Credentials " in {
      val userActor = system.actorOf(UserManager.props)
      val userName = "Jhon"
      val passWord = "Secret"
      val fullName = "Jhon Walker"
      userActor ! AddUser(User(userName,passWord,fullName))
      expectMsg(JsonMapper.getStatusResponse("0x7000"))
      userActor ! AddUser(User("Steve",passWord,"Steve Rogers"))
      expectMsg(JsonMapper.getStatusResponse("0x7000"))

      userActor ! Authenticate("Jhon","Secret")
      expectMsg(JsonMapper.getUserResponse("0x7000","Jhon Walker","Jhon"))
    }

    "Fail Invalid Authentication" in {
      val userActor = system.actorOf(UserManager.props)
      val userName = "Jhon"
      val passWord = "Secret"
      val fullName = "Jhon Walker"
      userActor ! AddUser(User(userName,passWord,fullName))
      expectMsg(JsonMapper.getStatusResponse("0x7000"))
      userActor ! AddUser(User("Steve",passWord,"Steve Rogers"))
      expectMsg(JsonMapper.getStatusResponse("0x7000"))

      userActor ! Authenticate("Jhon","Lie")
      expectMsg(JsonMapper.getStatusResponse("0x8000"))
    }
  }


}
