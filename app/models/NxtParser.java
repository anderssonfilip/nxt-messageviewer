package models;

import java.sql.*;
import java.util.*;

public class NxtParser {

    public static TreeMap<String, HashMap<String, List<Tuple2<String, String>>>> readDatabase(String h2Url) {

        HashMap<String, HashMap<String, List<Tuple2<String, String>>>> conversations = new HashMap<String, HashMap<String, List<Tuple2<String, String>>>>();

        Connection conn;
        Statement stat;
        try {
            conn = DriverManager.getConnection(h2Url, "sa", "sa");
            stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("select id, sender_id, recipient_id, height, attachment_bytes from transaction where type = 1 AND subtype = 0 order by height asc");
            while (rs.next()) {

                String sender = rs.getString("sender_id");
                String recipient = rs.getString("recipient_id");
                String hex = rs.getString("attachment_bytes");
                String height = rs.getString("height");
                String message = convertHexToString(hex);

                if (sender.equals(recipient)) { // skip messages sent to self
                    continue;
                }

                if (conversations.containsKey(recipient)) {
                    if (conversations.get(recipient).containsKey(sender)) {
                        conversations.get(recipient).get(sender).add(new Tuple2<String, String>(message, height));
                    }
                } else {
                    List<Tuple2<String, String>> messages = new ArrayList<Tuple2<String, String>>();
                    messages.add(new Tuple2<String, String>(message, height));

                    if (conversations.containsKey(sender) && conversations.get(sender).containsKey(recipient)) {
                        conversations.get(sender).get(recipient).add(new Tuple2<String, String>(message, height));
                    } else if (conversations.containsKey(sender)) {
                        conversations.get(sender).put(recipient, messages);
                    } else {
                        HashMap<String, List<Tuple2<String, String>>> intro = new HashMap<String, List<Tuple2<String, String>>>();
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

        return new TreeMap<String, HashMap<String, List<Tuple2<String, String>>>>(conversations);
    }

    public static String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            //convert the decimal to character
            sb.append((char) Integer.parseInt(output, 16));
        }

        return sb.toString();
    }
}
