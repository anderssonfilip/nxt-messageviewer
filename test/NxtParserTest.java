import models.Comparators;
import models.Tuple2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static org.fest.assertions.Assertions.assertThat;

public class NxtParserTest {


    @Test
    public void addMessageListInOrderTest() {
        TreeMap<String, List<Tuple2<String, Integer>>> conversations = new TreeMap<>();
        List<Tuple2<String, Integer>> messages = new ArrayList<>();

        messages.add(new Tuple2<>("hello", 1));
        messages.add(new Tuple2<>("hello", 5));
        messages.add(new Tuple2<>("hello", 3));

        conversations.put("r1", messages);

        Collections.sort(conversations.get("r1"), Comparators.HEIGHT);

        int max = Integer.MAX_VALUE;
        for (Tuple2<String, Integer> m : conversations.get("r1")) {
            assertThat(m.snd()).isLessThan(max);
            max = m.snd();
        }
    }

    @Test
    public void addMessagesInOrderTest() {
        TreeMap<String, List<Tuple2<String, Integer>>> conversations = new TreeMap<>();

        List<Tuple2<String, Integer>> messages = new ArrayList<>();
        messages.add(new Tuple2<>("hello", 2));
        messages.add(new Tuple2<>("hello", 5));
        messages.add(new Tuple2<>("hello", 3));
        conversations.put("r1", messages);

        List<Tuple2<String, Integer>> messages2 = new ArrayList<>();
        messages2.add(new Tuple2<>("hello", 3));
        conversations.put("r2", messages2);

        conversations = Comparators.sortByMessageHeight(conversations);

        assertThat(conversations.firstKey()).isEqualTo("r1");
    }
}
