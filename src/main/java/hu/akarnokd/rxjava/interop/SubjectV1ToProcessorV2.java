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

package hu.akarnokd.rxjava.interop;

/**
 * Wrap a V1 Subject and expose it as a V2 FlowableProcessor.
 * @param <T> the input/output value type
 * @since 0.9.0
 */
final class SubjectV1ToProcessorV2<T> extends io.reactivex.processors.FlowableProcessor<T> {

    final rx.subjects.Subject<T, T> source;

    volatile boolean terminated;
    Throwable error;

    SubjectV1ToProcessorV2(rx.subjects.Subject<T, T> source) {
        this.source = source;
    }

    @Override
    public void onSubscribe(org.reactivestreams.Subscription s) {
        if (terminated) {
            s.cancel();
        } else {
            s.request(Long.MAX_VALUE);
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
    protected void subscribeActual(org.reactivestreams.Subscriber<? super T> s) {
        hu.akarnokd.rxjava.interop.ObservableV1ToFlowableV2.ObservableSubscriber<T> parent =
                new hu.akarnokd.rxjava.interop.ObservableV1ToFlowableV2.ObservableSubscriber<T>(s);
        hu.akarnokd.rxjava.interop.ObservableV1ToFlowableV2.ObservableSubscriberSubscription parentSubscription =
                new hu.akarnokd.rxjava.interop.ObservableV1ToFlowableV2.ObservableSubscriberSubscription(parent);
        s.onSubscribe(parentSubscription);

        source.unsafeSubscribe(parent);
    }

    @Override
    public boolean hasSubscribers() {
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
