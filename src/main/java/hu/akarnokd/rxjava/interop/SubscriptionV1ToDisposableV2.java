package hu.akarnokd.rxjava.interop;

import io.reactivex.disposables.Disposable;
import rx.Subscription;

final class SubscriptionV1ToDisposableV2 implements Disposable {

    private final Subscription subscription;

    SubscriptionV1ToDisposableV2(Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public void dispose() {
        subscription.unsubscribe();
    }

    @Override
    public boolean isDisposed() {
        return subscription.isUnsubscribed();
    }
}
