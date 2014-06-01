package controllers;

import java.util.*;
import play.*;
import play.mvc.*;

import views.html.*;
import models.*;
import org.json.JSONObject;


public class Application extends Controller {

    public static Result index() {

//String, HashMap<String, List<Tuple2<String, String>>>> conversations = readDatabase();
		HashMap<String, List<Tuple2<String, String>>> conversations = new HashMap<String, List<Tuple2<String, String>>>();
      	List<Tuple2<String, String>> messages = new ArrayList<Tuple2<String, String>>();
        
        messages.add(new Tuple2<String, String>("hello", "123"));
		conversations.put("923423", messages);

		JSONObject json = new JSONObject(conversations);

		String strjson = "[\"Simple root node\",{\"text\" : \"Root node 2\",state\" : {\"opened\" : true,\"selected\" : true},\"children\" : [{ \"text\" : \"Child 1\"},\"Child 2]}]";
		System.out.println(json.toString());		
                    
        return ok(main.render("NXT Message Viewer", json.toString()));
    }

}
