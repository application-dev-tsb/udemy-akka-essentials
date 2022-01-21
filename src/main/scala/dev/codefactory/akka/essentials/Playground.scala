package dev.codefactory.akka.essentials

import akka.actor.{Actor, ActorSystem, Props}

object Playground extends App {

  println("Bye Java!")

  val actorSystem = ActorSystem("myActorSystem")

  object Person {
    def props(name: String) = Props(new Person(name))
  }

  class Person(var name: String) extends Actor {

    override def receive: Receive = {
      case message: String => println(s"$name: $message")
      case ChangeName(newName) => name = newName
      case _ => println("???")
    }
  }

  case class ChangeName(val newName: String)

  val person = actorSystem.actorOf(Person.props("Robert"))
  person ! "Bye Java"

  person ! "Hello"
  person ! ChangeName("Howard")
  person ! "Hello Again"
}
