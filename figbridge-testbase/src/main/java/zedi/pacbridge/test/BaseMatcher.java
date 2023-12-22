package zedi.pacbridge.test;

import java.util.function.Predicate;

public class BaseMatcher<T> {

    private final Predicate<T> predicate;
    private final String description;

    public BaseMatcher(Predicate<T> predicate, String description) {
        this.predicate = predicate;
        this.description = description;
    }

    public boolean matches(T item) {
        return predicate.test(item);
    }

    public String getDescription() {
        return description;
    }
}
