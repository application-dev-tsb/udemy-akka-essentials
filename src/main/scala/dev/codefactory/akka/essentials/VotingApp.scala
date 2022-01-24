package dev.codefactory.akka.essentials

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object VotingApp extends App {

  val system = ActorSystem("voting")

  case class Vote(candidate: String)
  case object VoteStatusRequest
  case class VoteStatusAndReply(candidate: Option[String])
  class Citizen() extends Actor {
    var candidate: Option[String] = None

    override def receive: Receive = {
      case Vote(candidate) => this.candidate = Some(candidate)
      case VoteStatusRequest => sender() ! VoteStatusAndReply(candidate)
    }
  }

  case class AggregateVotes(citizens: Set[ActorRef])
  case object DeclareWinner
  class VoteAggregator extends Actor {
    var haveNotVoted: Set[ActorRef] = Set()
    var votes: Map[String, Int] = Map()

    override def receive: Receive = {
      case AggregateVotes(citizens) => {
        haveNotVoted = citizens
        citizens.foreach(_ ! VoteStatusRequest)
      }
      case VoteStatusAndReply(Some(c)) =>
        val newHaveNotVoted = haveNotVoted - sender()
        val currentVoteOfCandidate = votes.getOrElse(c, 0)
        votes = votes + (c -> (currentVoteOfCandidate+1))
        haveNotVoted = newHaveNotVoted
        if (haveNotVoted.isEmpty) {
          context.self ! DeclareWinner
        }
      case DeclareWinner =>
        println(s"Declare Winner: $votes")
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

  Thread.sleep(5_000)

  system.terminate()
}
