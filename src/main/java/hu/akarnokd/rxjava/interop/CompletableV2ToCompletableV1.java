/*
 * Copyright 2016 David Karnok
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

/**
 * Convert a V2 Completable into a V1 Completable, composing cancellation.
 */
final class CompletableV2ToCompletableV1 implements rx.Completable.OnSubscribe {

    final io.reactivex.CompletableSource source;

    CompletableV2ToCompletableV1(io.reactivex.CompletableSource source) {
        this.source = source;
    }

    @Override
    public void call(rx.CompletableSubscriber observer) {
        source.subscribe(new SourceCompletableSubscriber(observer));
    }

    static final class SourceCompletableSubscriber
    implements io.reactivex.CompletableObserver, rx.Subscription {

        final rx.CompletableSubscriber observer;

        io.reactivex.disposables.Disposable d;

        SourceCompletableSubscriber(rx.CompletableSubscriber observer) {
            this.observer = observer;
        }

        @Override
        public void onSubscribe(io.reactivex.disposables.Disposable d) {
            this.d = d;
            observer.onSubscribe(this);
        }

        @Override
        public void onComplete() {
            observer.onCompleted();
        }

        @Override
        public void onError(Throwable error) {
            observer.onError(error);
        }

        @Override
        public void unsubscribe() {
            d.dispose();
        }

        @Override
        public boolean isUnsubscribed() {
            return d.isDisposed();
        }
    }
}
