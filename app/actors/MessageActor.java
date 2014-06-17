package actors;

import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.Application;
import models.NxtParser;
import play.mvc.WebSocket;
import scala.concurrent.duration.Duration;

import java.sql.*;
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

            System.out.println(payload);
            System.out.println(_height);
        } else {
            unhandled(payload);
        }
    }


    private void checkForNewMessages(){

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
            conn = DriverManager.getConnection(Application.H2ConnectionString, "sa", "sa");
            stat = conn.createStatement();

            ResultSet rs = stat.executeQuery(query);
            int height = _height;
            while (rs.next()) {

                long sender = rs.getLong("sender_id");
                long recipient = rs.getLong("recipient_id");
                height = rs.getInt("height");
                String attachment_bytes = rs.getString("attachment_bytes");

                String message = NxtParser.convertHexToString(attachment_bytes);

                if (sender == recipient) { // skip messages sent to self
                    continue;
                }

                if (NxtParser.isBinaryMessage(message)) {
                    continue;
                }

                if (message.trim().isEmpty()) {
                    continue;
                }

                //_textMessageCount++;

                if (NxtParser.useReedSolomonAddresses) {
                    String s = "NXT-" + crypto.ReedSolomon.encode(sender);
                    String r = "NXT-" + crypto.ReedSolomon.encode(recipient);

                    context().sender().tell("foo", self());

                } else {
                    //AddMessage(Long.toString(sender), Long.toString(recipient), message, height);
                }

            }
            _height = height;

        } catch (SQLException ex) {
            System.out.println(ex);
        } finally {

        }
    }
}
