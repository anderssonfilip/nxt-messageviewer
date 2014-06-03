package controllers;

import com.google.gson.*;
import models.*;
import play.*;
import play.mvc.*;

import java.util.HashMap;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

import java.util.*;

import views.html.*;
import models.*;

public class Application extends Controller {

    public static Result index() {

        return ok(main.render("NXT Message Viewer", 2));
    }

    public static Result tree() {

        String json = "[{\"text\" : \"Sender\", \"icon\":\"http://jstree.com/tree.png\", \"children\" : [{\"text\" : \"Recipient 1\", \"children\": [\"Message 1\"]},{\"text\" : \"Recipient 2\", \"children\": [\"Message 2\"] }] }]";

        return ok(toJson(json));
    }

    public static Result jsontree() {

        /*
        TreeMap<String, HashMap<String, List<Tuple2<String, String>>>> conversations = new TreeMap<String, HashMap<String, List<Tuple2<String, String>>>>();
        List<Tuple2<String, String>> messages = new ArrayList<Tuple2<String, String>>();

        messages.add(new Tuple2<String, String>("hello", "123"));

        HashMap<String, List<Tuple2<String, String>>> intro = new HashMap<String, List<Tuple2<String, String>>>();

        intro.put("923423", messages);

        conversations.put("63453534", intro);
        conversations.put("63453535", intro);
        */

        TreeMap<String, HashMap<String, List<Tuple2<String, String>>>> conversations = NxtParser.readDatabase("jdbc:h2:file:/Users/filip/dev/bitbucket/nxt/nxt_db/nxt;MV_STORE=FALSE");

        String tree = buildTree(conversations);

       // System.out.println(tree);

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
                .map(s -> String.format("\"text\" : \"%s\"",
                        s.fst())).collect(Collectors.joining(",")).toString();

        return String.format("[{%s}]", messages);
    }
}
