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
import java.util.stream.Collectors;

public class Application extends Controller {

    public static Result index() {

        return ok(main.render("NXT Chat", "Reading messages..."));
    }

    public static Result messageCount() {

        return ok("" + NxtParser.getMessageCount());
    }

    public static Result jsontree() {

        HashMap<String, HashMap<String, List<Tuple2<String, Integer>>>> conversations = NxtParser.readDatabase("jdbc:h2:file:/Users/filip/dev/bitbucket/nxt/nxt_db/nxt;MV_STORE=FALSE");

        String tree = buildTree(conversations);

        return ok(String.format("[%s]", tree));
    }

    public static String buildTree(HashMap<String, HashMap<String, List<Tuple2<String, Integer>>>> conversations) {

        return conversations.keySet().stream()
                .map(c -> String.format("{\"text\" : \"%s\", \"children\" : [{%s}]}",
                        c,
                        buildTree(conversations.get(c)))).collect(Collectors.joining(" ,")).toString();
    }

    public static String buildTree(Map<String, List<Tuple2<String, Integer>>> conversations) {

        return conversations.keySet().stream()
                .map(c -> String.format("\"text\" : \"%s\", \"children\" : %s",
                        c,
                        buildTree(conversations.get(c)))).collect(Collectors.joining(",")).toString();
    }

    public static String buildTree(List<Tuple2<String, Integer>> conversations) {

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
