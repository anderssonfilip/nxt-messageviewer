package controllers;

import actors.MessageActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import models.Comparators;
import models.NxtParser;
import models.Tuple3;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import scala.concurrent.duration.Duration;
import views.html.graph;
import views.html.index;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Application extends Controller {

    static NxtParser nxtParser = new NxtParser();

    static boolean _showHeight = false;

    static boolean hasBuiltInitialTree = false;
    static String tree = "";

    static String introChatIcon = "/assets/images/chat_blue.png";
    static String replyChatIcon = "/assets/images/chat_red.png";
    static String multiChatIcon = "/assets/images/chat_pair.png";

    public static Result index() {

        return ok(index.render("NXT Chat", "Reading messages..."));
    }

    public static Result messageCount() {

        return ok(validJson(nxtParser.getTextMessageCount() + " (" + nxtParser.getMessageCount() + ")"));
    }

    public static Result jsontree() {

        System.out.println("building tree " + !hasBuiltInitialTree);
        if (!hasBuiltInitialTree) {

            HashMap<String, HashMap<String, List<Tuple3<String, Integer, Boolean>>>> conversations = nxtParser.readDatabase();
            tree = buildTree(conversations);
            hasBuiltInitialTree = true;
        }

        return ok(String.format("[%s]", tree));
    }

    public static WebSocket<JsonNode> ws() {
        return WebSocket.whenReady((in, out) -> {

            ActorSystem system = ActorSystem.create("actorSystem");
            final ActorRef messageActor = Akka.system().actorOf(Props.create(MessageActor.class, out));

            system.scheduler().scheduleOnce(Duration.create(10, TimeUnit.SECONDS), messageActor, nxtParser.getBlockHeight(), system.dispatcher(), null);

            in.onClose(() -> Akka.system().stop(messageActor));
        });
    }

    public static Result graph() {
        return ok(graph.render("NXT Graph"));
    }

    public static String buildTree(HashMap<String, HashMap<String, List<Tuple3<String, Integer, Boolean>>>> conversations) {

        Map<String, Map<String, List<Tuple3<String, Integer, Boolean>>>> sortedConversations = Comparators.sortByMessageHeight(conversations);

        return sortedConversations.keySet().stream()
                .map(c -> String.format("{\"text\" : \"%s\", \"icon\" : \"%s\", \"children\" : [{%s}]}",
                        c,
                        setIcon(sortedConversations.get(c)),
                        buildTree(sortedConversations.get(c)))).collect(Collectors.joining(" ,")).toString();
    }

    public static String buildTree(Map<String, List<Tuple3<String, Integer, Boolean>>> conversations) {

        Map<String, List<Tuple3<String, Integer, Boolean>>> sortedConversations = Comparators.sortInnerByMessageHeight(conversations);

        return sortedConversations.keySet().stream()
                .map(c -> String.format("\"text\" : \"%s\", \"icon\" : \"%s\", \"children\" : %s",
                        c,
                        setIcon(sortedConversations.get(c)),
                        buildTree(sortedConversations.get(c)))).collect(Collectors.joining(",")).toString();
    }

    public static String buildTree(List<Tuple3<String, Integer, Boolean>> conversations) {

        Collections.sort(conversations, Comparators.HEIGHT); // sort the messages

        String messages = conversations.stream()
                .map(m -> formatMessage(m)).collect(Collectors.joining(",")).toString();
        //HtmlUtils.htmlEscape
        return String.format("[%s]", messages);
    }

    public static String formatMessage(Tuple3<String, Integer, Boolean> m) {

        if (_showHeight) {
            return String.format("{\"text\" : %s, \"icon\" : \"%s\"}",
                    validJson(m.fst().replace("@", "@@") + " - " + m.snd()),
                    m.trd() ? introChatIcon : replyChatIcon);
        } else {
            return String.format("{\"text\" : %s, \"icon\" : \"%s\"}",
                    validJson(m.fst().replace("@", "@@")),
                    m.trd() ? introChatIcon : replyChatIcon);
        }
    }

    private static Gson gson = new Gson();

    public static String validJson(String message) {

        return gson.toJson(message).toString();
    }

    public static String setIcon(Map<String, List<Tuple3<String, Integer, Boolean>>> m) {

        if (m.keySet().size() > 1) {
            return multiChatIcon;
        } else {
            return introChatIcon;
        }
    }

    public static String setIcon(List<Tuple3<String, Integer, Boolean>> m) {

        if (m.size() > 1) {
            return multiChatIcon;
        } else {
            return replyChatIcon;
        }
    }
}
