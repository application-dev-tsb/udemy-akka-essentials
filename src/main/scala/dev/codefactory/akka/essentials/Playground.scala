package dev.codefactory.akka.essentials

import akka.actor.{Actor, ActorSystem, Props}

object Playground extends App {

  println("Bye Java!")

  val actorSystem = ActorSystem("myActorSystem")

  object Person {
    def props(name: String) = Props(new Person(name))
  }

  class Person(name: String) extends Actor {

    var words: Set[String] = Set()

    override def receive: Receive = {
      case message: String => words += message.split(" ")
      case _ => words.toString()
    }
  }

  val person = actorSystem.actorOf(Person.props("Hello Scala"))
  person ! "Bye Java"
}
