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

/**
 * Wraps a 1.x {@link rx.Scheduler} and exposes it as a
 * 3.x {@link io.reactivex.rxjava3.core.Scheduler}.
 * @since 0.12.0
 */
final class SchedulerV1ToSchedulerV3 extends io.reactivex.rxjava3.core.Scheduler {

    final rx.Scheduler source;

    SchedulerV1ToSchedulerV3(rx.Scheduler source) {
        this.source = source;
    }

    @Override
    public long now(TimeUnit unit) {
        return unit.convert(source.now(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void start() {
        if (source instanceof rx.internal.schedulers.SchedulerLifecycle) {
            ((rx.internal.schedulers.SchedulerLifecycle)source).start();
        }
    }

    @Override
    public void shutdown() {
        if (source instanceof rx.internal.schedulers.SchedulerLifecycle) {
            ((rx.internal.schedulers.SchedulerLifecycle)source).shutdown();
        }
    }

    @Override
    public Worker createWorker() {
        return new WorkerV1ToWorkerV3(source.createWorker());
    }

    static final class WorkerV1ToWorkerV3 extends io.reactivex.rxjava3.core.Scheduler.Worker {

        final rx.Scheduler.Worker v1Worker;

        WorkerV1ToWorkerV3(rx.Scheduler.Worker v1Worker) {
            this.v1Worker = v1Worker;
        }

        @Override
        public io.reactivex.rxjava3.disposables.Disposable schedule(Runnable action) {
            final RunnableToV1Action0 runnable = new RunnableToV1Action0(action);
            return RxJavaInterop.toV3Disposable(v1Worker.schedule(runnable));
        }

        @Override
        public io.reactivex.rxjava3.disposables.Disposable schedule(Runnable action, long delayTime, TimeUnit unit) {
            final RunnableToV1Action0 runnable = new RunnableToV1Action0(action);
            return RxJavaInterop.toV3Disposable(v1Worker.schedule(runnable, delayTime, unit));
        }

        @Override
        public io.reactivex.rxjava3.disposables.Disposable schedulePeriodically(Runnable action, long initialDelay, long period, TimeUnit unit) {
            final RunnableToV1Action0 runnable = new RunnableToV1Action0(action);
            return RxJavaInterop.toV3Disposable(v1Worker.schedulePeriodically(runnable, initialDelay, period, unit));
        }

        @Override
        public long now(TimeUnit unit) {
            return unit.convert(v1Worker.now(), TimeUnit.MILLISECONDS);
        }

        @Override
        public void dispose() {
            v1Worker.unsubscribe();
        }

        @Override
        public boolean isDisposed() {
            return v1Worker.isUnsubscribed();
        }
    }

    static final class RunnableToV1Action0 implements rx.functions.Action0 {

        final Runnable source;

        RunnableToV1Action0(Runnable source) {
            java.util.Objects.requireNonNull(source, "Source 3.x Runnable is null");
            this.source = source;
        }

        @Override
        public void call() {
            source.run();
        }
    }
}
