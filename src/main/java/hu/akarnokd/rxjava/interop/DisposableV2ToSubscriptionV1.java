package hu.akarnokd.rxjava.interop;

import io.reactivex.disposables.Disposable;
import rx.Subscription;

final class DisposableV2ToSubscriptionV1 implements Subscription {

    private final Disposable disposable;

    DisposableV2ToSubscriptionV1(Disposable disposable) {
        this.disposable = disposable;
    }

    @Override
    public void unsubscribe() {
        disposable.dispose();
    }

    @Override
    public boolean isUnsubscribed() {
        return disposable.isDisposed();
    }
}
