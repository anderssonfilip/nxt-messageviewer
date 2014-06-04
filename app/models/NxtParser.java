package models;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class NxtParser {

    private static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static int _messageCount;

    public static TreeMap<String, HashMap<String, List<Tuple2<String, String>>>> readDatabase(String h2Url) {

        HashMap<String, HashMap<String, List<Tuple2<String, String>>>> conversations = new HashMap<>();

        Connection conn;
        Statement stat;
        try {
            conn = DriverManager.getConnection(h2Url, "sa", "sa");
            stat = conn.createStatement();

            _messageCount = 0;

            ResultSet rs = stat.executeQuery("select id, sender_id, recipient_id, height, attachment_bytes from transaction where type = 1 AND subtype = 0 order by height asc");
            while (rs.next()) {

                _messageCount++;

                String sender = rs.getString("sender_id");
                String recipient = rs.getString("recipient_id");
                String height = rs.getString("height");
                String attachment_bytes = rs.getString("attachment_bytes");

                String message = convertHexToString(attachment_bytes);

                if (sender.equals(recipient)) { // skip messages sent to self
                    continue;
                }

                if (isBinaryMessage(message)) {
                    continue;
                }

                if (message.trim().isEmpty()) {
                    continue;
                }

                if (conversations.containsKey(recipient)) {
                    if (conversations.get(recipient).containsKey(sender)) {
                        conversations.get(recipient).get(sender).add(new Tuple2<>(message, height));
                    }
                } else {
                    List<Tuple2<String, String>> messages = new ArrayList<>();
                    messages.add(new Tuple2<>(message, height));

                    if (conversations.containsKey(sender) && conversations.get(sender).containsKey(recipient)) {
                        conversations.get(sender).get(recipient).add(new Tuple2<>(message, height));
                    } else if (conversations.containsKey(sender)) {
                        conversations.get(sender).put(recipient, messages);
                    } else {
                        HashMap<String, List<Tuple2<String, String>>> intro = new HashMap<>();

                        intro.put(recipient, messages);
                        conversations.put(sender, intro);
                    }
                }
            }
            stat.close();
            conn.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        } finally {

        }

        return new TreeMap<>(conversations);
    }

    public static int getMessageCount() {
        return _messageCount;
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
