package dev.codefactory.akka.essentials

import akka.actor.{Actor, ActorSystem, Props}
import dev.codefactory.akka.essentials.CounterApp.Counter.{Decrement, Increment, Print}

object CounterApp extends App {

  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class Counter extends Actor {
    var count = 0

    override def receive: Receive = {
      case Increment => {
        println(s"[$count] Increment!")
        count += 1
      }
      case Decrement => {
        println(s"[$count] Decrement")
        count -= 1
      }
      case Print => println(count)
    }
  }

  val actorSystem = ActorSystem("counter-system")
  val counter = actorSystem.actorOf(Props(new Counter))

  //lets test thread safety

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

  Thread.sleep(10000)

  counter ! Print

  actorSystem.terminate()
}

