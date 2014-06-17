package actors;

import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.NxtParser;
import play.mvc.WebSocket;
import scala.concurrent.duration.Duration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MessageActor extends UntypedActor {

    private final WebSocket.Out<JsonNode> _wsOut;

    private int _height;

    public MessageActor(WebSocket.Out<JsonNode> out) {
        _wsOut = out;
    }

    @Override
    public void preStart() {
        System.out.println("preStart " + this.toString());
    }

    @Override
    public void onReceive(Object payload) throws Exception {

        ActorSystem system = context().system();

        if (payload instanceof Integer) {
            _height = (int) payload;
            system.scheduler().schedule(Duration.create(0, TimeUnit.SECONDS), Duration.create(30, TimeUnit.SECONDS), self(), new Object(), system.dispatcher(), self());
        } else if (payload instanceof Object) {

            List<ObjectNode> messages = checkForNewMessages();
            System.out.println("# of new messages: " + messages.size());
            messages.stream().forEach(m -> _wsOut.write(m));

        } else {
            unhandled(payload);
        }
    }


    private List<ObjectNode> checkForNewMessages() {

        List<ObjectNode> messages = new ArrayList<>();

        String query = String.format(
                "SELECT sender_id, recipient_id, height, attachment_bytes" +
                        " FROM transaction" +
                        " WHERE height > %s" +
                        " AND type = 1" +
                        " AND subtype = 0" +
                        " ORDER BY height ASC", _height);

        Connection conn;
        Statement stat;
        try {

            String url = play.Play.application().configuration().getString("db.nxt.url");
            String user = play.Play.application().configuration().getString("db.nxt.user");
            String password = play.Play.application().configuration().getString("db.nxt.user");

            conn = DriverManager.getConnection(url, user, password);
            stat = conn.createStatement();

            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {

                long sender = rs.getLong("sender_id");
                long recipient = rs.getLong("recipient_id");
                int height = rs.getInt("height");
                String attachment_bytes = rs.getString("attachment_bytes");

                ObjectNode message = NxtParser.ParseMessage(sender, recipient, height, attachment_bytes);

                if (message != null) {
                    messages.add(message);
                }

                _height = height;
            }

        } catch (SQLException ex) {
            System.out.println(ex);
        } finally {

        }
        return messages;
    }
}
