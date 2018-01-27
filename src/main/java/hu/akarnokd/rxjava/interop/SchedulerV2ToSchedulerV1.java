/*
 * Copyright 2016-2018 David Karnok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hu.akarnokd.rxjava.interop;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Wraps a 2.x {@link io.reactivex.Scheduler} and exposes it as a
 * 1.x {@link rx.Scheduler}.
 * @author Artem Zinnatulin
 * @since 0.12.0
 */
final class SchedulerV2ToSchedulerV1 extends rx.Scheduler implements rx.internal.schedulers.SchedulerLifecycle {

    final io.reactivex.Scheduler source;

    SchedulerV2ToSchedulerV1(io.reactivex.Scheduler source) {
        this.source = source;
    }

    @Override
    public long now() {
        return source.now(MILLISECONDS);
    }

    @Override
    public void start() {
        source.start();
    }

    @Override
    public void shutdown() {
        source.shutdown();
    }

    @Override
    public Worker createWorker() {
        return new WorkerV2ToWorkerV1(source.createWorker());
    }

    static final class WorkerV2ToWorkerV1 extends rx.Scheduler.Worker {

        final io.reactivex.Scheduler.Worker v2Worker;

        WorkerV2ToWorkerV1(io.reactivex.Scheduler.Worker v2Worker) {
            this.v2Worker = v2Worker;
        }

        @Override
        public rx.Subscription schedule(rx.functions.Action0 action) {
            final Action0V1ToRunnable runnable = new Action0V1ToRunnable(action);
            return RxJavaInterop.toV1Subscription(v2Worker.schedule(runnable));
        }

        @Override
        public rx.Subscription schedule(rx.functions.Action0 action, long delayTime, TimeUnit unit) {
            final Action0V1ToRunnable runnable = new Action0V1ToRunnable(action);
            return RxJavaInterop.toV1Subscription(v2Worker.schedule(runnable, delayTime, unit));
        }

        @Override
        public rx.Subscription schedulePeriodically(rx.functions.Action0 action, long initialDelay, long period, TimeUnit unit) {
            final Action0V1ToRunnable runnable = new Action0V1ToRunnable(action);
            return RxJavaInterop.toV1Subscription(v2Worker.schedulePeriodically(runnable, initialDelay, period, unit));
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
    }

    static final class Action0V1ToRunnable implements Runnable {

        final rx.functions.Action0 source;

        Action0V1ToRunnable(rx.functions.Action0 source) {
            io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "Source 1.x Action0 is null");
            this.source = source;
        }

        @Override
        public void run() {
            source.call();
        }
    }
}
