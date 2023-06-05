package us.cpvp.kits.data.mongo;

import org.reactivestreams.Subscription;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CompletableFutureSubscriber<TResult, TReturn> extends ObservableSubscriber<TResult> {
    private final CompletableFuture<TReturn> future;
    private final Function<TResult, TReturn> action;
    private final int requests;

    public CompletableFutureSubscriber(CompletableFuture<TReturn> future, Function<TResult, TReturn> action, int requests) {
        this.future = future;
        this.action = action;
        this.requests = requests;
    }

    public CompletableFutureSubscriber(CompletableFuture<TReturn> future, Function<TResult, TReturn> action) {
        this(future, action, Integer.MAX_VALUE);
    }

    @Override
    public void onSubscribe(Subscription s) {
        super.onSubscribe(s);
        s.request(requests);
    }

    @Override
    public void onNext(TResult t) {
        super.onNext(t);

        future.complete(action.apply(t));

        super.onComplete();
    }

    @Override
    public void onError(Throwable t) {
        super.onError(t);

        future.completeExceptionally(t);
    }
}
