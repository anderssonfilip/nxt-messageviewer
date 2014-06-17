package models;

public class Tuple3<A, B, C> {

    private final A _1;
    private final B _2;
    private final C _3;

    public Tuple3(A a, B b, C c) {
        this._1 = a;
        this._2 = b;
        this._3 = c;
    }

    public A fst() {
        return _1;
    }

    public B snd() {
        return _2;
    }

    public C trd() { return _3; }
}
