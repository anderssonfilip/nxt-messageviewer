package models;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Comparators {

    // descending height
    public static Comparator<Tuple2<String, Integer>> HEIGHT = (m1, m2) -> -m1.snd().compareTo(m2.snd());

    public static <K, V extends Comparable<List<Tuple2<String, Integer>>>> Map<String, List<Tuple2<String, Integer>>> sortByMessageHeight(final Map<String, List<Tuple2<String, Integer>>> map) {
        Comparator<java.lang.String> valueComparator = (k1, k2) ->
                map.get(k2).stream().map(m -> m.snd()).max(Integer::compare).get()
                        .compareTo(
                                map.get(k1).stream().map(m -> m.snd()).max(Integer::compare).get());

        TreeMap<String, List<Tuple2<String, Integer>>> sortedByValues = new TreeMap<>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }
}