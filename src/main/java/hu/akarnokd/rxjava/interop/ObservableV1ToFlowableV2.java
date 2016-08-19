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
 * Convert a V1 Observable into a V2 Flowable, composing backpressure and cancellation.
 *
 * @param <T> the value type
 */
final class ObservableV1ToFlowableV2<T> extends io.reactivex.Flowable<T> {

    final rx.Observable<T> source;

    public ObservableV1ToFlowableV2(rx.Observable<T> source) {
        this.source = source;
    }
    
    @Override
    protected void subscribeActual(org.reactivestreams.Subscriber<? super T> s) {
        ObservableSubscriber<T> parent = new ObservableSubscriber<T>(s);
        ObservableSubscriberSubscription parentSubscription = new ObservableSubscriberSubscription(parent);
        s.onSubscribe(parentSubscription);
        
        source.unsafeSubscribe(parent);
    }
    
    static final class ObservableSubscriber<T> extends rx.Subscriber<T> {
        
        final org.reactivestreams.Subscriber<? super T> actual;

        boolean done;
        
        public ObservableSubscriber(org.reactivestreams.Subscriber<? super T> actual) {
            this.actual = actual;
            this.request(0L); // suppress starting out with Long.MAX_VALUE
        }
        
        @Override
        public void onNext(T t) {
            if (done) {
                return;
            }
            if (t == null) {
                unsubscribe();
                onError(new NullPointerException(
                    "The upstream 1.x Observable signalled a null value which is not supported in 2.x"));
            } else {
                actual.onNext(t);
            }
        }
        
        @Override
        public void onError(Throwable e) {
            if (done) {
                io.reactivex.plugins.RxJavaPlugins.onError(e);
                return;
            }
            done = true;
            actual.onError(e);
        }
        
        @Override
        public void onCompleted() {
            if (done) {
                return;
            }
            done = true;
            actual.onComplete();
        }
        
        void requestMore(long n) {
            request(n);
        }
    }
    
    static final class ObservableSubscriberSubscription implements org.reactivestreams.Subscription {

        final ObservableSubscriber<?> parent;

        public ObservableSubscriberSubscription(ObservableSubscriber<?> parent) {
            this.parent = parent;
        }
        
        @Override
        public void request(long n) {
            parent.requestMore(n);
        }
        
        @Override
        public void cancel() {
            parent.unsubscribe();
        }
    }
    
}
