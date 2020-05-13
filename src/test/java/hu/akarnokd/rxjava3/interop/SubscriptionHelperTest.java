/*
 * Copyright 2016-2020 David Karnok
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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.atomic.*;

import org.junit.Test;
import org.reactivestreams.Subscription;

import io.reactivex.rxjava3.exceptions.ProtocolViolationException;
import io.reactivex.rxjava3.internal.subscriptions.BooleanSubscription;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class SubscriptionHelperTest {

    @Test
    public void checkEnum() {
        TestHelper.checkEnum(SubscriptionHelper.class);
    }

    @Test
    public void validateNullThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            SubscriptionHelper.validate(null, null);

            TestHelper.assertError(errors, 0, NullPointerException.class, "next is null");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void cancelNoOp() {
        SubscriptionHelper.CANCELLED.cancel();
    }

    @Test
    public void cancelRace() {
        for (int i = 0; i < TestHelper.RACE_DEFAULT_LOOPS; i++) {
            final AtomicReference<Subscription> atomicSubscription = new AtomicReference<>();

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    SubscriptionHelper.cancel(atomicSubscription);
                }
            };

            TestHelper.race(r, r);
        }
    }

    @Test
    public void invalidDeferredRequest() {
        AtomicReference<Subscription> atomicSubscription = new AtomicReference<>();
        AtomicLong r = new AtomicLong();

        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            SubscriptionHelper.deferredRequest(atomicSubscription, r, -99);

            TestHelper.assertError(errors, 0, IllegalArgumentException.class, "n > 0 required but it was -99");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void deferredRace() {
        for (int i = 0; i < TestHelper.RACE_DEFAULT_LOOPS; i++) {
            final AtomicReference<Subscription> atomicSubscription = new AtomicReference<>();
            final AtomicLong r = new AtomicLong();

            final AtomicLong q = new AtomicLong();

            final Subscription a = new Subscription() {
                @Override
                public void request(long n) {
                    q.addAndGet(n);
                }

                @Override
                public void cancel() {

                }
            };

            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    SubscriptionHelper.deferredSetOnce(atomicSubscription, r, a);
                }
            };

            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    SubscriptionHelper.deferredRequest(atomicSubscription, r, 1);
                }
            };

            TestHelper.race(r1, r2);

            assertSame(a, atomicSubscription.get());
            assertEquals(1, q.get());
            assertEquals(0, r.get());
        }
    }

    @Test
    public void setOnceAndRequest() {
        AtomicReference<Subscription> ref = new AtomicReference<>();

        Subscription sub = mock(Subscription.class);

        assertTrue(SubscriptionHelper.setOnce(ref, sub, 1));

        verify(sub).request(1);
        verify(sub, never()).cancel();

        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            sub = mock(Subscription.class);

            assertFalse(SubscriptionHelper.setOnce(ref, sub, 1));

            verify(sub, never()).request(anyLong());
            verify(sub).cancel();

            TestHelper.assertError(errors, 0, ProtocolViolationException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void reportDisposableSet() throws Throwable {
        TestHelper.withErrorTracking(errors -> {

            SubscriptionHelper.validate(new BooleanSubscription(), new BooleanSubscription());

            TestHelper.assertError(errors, 0, ProtocolViolationException.class);
        });
    }

    @Test
    public void validate() {
        assertTrue(SubscriptionHelper.validate(null, new BooleanSubscription()));
    }
}