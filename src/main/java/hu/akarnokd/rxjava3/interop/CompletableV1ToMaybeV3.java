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

/**
 * Convert a V1 Completable into a V+ Completable, composing cancellation.
 */
final class CompletableV1ToMaybeV3<T> extends io.reactivex.rxjava3.core.Maybe<T> {

    final rx.Completable source;

    CompletableV1ToMaybeV3(rx.Completable source) {
        this.source = source;
    }

    @Override
    protected void subscribeActual(io.reactivex.rxjava3.core.MaybeObserver<? super T> observer) {
        source.subscribe(new SourceCompletableSubscriber<T>(observer));
    }

    static final class SourceCompletableSubscriber<T>
    implements rx.CompletableSubscriber, io.reactivex.rxjava3.disposables.Disposable {

        final io.reactivex.rxjava3.core.MaybeObserver<? super T> observer;

        rx.Subscription s;

        SourceCompletableSubscriber(io.reactivex.rxjava3.core.MaybeObserver<? super T> observer) {
            this.observer = observer;
        }

        @Override
        public void onSubscribe(rx.Subscription d) {
            this.s = d;
            observer.onSubscribe(this);
        }

        @Override
        public void onCompleted() {
            observer.onComplete();
        }

        @Override
        public void onError(Throwable error) {
            observer.onError(error);
        }

        @Override
        public void dispose() {
            s.unsubscribe();
        }

        @Override
        public boolean isDisposed() {
            return s.isUnsubscribed();
        }
    }
}
