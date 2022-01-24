package dev.codefactory.akka.essentials

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object VotingApp extends App {

  val system = ActorSystem("voting")

  case class Vote(candidate: String)
  case object VoteStatusRequest
  case class VoteStatusAndReply(candidate: Option[String])
  class Citizen(var candidate: Option[String] = None) extends Actor {
    override def receive: Receive = {
      case Vote(candidate) => this.candidate = Some(candidate)
      case VoteStatusRequest => sender() ! VoteStatusAndReply(candidate)
    }
  }

  case class AggregateVotes(citizens: Set[ActorRef])
  case object DeclareWinner
  class VoteAggregator extends Actor {
    var votes: Map[String, Int] = Map()

    override def receive: Receive = {
      case AggregateVotes(citizens) => citizens.foreach(_ ! VoteStatusRequest)
      case VoteStatusAndReply(Some(candidate)) => {
        if (votes contains candidate) {

        } else {
          votes += (candidate, 1)
        }
      }
      case DeclareWinner => println(votes)
    }
  }

  //use case:
  val alice = system.actorOf(Props[Citizen])
  val bob = system.actorOf(Props[Citizen])
  val charlie = system.actorOf(Props[Citizen])
  val daniel = system.actorOf(Props[Citizen])

  alice ! Vote("Martin")
  bob ! Vote("Jonas")
  charlie ! Vote("Roland")
  daniel ! Vote("Roland")

  val voteAggregator = system.actorOf(Props[VoteAggregator])
  voteAggregator ! AggregateVotes(Set(alice, bob, charlie, daniel))

  voteAggregator ! DeclareWinner
}
