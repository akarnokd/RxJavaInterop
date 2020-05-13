/*
 * Copyright 2016-2020 David Karnok
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

package hu.akarnokd.rxjava3.interop;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Wraps a 3.x {@link io.reactivex.rxjava3.core.Scheduler} and exposes it as a
 * 1.x {@link rx.Scheduler}.
 * @author Artem Zinnatulin
 * @since 0.12.0
 */
final class SchedulerV3ToSchedulerV1 extends rx.Scheduler implements rx.internal.schedulers.SchedulerLifecycle {

    final io.reactivex.rxjava3.core.Scheduler source;

    SchedulerV3ToSchedulerV1(io.reactivex.rxjava3.core.Scheduler source) {
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
        return new WorkerV3ToWorkerV1(source.createWorker());
    }

    static final class WorkerV3ToWorkerV1 extends rx.Scheduler.Worker {

        final io.reactivex.rxjava3.core.Scheduler.Worker v3Worker;

        WorkerV3ToWorkerV1(io.reactivex.rxjava3.core.Scheduler.Worker v3Worker) {
            this.v3Worker = v3Worker;
        }

        @Override
        public rx.Subscription schedule(rx.functions.Action0 action) {
            final Action0V1ToRunnable runnable = new Action0V1ToRunnable(action);
            return RxJavaInterop.toV1Subscription(v3Worker.schedule(runnable));
        }

        @Override
        public rx.Subscription schedule(rx.functions.Action0 action, long delayTime, TimeUnit unit) {
            final Action0V1ToRunnable runnable = new Action0V1ToRunnable(action);
            return RxJavaInterop.toV1Subscription(v3Worker.schedule(runnable, delayTime, unit));
        }

        @Override
        public rx.Subscription schedulePeriodically(rx.functions.Action0 action, long initialDelay, long period, TimeUnit unit) {
            final Action0V1ToRunnable runnable = new Action0V1ToRunnable(action);
            return RxJavaInterop.toV1Subscription(v3Worker.schedulePeriodically(runnable, initialDelay, period, unit));
        }

        @Override
        public long now() {
            return v3Worker.now(MILLISECONDS);
        }

        @Override
        public void unsubscribe() {
            v3Worker.dispose();
        }

        @Override
        public boolean isUnsubscribed() {
            return v3Worker.isDisposed();
        }
    }

    static final class Action0V1ToRunnable implements Runnable {

        final rx.functions.Action0 source;

        Action0V1ToRunnable(rx.functions.Action0 source) {
            java.util.Objects.requireNonNull(source, "Source 1.x Action0 is null");
            this.source = source;
        }

        @Override
        public void run() {
            source.call();
        }
    }
}
