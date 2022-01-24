package dev.codefactory.akka.essentials

import akka.actor.{Actor, ActorSystem, Props}
import dev.codefactory.akka.essentials.CounterApp.Counter.{Decrement, Increment, Print}

object CounterAppImmutable extends App {

  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class Counter extends Actor {
    override def receive: Receive = countReceive(0)

    def countReceive(count: Int): Receive = {
      case Increment => context.become(countReceive(count+1))
      case Decrement => context.become(countReceive(count-1))
      case Print => println(s"Count: $count")
    }
  }

  val system = ActorSystem("counter")
  val counter = system.actorOf(Props[Counter])

  val incrementerThread = new Thread(() => {
    var i = 0
    while (i < 1_000) {
      counter ! Increment
      i += 1
    }
  })

  val decrementThread = new Thread(() => {
    var i = 0
    while (i < 1_000) {
      counter ! Decrement
      i += 1
    }
  })

  decrementThread.start()
  incrementerThread.start()

  counter ! Print

  Thread.sleep(10000)

  counter ! Print

  system.terminate()
}
