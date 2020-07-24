package converter.utils;

public class Pair<T> {
    private final T from;
    private final T to;

    public Pair(T f, T t) {
        from = f;
        to = t;
    }

    public T getFrom() {
        return from;
    }

    public T getTo() {
        return to;
    }
}
