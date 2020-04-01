package com.aroniasoft

import akka.actor.{Actor, ActorSystem, Props}

class RootActor extends Actor {
  def receive = {
    case StartActorSystem => {
      println("start FS Actor")
      val fsActor = context.actorOf(Props[FSActor], "fsActor")
      fsActor ! StartFolderScan("C:\\Users\\nesovic\\Desktop\\nokia-3-3-20", List("mp4"))
    }
    case _ => context.system.terminate()
  }
}


object ActorFirst extends App {

  val system = ActorSystem("ImgProcSystem")

  val rootActor = system.actorOf(Props[RootActor], "rootActor")
  rootActor ! StartActorSystem

}
