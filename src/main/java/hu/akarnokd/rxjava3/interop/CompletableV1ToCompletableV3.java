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
 * Convert a V1 Completable into a V3 Completable, composing cancellation.
 */
final class CompletableV1ToCompletableV3 extends io.reactivex.rxjava3.core.Completable {

    final rx.Completable source;

    CompletableV1ToCompletableV3(rx.Completable source) {
        this.source = source;
    }

    @Override
    protected void subscribeActual(io.reactivex.rxjava3.core.CompletableObserver observer) {
        source.subscribe(new SourceCompletableSubscriber(observer));
    }

    static final class SourceCompletableSubscriber
    implements rx.CompletableSubscriber, io.reactivex.rxjava3.disposables.Disposable {

        final io.reactivex.rxjava3.core.CompletableObserver observer;

        rx.Subscription s;

        SourceCompletableSubscriber(io.reactivex.rxjava3.core.CompletableObserver observer) {
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
