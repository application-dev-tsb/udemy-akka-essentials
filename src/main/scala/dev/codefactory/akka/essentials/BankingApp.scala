package dev.codefactory.akka.essentials

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object BankingApp extends App {

  object Bank {
    case class Withdraw(amount: BigDecimal)
    case class Deposit(amount: BigDecimal)
    case object Statement

    case class Success(msg: String)
  }
  class Bank(var name: String, var balance: BigDecimal) extends Actor {
    override def receive: Receive = {
      case _ => println("???")
    }
  }

  case class Yolo(bank: ActorRef)
  class Person(val name: String) extends Actor {
    import Bank._

    override def receive: Receive = {
      case Yolo(bank) => {
        bank ! Withdraw(1000)
        bank ! Deposit(1000)
        bank ! Withdraw(1)
      }
      case msg => println(msg)
    }
  }

  val actorSystem = ActorSystem("banking-app")

  val bank = actorSystem.actorOf(Props(new Bank("BDO", 1_000_000)))
  val person = actorSystem.actorOf(Props(new Person("Bob")))


  actorSystem.terminate()
}
