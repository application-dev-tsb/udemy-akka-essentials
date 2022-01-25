package dev.codefactory.akka.essentials

import akka.actor.{Actor, ActorSystem, Props}

object ChildActors extends App {

  object Parent {
    case class CreateChild(name: String)
    case class TellChild(message: String)
  }

  class Parent extends Actor {
    override def receive: Receive = ???
  }

  class Child extends Actor {
    override def receive: Receive = ???
  }

  val system = ActorSystem("childActorDemo")
  val parent = system.actorOf(Props[Parent])


  system.terminate()
}
