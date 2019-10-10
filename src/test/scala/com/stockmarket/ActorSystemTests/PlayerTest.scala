package com.stockmarket.ActorSystemTests

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit, TestProbe}
import com.stockmarket.System.StopSystemAfterAll
//import com.stockmarket.actors.middlelevel.Market.PurchaseMarketStock
import com.stockmarket.actors.middlelevel.{PlayMatch, Player}
import com.stockmarket.actors.toplevel.Game
import com.stockmarket.actors.toplevel.Game.RequestCreatedMatch
import com.stockmarket.models.FINANCE
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._



class PlayerTest extends TestKit(ActorSystem("testPlayer"))
  with WordSpecLike
  with Matchers
  with ImplicitSender
  with DefaultTimeout with StopSystemAfterAll{
  "Player" must {
    "reply to registration request" in {
      val probe = TestProbe()
      val playerActor = system.actorOf(Player.props("match","player"))
      playerActor.tell(Game.RequestTrackPlayer("match","player"),probe.ref)
      probe.expectMsg(500.milliseconds,Game.PlayerRegistered)
      probe.lastSender should ===(playerActor)
    }

    "ignore wrong registration requests" in {
      val probe = TestProbe()
      val playerActor = system.actorOf(Player.props("match","player"))

      playerActor.tell(Game.RequestTrackPlayer("wrongMatch","device"), probe.ref)
      probe.expectNoMessage(500.milliseconds)

      playerActor.tell(Game.RequestTrackPlayer("match","WrontPlayer"),probe.ref)
      probe.expectNoMessage(500.milliseconds)
    }

    "able to register a player actor" in {
      val probe = TestProbe()

      val matchActor = system.actorOf(PlayMatch.props("match1"))

      matchActor.tell(Game.RequestTrackPlayer("match1","player1"),probe.ref)
      probe.expectMsg(Game.PlayerRegistered)
      val matchActor1 = probe.lastSender
      matchActor.tell(Game.RequestTrackPlayer("match1","player2"),probe.ref)
      probe.expectMsg(Game.PlayerRegistered)
      val matchActor2 = probe.lastSender

      matchActor1 should !== (matchActor2)

      matchActor1.tell(Player.BuyStock(0,"GOOGLE",20),probe.ref)
      probe.expectMsg(Player.StockBought(0))
      matchActor1.tell(Player.SellStock(0,"GOOGLE",15),probe.ref)
      probe.expectMsg(Player.StockSold(0))
      matchActor1.tell(Player.SellStock(0,"GOOGLE",15),probe.ref)
      probe.expectMsg(Player.StockSellFailed(0))





    }

    "ignore requests for wrong group Id" in {
      val probe = TestProbe()
      val matchActor = system.actorOf(PlayMatch.props("match"))
      matchActor.tell(Game.RequestTrackPlayer("wrongMatch","player"),probe.ref)
      probe.expectNoMessage(500.milliseconds)
    }

    "return same actor for same player id" in {
      val probe = TestProbe()
      val matchActor = system.actorOf(PlayMatch.props("match"))
      matchActor.tell(Game.RequestTrackPlayer("match","player"),probe.ref)
      probe.expectMsg(Game.PlayerRegistered)
      val matchActor1 = probe.lastSender

      matchActor.tell(Game.RequestTrackPlayer("match","player"),probe.ref)
      probe.expectMsg(Game.PlayerRegistered)
      val matchActor2 = probe.lastSender

      matchActor1 should ===(matchActor2)
    }

    "be able to list active players" in {
      val probe = TestProbe()
      val groupActor = system.actorOf(PlayMatch.props("match"))

      groupActor.tell(Game.RequestTrackPlayer("match", "player1"), probe.ref)
      probe.expectMsg(Game.PlayerRegistered)

      groupActor.tell(Game.RequestTrackPlayer("match", "player2"), probe.ref)
      probe.expectMsg(Game.PlayerRegistered)

      groupActor.tell(PlayMatch.RequestPlayerList(requestId = 0), probe.ref)
      probe.expectMsg(PlayMatch.ReplyPlayerList(requestId = 0, Set("player1", "player2")))
    }

    "be able to list active players after one shuts down" in {
      val probe = TestProbe()
      val groupActor = system.actorOf(PlayMatch.props("match"))

      groupActor.tell(Game.RequestTrackPlayer("match", "player1"), probe.ref)
      probe.expectMsg(Game.PlayerRegistered)
      val toShutDown = probe.lastSender



      groupActor.tell(PlayMatch.RequestPlayerList(0), probe.ref)
      probe.expectMsg(PlayMatch.ReplyPlayerList(requestId = 0, Set("player1", "player2")))

      probe.watch(toShutDown)
      toShutDown ! PoisonPill
      probe.expectTerminated(toShutDown)

      // using awaitAssert to retry because it might take longer for the matchActor
      // to see the Terminated, that order is undefined
      probe.awaitAssert {
        groupActor.tell(PlayMatch.RequestPlayerList(0), probe.ref)
        probe.expectMsg(PlayMatch.ReplyPlayerList(requestId = 0, Set("player2")))
      }
    }

//    "be able to create a match from game actor" in {
//      val probe = TestProbe()
//      val gameActor: ActorRef = system.actorOf(Game.props)
//
//      gameActor.tell(Game.RequestTrackPlayer("match", "player1"), probe.ref)
//      gameActor.tell(Game.RequestTrackPlayer("match", "player2"), probe.ref)
//      gameActor.tell(Game.RequestTrackPlayer("match", "player3"), probe.ref)
//
//
//      gameActor.tell(Game.StartGame("match", "player1"),probe.ref)
//      Thread.sleep(3000)
//      gameActor.tell(PurchaseMarketStock("player1","match",FINANCE.id,FINANCE.AMEX.toString,10),probe.ref)

//      probe.expectMsg(Game.PlayerRegistered)
//      val toShutDown = probe.lastSender
//            probe.awaitAssert {
//              val res : ActorRef = gameActor.ask(RequestCreatedMatch("match"),probe.ref)
//
//
//              ref.tell(PlayMatch.RequestPlayerList(0), probe.ref)
//              probe.expectMsg(PlayMatch.ReplyPlayerList(requestId = 0, Set("player1")))
////              lastSender should ===(matchref)
//            }


//      groupActor.tell(PlayMatch.RequestPlayerList(0), probe.ref)
//      probe.expectMsg(PlayMatch.ReplyPlayerList(requestId = 0, Set("player1", "player2")))
//
//      probe.watch(toShutDown)
//      toShutDown ! PoisonPill
//      probe.expectTerminated(toShutDown)
//
//      // using awaitAssert to retry because it might take longer for the matchActor
//      // to see the Terminated, that order is undefined

//    }
  }
}
