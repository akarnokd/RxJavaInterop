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
 * Wrap a V1 Subject and expose it as a V2 Subject.
 * @param <T> the input/output value type
 * @since 0.9.0
 */
final class SubjectV1ToSubjectV2<T> extends io.reactivex.subjects.Subject<T> {

    final rx.subjects.Subject<T, T> source;

    volatile boolean terminated;
    Throwable error;

    SubjectV1ToSubjectV2(rx.subjects.Subject<T, T> source) {
        this.source = source;
    }

    @Override
    public void onSubscribe(io.reactivex.disposables.Disposable d) {
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
            error = e != null ? e : new NullPointerException();
            terminated = true;
            source.onError(e);
        } else {
            io.reactivex.plugins.RxJavaPlugins.onError(e);
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
    protected void subscribeActual(io.reactivex.Observer<? super T> observer) {
        hu.akarnokd.rxjava.interop.ObservableV1ToObservableV2.ObservableSubscriber<T> parent =
                new hu.akarnokd.rxjava.interop.ObservableV1ToObservableV2.ObservableSubscriber<T>(observer);
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
