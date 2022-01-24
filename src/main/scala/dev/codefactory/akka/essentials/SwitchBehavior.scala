package dev.codefactory.akka.essentials

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object SwitchBehavior extends App {

  object StatelessActorDemo {
    case object ChangeToDefaultMode
    case object ChangeToAltMode

    case class DefaultReply(message: String)
    case class AltReply(message: String)
  }

  class StatelessActorDemo extends Actor {

    import StatelessActorDemo._

    override def receive: Receive = defaultReceive

    def defaultReceive: Receive = {
      case ChangeToAltMode => context.become(alternativeReceive)
      case ChangeToDefaultMode =>
      case _ => sender() ! DefaultReply("OK")
    }

    def alternativeReceive: Receive = {
      case ChangeToAltMode =>
      case ChangeToDefaultMode => context.become(defaultReceive)
      case _ => sender() ! AltReply("Not OK")
    }
  }

  class Messenger extends Actor {
    import StatelessActorDemo._

    override def receive: Receive = {
      case DefaultReply(message) => println(s"Default: $message")
      case AltReply(message) => println(s"Alt Reply: $message")
      case statelessActorRef: ActorRef => {
        statelessActorRef ! "test"
        statelessActorRef ! ChangeToAltMode
        statelessActorRef ! "test"
        statelessActorRef ! ChangeToDefaultMode
        statelessActorRef ! "test"
      }
    }
  }


  //IMPORTANT NOTE:
  //BECOME AND AND BECOME ALSO COMES IN "STACK" MODE, INSTEAD OF JUST REPLACE
  // context.become(anotherHandler, true) <------ the bool param

  val system = ActorSystem("test123")
  val statelessActorDemo = system.actorOf(Props[StatelessActorDemo])
  val messenger = system.actorOf(Props[Messenger])

  messenger ! statelessActorDemo

  system.terminate()
}
