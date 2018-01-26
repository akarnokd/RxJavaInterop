package hu.akarnokd.rxjava.interop;


import io.reactivex.Scheduler;
import rx.Subscription;
import rx.functions.Action0;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

final class SchedulerV2ToSchedulerV1 extends rx.Scheduler {

    private final Scheduler source;

    SchedulerV2ToSchedulerV1(Scheduler source) {
        this.source = source;
    }

    @Override
    public long now() {
        return source.now(MILLISECONDS);
    }

    @Override
    public Worker createWorker() {
        final Scheduler.Worker v2Worker = source.createWorker();

        return new Worker() {
            @Override
            public Subscription schedule(Action0 action) {
                return RxJavaInterop.toV1Subscription(v2Worker.schedule(new Action0V1ToRunnable(action)));
            }

            @Override
            public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
                return RxJavaInterop.toV1Subscription(
                        v2Worker.schedule(new Action0V1ToRunnable(action), delayTime, unit)
                );
            }

            @Override
            public Subscription schedulePeriodically(Action0 action, long initialDelay, long period, TimeUnit unit) {
                return RxJavaInterop.toV1Subscription(v2Worker.schedulePeriodically(
                        new Action0V1ToRunnable(action), initialDelay, period, unit)
                );
            }

            @Override
            public long now() {
                return v2Worker.now(MILLISECONDS);
            }

            @Override
            public void unsubscribe() {
                v2Worker.dispose();
            }

            @Override
            public boolean isUnsubscribed() {
                return v2Worker.isDisposed();
            }
        };
    }

    static final class Action0V1ToRunnable implements Runnable {

        private final Action0 source;

        Action0V1ToRunnable(Action0 source) {
            io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "Source 1.x Action0 is null");
            this.source = source;
        }

        @Override
        public void run() {
            source.call();
        }
    }
}
