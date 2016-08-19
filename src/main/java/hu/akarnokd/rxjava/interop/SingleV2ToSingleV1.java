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

import java.util.concurrent.atomic.AtomicReference;

/**
 * Convert a V2 Single into a V1 Single, composing cancellation.
 *
 * @param <T> the value type
 */
final class SingleV2ToSingleV1<T> implements rx.Single.OnSubscribe<T> {

    final io.reactivex.SingleSource<T> source;
    
    public SingleV2ToSingleV1(io.reactivex.SingleSource<T> source) {
        this.source = source;
    }
    
    @Override
    public void call(rx.SingleSubscriber<? super T> t) {
        SourceSingleObserver<T> parent = new SourceSingleObserver<T>(t);
        t.add(parent);
        source.subscribe(parent);
    }
    
    static final class SourceSingleObserver<T> 
    extends AtomicReference<io.reactivex.disposables.Disposable>
    implements io.reactivex.SingleObserver<T>, rx.Subscription {

        /** */
        private static final long serialVersionUID = 4758098209431016997L;

        final rx.SingleSubscriber<? super T> actual;
        
        public SourceSingleObserver(rx.SingleSubscriber<? super T> actual) {
            this.actual = actual;
        }
        
        @Override
        public void unsubscribe() {
            io.reactivex.internal.disposables.DisposableHelper.dispose(this);
        }

        @Override
        public boolean isUnsubscribed() {
            return io.reactivex.internal.disposables.DisposableHelper.isDisposed(get());
        }

        @Override
        public void onSubscribe(io.reactivex.disposables.Disposable d) {
            io.reactivex.internal.disposables.DisposableHelper.setOnce(this, d);
        }

        @Override
        public void onSuccess(T value) {
            actual.onSuccess(value);
        }

        @Override
        public void onError(Throwable e) {
            actual.onError(e);
        }
        
    }
}
