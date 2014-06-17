package models;

import java.util.*;

public class Comparators {

    // descending height
    public static Comparator<Tuple3<String, Integer, Boolean>> HEIGHT = (m1, m2) -> -m1.snd().compareTo(m2.snd());

    public static <K, V extends Comparable<List<Tuple3<String, Integer, Boolean>>>> Map<String, List<Tuple3<String, Integer, Boolean>>> sortInnerByMessageHeight(final Map<String, List<Tuple3<String, Integer, Boolean>>> conversations) {

        Comparator<java.lang.String> valueComparator = (k1, k2) ->
                conversations.get(k2).stream().map(m -> m.snd()).max(Integer::compare).get()
                        .compareTo(
                                conversations.get(k1).stream().map(m -> m.snd()).max(Integer::compare).get());

        TreeMap<String, List<Tuple3<String, Integer, Boolean>>> sortedByValues = new TreeMap<>(valueComparator);
        sortedByValues.putAll(conversations);
        return sortedByValues;
    }

    public static <K, V extends Comparable<Map<String, Map<String, List<Tuple3<String, Integer, Boolean>>>>>> Map<String, Map<String, List<Tuple3<String, Integer, Boolean>>>> sortByMessageHeight(final HashMap<String, HashMap<String, List<Tuple3<String, Integer, Boolean>>>> conversations) {

        Comparator<java.lang.String> valueComparator = (k1, k2) ->
                conversations.get(k2).values().stream().map(l -> l.stream().map(m -> m.snd()).max(Integer::compare).get()).max(Integer::compare).get()
                        .compareTo(
                                conversations.get(k1).values().stream().map(l -> l.stream().map(m -> m.snd()).max(Integer::compare).get()).max(Integer::compare).get());

        Map<String, Map<String, List<Tuple3<String, Integer, Boolean>>>> sortedByValues = new TreeMap<>(valueComparator);
        sortedByValues.putAll(conversations);
        return sortedByValues;
    }
}