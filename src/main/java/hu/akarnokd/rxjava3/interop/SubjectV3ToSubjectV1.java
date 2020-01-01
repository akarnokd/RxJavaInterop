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

package hu.akarnokd.rxjava3.interop;

import java.util.concurrent.atomic.*;

/**
 * Wrap a 3.x Subject into a 1.x Subject.
 * @param <T> the input and output value type
 * @since 0.9.0
 */
final class SubjectV3ToSubjectV1<T> extends rx.subjects.Subject<T, T> {

    static <T> rx.subjects.Subject<T, T> createWith(io.reactivex.rxjava3.subjects.Subject<T> subject) {
        State<T> state = new State<>(subject);
        return new SubjectV3ToSubjectV1<>(state);
    }

    final State<T> state;

    private SubjectV3ToSubjectV1(State<T> state) {
        super(state);
        this.state = state;
    }

    @Override
    public void onNext(T t) {
        state.onNext(t);
    }

    @Override
    public void onError(Throwable e) {
        state.onError(e);
    }

    @Override
    public void onCompleted() {
        state.onCompleted();
    }

    @Override
    public boolean hasObservers() {
        return state.hasObservers();
    }

    static final class State<T>
    implements rx.Observable.OnSubscribe<T> {

        final io.reactivex.rxjava3.subjects.Subject<T> subject;

        State(io.reactivex.rxjava3.subjects.Subject<T> subject) {
            this.subject = subject;
        }

        @Override
        public void call(rx.Subscriber<? super T> t) {
            SourceObserver<T> parent = new SourceObserver<>(t);

            t.add(parent);
            t.setProducer(parent);

            subject.subscribe(parent);
        }

        void onNext(T t) {
            subject.onNext(t);
        }

        void onError(Throwable e) {
            subject.onError(e);
        }

        void onCompleted() {
            subject.onComplete();
        }

        boolean hasObservers() {
            return subject.hasObservers();
        }
    }

    static final class SourceObserver<T>
    extends AtomicReference<io.reactivex.rxjava3.disposables.Disposable>
    implements io.reactivex.rxjava3.core.Observer<T>, rx.Subscription, rx.Producer {

        private static final long serialVersionUID = -6567012932544037069L;

        final rx.Subscriber<? super T> actual;

        final AtomicLong requested;

        SourceObserver(rx.Subscriber<? super T> actual) {
            this.actual = actual;
            this.requested = new AtomicLong();
        }

        @Override
        public void request(long n) {
            if (n > 0L) {
                io.reactivex.rxjava3.internal.util.BackpressureHelper.add(requested, n);
            }
        }

        @Override
        public void unsubscribe() {
            io.reactivex.rxjava3.internal.disposables.DisposableHelper.dispose(this);
        }

        @Override
        public boolean isUnsubscribed() {
            return io.reactivex.rxjava3.internal.disposables.DisposableHelper.isDisposed(get());
        }

        @Override
        public void onSubscribe(io.reactivex.rxjava3.disposables.Disposable d) {
            io.reactivex.rxjava3.internal.disposables.DisposableHelper.setOnce(this, d);
        }

        @Override
        public void onNext(T t) {
            if (requested.get() != 0) {
                actual.onNext(t);
                io.reactivex.rxjava3.internal.util.BackpressureHelper.produced(requested, 1);
            } else {
                unsubscribe();
                actual.onError(new rx.exceptions.MissingBackpressureException());
            }
        }

        @Override
        public void onError(Throwable t) {
            lazySet(io.reactivex.rxjava3.internal.disposables.DisposableHelper.DISPOSED);
            actual.onError(t);
        }

        @Override
        public void onComplete() {
            lazySet(io.reactivex.rxjava3.internal.disposables.DisposableHelper.DISPOSED);
            actual.onCompleted();
        }
    }
}
