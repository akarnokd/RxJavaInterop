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
 * Wrap a V1 Subject and expose it as a V3 Subject.
 * @param <T> the input/output value type
 * @since 0.9.0
 */
final class SubjectV1ToSubjectV3<T> extends io.reactivex.rxjava3.subjects.Subject<T> {

    final rx.subjects.Subject<T, T> source;

    volatile boolean terminated;
    Throwable error;

    SubjectV1ToSubjectV3(rx.subjects.Subject<T, T> source) {
        this.source = source;
    }

    @Override
    public void onSubscribe(io.reactivex.rxjava3.disposables.Disposable d) {
        if (terminated) {
            d.dispose();
        }
    }

    @Override
    public void onNext(T t) {
        if (!terminated) {
            if (t == null) {
                onError(new NullPointerException());
            } else {
                source.onNext(t);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        if (!terminated) {
            if (e == null) {
                e = new NullPointerException("Throwable was null");
            }
            error = e;
            terminated = true;
            source.onError(e);
        } else {
            io.reactivex.rxjava3.plugins.RxJavaPlugins.onError(e);
        }
    }

    @Override
    public void onComplete() {
        if (!terminated) {
            terminated = true;
            source.onCompleted();
        }
    }

    @Override
    protected void subscribeActual(io.reactivex.rxjava3.core.Observer<? super T> observer) {
        hu.akarnokd.rxjava3.interop.ObservableV1ToObservableV3.ObservableSubscriber<T> parent =
                new hu.akarnokd.rxjava3.interop.ObservableV1ToObservableV3.ObservableSubscriber<>(observer);
        observer.onSubscribe(parent);

        source.unsafeSubscribe(parent);
    }

    @Override
    public boolean hasObservers() {
        return source.hasObservers();
    }

    @Override
    public boolean hasComplete() {
        return terminated && error == null;
    }

    @Override
    public boolean hasThrowable() {
        return terminated && error != null;
    }

    @Override
    public Throwable getThrowable() {
        return terminated ? error : null;
    }
}
