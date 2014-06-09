package actors;

import akka.actor.UntypedActor;
import org.joda.time.DateTime;

public class MessageActor extends UntypedActor {

    private int _height = 0;

    public MessageActor(int height) {
        _height = height;
        System.out.println("Created " + this.toString() + " " + _height);
    }

    @Override
    public void preStart() {
        System.out.println("preStart " + this.toString());
    }

    @Override
    public void onReceive(Object height) throws Exception {
        System.out.println("tick " + this.toString() + " - " + new DateTime());

        String q = String.format(
                "select id, sender_id, recipient_id, height, attachment_bytes " +
                        "from transaction height > %s " +
                        "AND type = 1 " +
                        "AND subtype = 0 " +
                        "order by height asc", _height);

        context().sender().tell("foo", self());

    }
}
