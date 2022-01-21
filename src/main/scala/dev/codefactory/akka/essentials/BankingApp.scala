package dev.codefactory.akka.essentials

import akka.actor.{Actor, ActorSystem, Props}

object BankingApp extends App {

  class Bank(var name: String, var balance: BigDecimal) extends Actor {
    override def receive: Receive = {
      case _ => println("???")
    }
  }

  class Person(val name: String) extends Actor {
    override def receive: Receive = {
      case _ => println("???")
    }
  }

  val actorSystem = ActorSystem("banking-app")

  val bank = actorSystem.actorOf(Props(new Bank("BDO", 1_000_000)))
  val person = actorSystem.actorOf(Props(new Person("Bob")))


  actorSystem.terminate()
}
