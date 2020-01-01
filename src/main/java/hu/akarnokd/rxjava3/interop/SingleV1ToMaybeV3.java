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

/**
 * Convert a V1 Single into a V3 Maybe, composing cancellation.
 *
 * @param <T> the value type
 */
final class SingleV1ToMaybeV3<T> extends io.reactivex.rxjava3.core.Maybe<T> {

    final rx.Single<T> source;

    SingleV1ToMaybeV3(rx.Single<T> source) {
        this.source = source;
    }

    @Override
    protected void subscribeActual(io.reactivex.rxjava3.core.MaybeObserver<? super T> observer) {
        SourceSingleSubscriber<T> parent = new SourceSingleSubscriber<>(observer);
        observer.onSubscribe(parent);
        source.subscribe(parent);
    }

    static final class SourceSingleSubscriber<T> extends rx.SingleSubscriber<T>
    implements io.reactivex.rxjava3.disposables.Disposable {

        final io.reactivex.rxjava3.core.MaybeObserver<? super T> observer;

        SourceSingleSubscriber(io.reactivex.rxjava3.core.MaybeObserver<? super T> observer) {
            this.observer = observer;
        }

        @Override
        public void onSuccess(T value) {
            if (value == null) {
                observer.onError(new NullPointerException(
                        "The upstream 1.x Single signalled a null value which is not supported in 3.x"));
            } else {
                observer.onSuccess(value);
            }
        }

        @Override
        public void onError(Throwable error) {
            observer.onError(error);
        }

        @Override
        public void dispose() {
            unsubscribe();
        }

        @Override
        public boolean isDisposed() {
            return isUnsubscribed();
        }
    }
}
