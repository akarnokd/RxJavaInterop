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

import java.util.concurrent.atomic.*;

/**
 * Convert a V2 Flowable into a V1 Observable, composing backpressure and cancellation.
 *
 * @param <T> the value type
 */
final class FlowableV2ToObservableV1<T> implements rx.Observable.OnSubscribe<T> {

    final org.reactivestreams.Publisher<T> source;
    
    public FlowableV2ToObservableV1(org.reactivestreams.Publisher<T> source) {
        this.source = source;
    }
    
    @Override
    public void call(rx.Subscriber<? super T> t) {
        SourceSubscriber<T> parent = new SourceSubscriber<T>(t);
        
        t.add(parent);
        t.setProducer(parent);
        
        source.subscribe(parent);
    }
    
    static final class SourceSubscriber<T> 
    extends AtomicReference<org.reactivestreams.Subscription>
    implements org.reactivestreams.Subscriber<T>, rx.Subscription, rx.Producer {
        
        /** */
        private static final long serialVersionUID = -6567012932544037069L;

        final rx.Subscriber<? super T> actual;
        
        final AtomicLong requested;
        
        public SourceSubscriber(rx.Subscriber<? super T> actual) {
            this.actual = actual;
            this.requested = new AtomicLong();
        }

        @Override
        public void request(long n) {
            io.reactivex.internal.subscriptions.SubscriptionHelper.deferredRequest(this, requested, n);
        }

        @Override
        public void unsubscribe() {
            io.reactivex.internal.subscriptions.SubscriptionHelper.cancel(this);
        }

        @Override
        public boolean isUnsubscribed() {
            return io.reactivex.internal.subscriptions.SubscriptionHelper.isCancelled(get());
        }

        @Override
        public void onSubscribe(org.reactivestreams.Subscription s) {
            io.reactivex.internal.subscriptions.SubscriptionHelper.deferredSetOnce(this, requested, s);
        }

        @Override
        public void onNext(T t) {
            actual.onNext(t);
        }

        @Override
        public void onError(Throwable t) {
            actual.onError(t);
        }

        @Override
        public void onComplete() {
            actual.onCompleted();
        }
    }
}
