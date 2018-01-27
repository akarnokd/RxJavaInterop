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
 * Wraps a v1 Subscription and exposes it as a v2 Disposable.
 * @since 0.11.0
 * @author Artem Zinnatulin
 */
final class SubscriptionV1ToDisposableV2 implements Disposable {

    private final Subscription subscription;

    SubscriptionV1ToDisposableV2(Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public void dispose() {
        subscription.unsubscribe();
    }

    @Override
    public boolean isDisposed() {
        return subscription.isUnsubscribed();
    }
}
