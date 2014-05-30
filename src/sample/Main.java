package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Main extends Application {

    private static String h2Url;

    @Override
    public void start(Stage primaryStage) throws Exception {

        HashMap<String, HashMap<String, List<String>>> conversations = readDatabase();

        TreeItem<String> tree = new TreeItem<String>("Messages");
        tree.setExpanded(false);

        int i = 0;
        int j = 0;

        String sender;
        Iterator<String> i1 = conversations.keySet().iterator();
        do {
            sender = i1.next();
            TreeItem<String> st = new TreeItem<String>(sender);
            tree.getChildren().add(st);

            Iterator<String> i2 = conversations.get(sender).keySet().iterator();
            do {
                String recipient = i2.next();
                TreeItem<String> rt = new TreeItem<String>(recipient);
                st.getChildren().add(rt);
                Iterator<String> i3 = conversations.get(sender).get(recipient).iterator();
                while (i3.hasNext()) {
                    rt.getChildren().add(new TreeItem<String>(i3.next()));
                }

            } while (i2.hasNext());

        } while (i1.hasNext());


        Iterator<TreeItem<String>> it = tree.getChildren().iterator();
        while (it.hasNext()) {
            TreeItem<String> senderItem = it.next();
            Iterator<TreeItem<String>> it2 = senderItem.getChildren().iterator();
            while (it2.hasNext()) {
                i++;
                TreeItem<String> recipientItem = it2.next();

                Iterator<TreeItem<String>> it3 = recipientItem.getChildren().iterator();
                while (it3.hasNext()) {
                    j++;
                    it3.next();
                }
                recipientItem.setValue(recipientItem.getValue() + " (" + j + ")");
                j = 0;
            }
            senderItem.setValue(senderItem.getValue() + " (" + i + ")");
            i = 0;

            TreeView<String> treeView = new TreeView<String>(tree);
            primaryStage.setTitle("Start");
            StackPane root = new StackPane();
            root.getChildren().add(treeView);
            primaryStage.setScene(new Scene(root, 300, 600));
            primaryStage.show();
        }
    }

    public static HashMap<String, HashMap<String, List<String>>> readDatabase() {

        HashMap<String, HashMap<String, List<String>>> conversations = new HashMap<String, HashMap<String, List<String>>>();

        Connection conn;
        Statement stat;
        try {
            conn = DriverManager.getConnection(h2Url, "sa", "sa");
            stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("select id, sender_id, recipient_id, attachment_bytes from transaction where type = 1 AND subtype = 0 order by height asc");
            while (rs.next()) {

                String sender = rs.getString("sender_id");
                String recipient = rs.getString("recipient_id");
                String hex = rs.getString("attachment_bytes");
                String message = convertHexToString(hex);

                if (sender.equals(recipient)) { // skip messages sent to self
                    continue;
                }

                if (conversations.containsKey(recipient)) {
                    if (conversations.get(recipient).containsKey(sender)) {
                        conversations.get(recipient).get(sender).add(message);
                    }
                } else {
                    List<String> messages = new ArrayList<String>();
                    messages.add(message);

                    if (conversations.containsKey(sender) && conversations.get(sender).containsKey(recipient)) {
                        conversations.get(sender).get(recipient).add(message);
                    } else if (conversations.containsKey(sender)) {
                        conversations.get(sender).put(recipient, messages);
                    } else {
                        HashMap<String, List<String>> intro = new HashMap<String, List<String>>();
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

        return conversations;
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

    public static void main(String[] args) {

        if (args.length == 1) {
            h2Url = args[0];
            launch(args);
        } else {
            System.out.println("Argument should be the path to nxt h2 database");
        }
    }
}
