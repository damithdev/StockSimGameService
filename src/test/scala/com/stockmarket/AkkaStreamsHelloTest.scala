package com.stockmarket


import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import scala.concurrent.Future

object AkkaStreamsHelloTest {

  def main(args: Array[String]): Unit = {
    implicit val system       = ActorSystem("Sys")
    implicit val materializer = ActorMaterializer()

    val numbers = 1 to 1000

    val numberSource: Source[Int, NotUsed] = Source.fromIterator(() => numbers.iterator)

    val isEvenFlow: Flow[Int, Int, NotUsed] = Flow[Int].filter((num) => num % 2 == 0)

    val evenNumbersSource: Source[Int, NotUsed] = numberSource.via(isEvenFlow)

    val consoleSink: Sink[Int, Future[Done]] = Sink.foreach[Int](println)

    evenNumbersSource.runWith(consoleSink)
  }
}
