package controllers;

import com.google.gson.Gson;
import models.NxtParser;
import models.Tuple2;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class Application extends Controller {

    public static Result index() {

        return ok(main.render("NXT Chat", "Reading messages..."));
    }

    public static Result tree() {

        String json = "[{\"text\" : \"Sender\", \"icon\":\"http://jstree.com/tree.png\", \"children\" : [{\"text\" : \"Recipient 1\", \"children\": [\"Message 1\"]},{\"text\" : \"Recipient 2\", \"children\": [\"Message 2\"] }] }]";

        return ok(toJson(json));
    }

    public static Result messageCount() {

        return ok("" + NxtParser.getMessageCount());
    }

    public static Result jsontree() {

        TreeMap<String, HashMap<String, List<Tuple2<String, String>>>> conversations = NxtParser.readDatabase("jdbc:h2:file:/Users/filip/dev/bitbucket/nxt/nxt_db/nxt;MV_STORE=FALSE");

        String tree = buildTree(conversations);

        return ok(String.format("[%s]", tree));
    }

    public static String buildTree(TreeMap<String, HashMap<String, List<Tuple2<String, String>>>> conversations) {

        return conversations.keySet().stream()
                .map(c -> String.format("{\"text\" : \"%s\", \"children\" : [{%s}]}",
                        c,
                        buildTree(conversations.get(c)))).collect(Collectors.joining(" ,")).toString();
    }

    public static String buildTree(Map<String, List<Tuple2<String, String>>> conversations) {

        return conversations.keySet().stream()
                .map(c -> String.format("\"text\" : \"%s\", \"children\" : %s",
                        c,
                        buildTree(conversations.get(c)))).collect(Collectors.joining(",")).toString();
    }

    public static String buildTree(List<Tuple2<String, String>> conversations) {

        String messages = conversations.stream()
                .map(s -> String.format("{\"text\" : %s}",
                        validJson((s.fst().replace("@", "@@"))))).collect(Collectors.joining(",")).toString();
        //HtmlUtils.htmlEscape
        return String.format("[%s]", messages);
    }

    private static Gson gson = new Gson();

    public static String validJson(String message) {

        return gson.toJson(message).toString();
    }
}
