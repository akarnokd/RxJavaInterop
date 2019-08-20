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

import java.util.concurrent.atomic.AtomicReference;

/**
 * Converts a V3 Maybe into a V1 Complete where an onSuccess value triggers onCompleted.
 *
 * @param <T> the value type
 */
final class MaybeV3ToCompletableV1<T> implements rx.Completable.OnSubscribe {

    final io.reactivex.rxjava3.core.MaybeSource<T> source;

    MaybeV3ToCompletableV1(io.reactivex.rxjava3.core.MaybeSource<T> source) {
        this.source = source;
    }

    @Override
    public void call(rx.CompletableSubscriber t) {
        MaybeV3Observer<T> parent = new MaybeV3Observer<T>(t);
        t.onSubscribe(parent);
        source.subscribe(parent);
    }

    static final class MaybeV3Observer<T>
    extends AtomicReference<io.reactivex.rxjava3.disposables.Disposable>
    implements io.reactivex.rxjava3.core.MaybeObserver<T>, rx.Subscription {

        private static final long serialVersionUID = 5045507662443540605L;

        final rx.CompletableSubscriber actual;

        MaybeV3Observer(rx.CompletableSubscriber actual) {
            this.actual = actual;
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
        public void onSuccess(T value) {
            actual.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            actual.onError(e);
        }

        @Override
        public void onComplete() {
            actual.onCompleted();
        }
    }
}
