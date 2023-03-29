package es.thalesalv.chatrpg.application.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class DelayedPredicate<T> implements Predicate<T> {

    private final AtomicBoolean override = new AtomicBoolean(false);
    private final Predicate<T> test;

    private DelayedPredicate(Predicate<T> test) {

        this.test = test;
    }

    public static <T> Predicate<T> withTest(Predicate<T> test) {

        return new DelayedPredicate<>(test);
    }

    @Override
    public boolean test(T t) {

        return override.getAndSet(test.test(t));
    }
}
