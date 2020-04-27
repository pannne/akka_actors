package com.aroniasoft

import akka.actor.{Actor, ActorLogging, Props}

class RootActor extends Actor  with ActorLogging {

  private var singleThreadProcessorActor = Actor.noSender
  private var filesSchedulerActor = Actor.noSender
  private var fsActor = Actor.noSender

  def receive = {
    case StartActorSystem(src, dst, extFilter) => {
      initFSActor(src, extFilter)
      startFileSchedulerActor(dst)
      startSingleThreadActor

      fsActor ! FSActor.InitMsg(filesSchedulerActor, singleThreadProcessorActor)
      fsActor ! StartFolderScan(src, extFilter)
    }
    case _ => context.system.terminate()
  }

  private def startFileSchedulerActor(dst: String) = {
    log.info("start filesSchedulerActor")
    filesSchedulerActor = context.actorOf(Props[FileSchedulerActor], "filesSchedulerActor")
    filesSchedulerActor ! FileSchedulerActor.InitMsg(fsActor, dst)
  }

  private def startSingleThreadActor = {
    log.info("start singleThreadProcessorActor")
    singleThreadProcessorActor = context.actorOf(Props[SingleThreadActors], "singleThreadProcessorActor")
    singleThreadProcessorActor ! SingleThreadActors.InitMsg(filesSchedulerActor)
  }

  private def initFSActor(src: String, extFilter: List[String]) = {
    fsActor = context.actorOf(Props[FSActor], "fsActor")
  }
}
