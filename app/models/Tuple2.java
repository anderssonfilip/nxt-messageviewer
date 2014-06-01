package models;

public class Tuple2<A, B> {

    private final A _1;
    private final B _2;

    public Tuple2(A a, B b) {
        this._1 = a;
        this._2 = b;
    }

    public A fst() {
        return _1;
    }

    public B snd() {
        return _2;
    }
}
