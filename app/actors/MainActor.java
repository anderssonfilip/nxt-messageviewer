package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import models.NxtParser;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class MainActor extends UntypedActor {


    @Override
    public void preStart() {
        System.out.println("preStart " + this.toString());
    }

    @Override
    public void onReceive(Object payload) throws Exception {

        ActorSystem system = context().system();

        System.out.println("recv: " + payload);

        if (payload instanceof Integer) {
            ActorRef a = system.actorOf(Props.create(MessageActor.class, (int) payload), "messageActor");
            system.scheduler().schedule(Duration.create(0, TimeUnit.SECONDS), Duration.create(60, TimeUnit.SECONDS), a, new Object(), system.dispatcher(), self());
        } else if (payload instanceof String) {



        } else {
            unhandled(payload);
        }
    }
}
