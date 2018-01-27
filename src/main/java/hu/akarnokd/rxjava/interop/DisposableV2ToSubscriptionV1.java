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

import io.reactivex.disposables.Disposable;
import rx.Subscription;

/**
 * Wraps a v2 Disposable and exposes it as a v1 Subscription.
 * @since 0.11.0
 * @author Artem Zinnatulin
 */
final class DisposableV2ToSubscriptionV1 implements Subscription {

    private final Disposable disposable;

    DisposableV2ToSubscriptionV1(Disposable disposable) {
        this.disposable = disposable;
    }

    @Override
    public void unsubscribe() {
        disposable.dispose();
    }

    @Override
    public boolean isUnsubscribed() {
        return disposable.isDisposed();
    }
}
