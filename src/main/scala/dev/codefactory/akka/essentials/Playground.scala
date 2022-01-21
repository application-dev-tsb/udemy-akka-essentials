package dev.codefactory.akka.essentials

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object Playground extends App {

  println("Bye Java!")

  val actorSystem = ActorSystem("myActorSystem")

  object Person {
    def props(name: String) = Props(new Person(name))
  }

  class Person(var name: String) extends Actor {

    override def receive: Receive = {
      case AskSender => println(s"sender: $sender")
      case message: String => println(s"(from: $sender) $name: $message")
      case ChangeName(newName) => name = newName
      case InfiniteLoop => {
        println(s"$name: Zup ${System.currentTimeMillis()}")
        self ! InfiniteLoop
      }
      case WirelessMessage(content, ref) => ref forward (s"($ref): $content")
      case _ => println("???")
    }
  }

  case class ChangeName(newName: String)
  case class InfiniteLoop()
  case class AskSender()
  case class WirelessMessage(content: String, ref: ActorRef)

  val person = actorSystem.actorOf(Person.props("Robert"))
  person ! "Bye Java"

  person ! "Hello"
  person ! ChangeName("Howard")
  person ! "Hello Again"

  //person ! InfiniteLoop
  person ! AskSender


  val alice = actorSystem.actorOf(Person.props("Alice"))
  val daniel = actorSystem.actorOf(Person.props("Daniel"))

  alice ! WirelessMessage("test", daniel)

  actorSystem.terminate()
}
