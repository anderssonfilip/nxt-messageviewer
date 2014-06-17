import models.Tuple2;
import org.junit.Test;
import play.twirl.api.Content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;

/**
 * Simple (JUnit) tests that can call all parts of a play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 */
public class ApplicationTest {

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }

    @Test
    public void renderTemplate() {
        Content html = views.html.main.render("Your new application is ready.", "0");
        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Your new application is ready.");
    }

    @Test
    public void toJsonTreeTest() {
        HashMap<String, List<Tuple2<String, Integer>>> conversations = new HashMap<>();
        List<Tuple2<String, Integer>> messages = new ArrayList<>();

        messages.add(new Tuple2<>("hello", 123));
        conversations.put("923423", messages);

        String r = controllers.Application.buildTree(messages);

        assertThat(false);
    }
}
