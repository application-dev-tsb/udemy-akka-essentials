package dev.codefactory.akka.essentials

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object VotingAppImmutable extends App {

  val system = ActorSystem("voting")

  case class Vote(candidate: String)
  case object VoteStatusRequest
  case class VoteStatusAndReply(candidate: Option[String])
  class Citizen() extends Actor {
    override def receive: Receive = voted(None)

    def voted(candidate: Option[String]): Receive = {
      case Vote(candidate) =>
        println(s"Voted: $candidate")
        context.become(voted(Some(candidate)))
      case VoteStatusRequest =>
        println(s"Request Vote Status: $candidate")
        sender() ! VoteStatusAndReply(candidate)
    }
  }

  case class AggregateVotes(citizens: Set[ActorRef])
  case object DeclareWinner
  class VoteAggregator extends Actor {

    override def receive: Receive = action(Set(), Map())

    def action(haveNotVoted: Set[ActorRef], votes: Map[String, Int]): Receive = {
      case AggregateVotes(citizens) =>
        context.become(action(citizens, votes))
        citizens.foreach(_ ! VoteStatusRequest)
      case VoteStatusAndReply(Some(c)) =>
        println(s"[${sender()}] Vote Reply: $c")
        val newHaveNotVoted = haveNotVoted - sender()
        val currentVoteOfCandidate = votes.getOrElse(c, 0)
        context.become(action(newHaveNotVoted, votes + (c -> (currentVoteOfCandidate+1))))
        if (newHaveNotVoted.isEmpty) {
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
