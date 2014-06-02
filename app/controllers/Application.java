package controllers;

import java.util.*;
import play.*;
import play.mvc.*;

import views.html.*;
import models.*;
import static play.libs.Json.toJson;


public class Application extends Controller {

    public static Result index() {

        return ok(main.render("NXT Message Viewer", 2));
    }

    public static Result tree() {
		HashMap<String, List<Tuple2<String, String>>> conversations = new HashMap<String, List<Tuple2<String, String>>>();
      	List<Tuple2<String, String>> messages = new ArrayList<Tuple2<String, String>>();
        
        messages.add(new Tuple2<String, String>("hello", "123"));
		conversations.put("923423", messages);

		String json = "[{\"text\" : \"Sender\", \"icon\":\"http://jstree.com/tree.png\", \"children\" : [{\"text\" : \"Recipient 1\", \"children\": [\"Message 1\"]},{\"text\" : \"Recipient 2\", \"children\": [\"Message 2\"] }] }]";

      	return ok(toJson(json));
    }
}
