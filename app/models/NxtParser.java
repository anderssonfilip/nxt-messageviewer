package models;

import actors.MessageActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import scala.concurrent.duration.Duration;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class NxtParser {

    private static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static int _messageCount;
    private static int _textMessageCount;
    private static int _maxHeight;

    private ActorSystem system = null;

    public static final boolean useReedSolomonAddresses = true;

    private static HashMap<String, HashMap<String, List<Tuple3<String, Integer, Boolean>>>> conversations;

    public static ObjectNode ParseMessage(long sender,
                                          long recipient,
                                          int height,
                                          String attachment_bytes) {
        _messageCount++;

        String message = convertHexToString(attachment_bytes);

        if (sender == recipient) { // skip messages sent to self
            return null;
        }

        if (isBinaryMessage(message)) {
            return null;
        }

        if (message.trim().isEmpty()) {
            return null;
        }

        _textMessageCount++;

        if (height > _maxHeight)
            _maxHeight = height;

        if (useReedSolomonAddresses) {

            String s = "NXT-" + crypto.ReedSolomon.encode(sender);
            String r = "NXT-" + crypto.ReedSolomon.encode(recipient);

            AddMessage(s, r, message, height);

            return Json.newObject()
                    .put("type", "message")
                    .put("sender", s)
                    .put("recipient", r)
                    .put("text", message)
                    .put("height", height);
        } else {

            AddMessage(Long.toString(sender), Long.toString(recipient), message, height);

            return Json.newObject()
                    .put("type", "message")
                    .put("sender", sender)
                    .put("recipient", recipient)
                    .put("text", message)
                    .put("height", height);
        }
    }


    private static void AddMessage(String sender,
                                   String recipient,
                                   String message,
                                   int height) {

        if (conversations.containsKey(recipient)) {
            if (conversations.get(recipient).containsKey(sender)) {
                conversations.get(recipient).get(sender).add(new Tuple3<>(message, height, false));
            }
        } else {

            if (conversations.containsKey(sender) && conversations.get(sender).containsKey(recipient)) {
                conversations.get(sender).get(recipient).add(new Tuple3<>(message, height, true));
            } else {
                List<Tuple3<String, Integer, Boolean>> messages = new ArrayList<>();
                messages.add(new Tuple3<>(message, height, true));

                if (conversations.containsKey(sender)) {
                    conversations.get(sender).put(recipient, messages);
                } else {
                    HashMap<String, List<Tuple3<String, Integer, Boolean>>> intro = new HashMap<>();

                    intro.put(recipient, messages);
                    conversations.put(sender, intro);
                }
            }
        }
    }


    public HashMap<String, HashMap<String, List<Tuple3<String, Integer, Boolean>>>> readDatabase() {

        if (conversations != null) {
            return conversations;
        } else {
            conversations = new HashMap<>();

            Connection conn;
            Statement stat;
            try {

                String url = play.Play.application().configuration().getString("db.nxt.url");
                String user = play.Play.application().configuration().getString("db.nxt.user");
                String password = play.Play.application().configuration().getString("db.nxt.user");

                conn = DriverManager.getConnection(url, user, password);
                stat = conn.createStatement();

                _messageCount = 0;
                _textMessageCount = 0;

                ResultSet rs = stat.executeQuery("select id, sender_id, recipient_id, height, attachment_bytes from transaction where type = 1 AND subtype = 0 order by height asc");
                while (rs.next()) {

                    long sender = rs.getLong("sender_id");
                    long recipient = rs.getLong("recipient_id");
                    int height = rs.getInt("height");
                    String attachment_bytes = rs.getString("attachment_bytes");

                    ParseMessage(sender, recipient, height, attachment_bytes);
                }
                stat.close();
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            } finally {

            }

            if (system == null) {
                system = ActorSystem.create("actorSystem");
                ActorRef a = system.actorOf(Props.create(MessageActor.class), "mainActor");

                system.scheduler().scheduleOnce(Duration.create(5, TimeUnit.SECONDS), a, _maxHeight, system.dispatcher(), null);
            }

            return conversations;
        }
    }

    public int getMessageCount() {
        return _messageCount;
    }

    public int getTextMessageCount() {
        return _textMessageCount;
    }

    public int getBlockHeight() {
        return _maxHeight;
    }

    public static String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 8; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            //convert the decimal to character
            sb.append((char) Integer.parseInt(output, 16));
        }

        return sb.toString();
    }

    public static String toHexString(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            chars[i * 2] = hexChars[((bytes[i] >> 4) & 0xF)];
            chars[i * 2 + 1] = hexChars[(bytes[i] & 0xF)];
        }
        return String.valueOf(chars);
    }

    public static boolean isBinaryMessage(String message) {

        int cnt = 0;
        for (int i = 0; i < message.length(); i++) {
            char it = message.charAt(i);
            if (Character.isISOControl(it)) {
                cnt++;
            }
        }
        return cnt > 1;
    }
}
