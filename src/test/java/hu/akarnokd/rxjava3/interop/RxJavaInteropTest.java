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

import static hu.akarnokd.rxjava3.interop.RxJavaInterop.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.reactivestreams.Subscription;

import hu.akarnokd.rxjava3.interop.RxJavaInterop;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.*;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.internal.subscriptions.BooleanSubscription;
import io.reactivex.rxjava3.observers.BaseTestConsumer;
import rx.Observable;
import rx.Observable.*;
import rx.Subscriber;
import rx.functions.*;
import rx.subscriptions.Subscriptions;

public class RxJavaInteropTest {

    @Test
    public void privateConstructor() {
        try {
            Constructor<?> c = hu.akarnokd.rxjava3.interop.RxJavaInterop.class.getDeclaredConstructor();
            c.setAccessible(true);
            c.newInstance();
        } catch (InvocationTargetException ex) {
            assertTrue(ex.getCause() instanceof IllegalStateException);
            assertEquals("No instances!", ex.getCause().getMessage());
        } catch (Throwable ex) {
            fail(ex.toString());
        }
    }

    // ----------------------------------------------------------
    // Interop method null-check validation
    // ----------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void o1f3Null() {
        toV3Flowable(null);
    }

    @Test(expected = NullPointerException.class)
    public void o1o3Null() {
        toV3Observable(null);
    }

    @Test(expected = NullPointerException.class)
    public void s1s3Null() {
        toV3Single(null);
    }

    @Test(expected = NullPointerException.class)
    public void s1m3Null() {
        toV3Maybe((rx.Single<?>)null);
    }

    @Test(expected = NullPointerException.class)
    public void c1c3Null() {
        toV3Completable(null);
    }

    @Test(expected = NullPointerException.class)
    public void c1m3Null() {
        toV3Maybe((rx.Completable)null);
    }

    @Test(expected = NullPointerException.class)
    public void f3o1Null() {
        toV1Observable(null);
    }

    @Test(expected = NullPointerException.class)
    public void o3o1Null() {
        toV1Observable(null, io.reactivex.rxjava3.core.BackpressureStrategy.BUFFER);
    }

    @Test(expected = NullPointerException.class)
    public void o3o1NullStrategy() {
        toV1Observable(io.reactivex.rxjava3.core.Observable.empty(), null);
    }

    @Test(expected = NullPointerException.class)
    public void s3s1Null() {
        toV1Single((SingleSource<?>)null);
    }

    @Test(expected = NullPointerException.class)
    public void m3s1Null() {
        toV1Single((MaybeSource<?>)null);
    }

    @Test(expected = NullPointerException.class)
    public void c3c1Null() {
        toV1Completable((io.reactivex.rxjava3.core.CompletableSource)null);
    }

    @Test(expected = NullPointerException.class)
    public void m3c1Null() {
        toV1Completable((MaybeSource<?>)null);
    }

    // ----------------------------------------------------------
    // 1.x Observable -> 3.x Flowable
    // ----------------------------------------------------------

    @Test
    public void o1f3Normal() {
        toV3Flowable(rx.Observable.range(1, 5))
        .test()
        .assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void o1f3NormalTake() {
        toV3Flowable(rx.Observable.range(1, 5))
        .take(2)
        .test()
        .assertResult(1, 2);
    }

    @Test
    public void o1f3NormalBackpressured() {
        toV3Flowable(rx.Observable.range(1, 5))
        .test(1)
        .assertValue(1)
        .assertNoErrors()
        .assertNotComplete();
    }

    @Test
    public void o1f3Empty() {
        toV3Flowable(rx.Observable.empty())
        .test()
        .assertResult();
    }

    @SuppressWarnings("unchecked")
    static void assertFailureAndMessage(BaseTestConsumer<?, ?> testConsumer, Class<? extends Throwable> errorClass, final String message) {
        testConsumer
        .assertFailure(errorClass)
        .assertError(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable error) throws Throwable {
                return error.getMessage().equals(message);
            }
        });
    }
    
    @Test
    public void o1f3Error() {
        assertFailureAndMessage(toV3Flowable(rx.Observable.error(new RuntimeException("Forced failure")))
        .test(),
        RuntimeException.class, "Forced failure");
    }

    @Test
    public void o1f3ErrorNull() {
        toV3Flowable(rx.Observable.just(null))
        .test()
        .assertFailure(NullPointerException.class);
    }

    @Test
    public void o1f3IgnoreCancel() {
        toV3Flowable(rx.Observable.unsafeCreate(new OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> s) {
                s.onNext(null);
                s.onNext(1);
                s.onError(new RuntimeException("Forced failure"));
                s.onCompleted();
            }
        }))
        .test()
        .assertFailure(NullPointerException.class);
    }

    // ----------------------------------------------------------
    // 1.x Observable -> 3.x Observable
    // ----------------------------------------------------------

    @Test
    public void o1o3Normal() {
        toV3Observable(rx.Observable.range(1, 5))
        .test()
        .assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void o1o3NormalTake() {
        toV3Observable(rx.Observable.range(1, 5))
        .take(2)
        .test()
        .assertResult(1, 2);
    }

    @Test
    public void o1o3Empty() {
        toV3Observable(rx.Observable.empty())
        .test()
        .assertResult();
    }

    @Test
    public void o1o3Error() {
        assertFailureAndMessage(toV3Observable(rx.Observable.error(new RuntimeException("Forced failure")))
        .test(),
        RuntimeException.class, "Forced failure");
    }

    @Test
    public void o1o3ErrorNull() {
        toV3Observable(rx.Observable.just(null))
        .test()
        .assertFailure(NullPointerException.class);
    }

    @Test
    public void o1fo3IgnoreCancel() {
        toV3Observable(rx.Observable.unsafeCreate(new OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> s) {
                s.onNext(null);
                s.onNext(1);
                s.onError(new RuntimeException("Forced failure"));
                s.onCompleted();
            }
        }))
        .test()
        .assertFailure(NullPointerException.class);
    }

    @Test
    public void o1o3IsDisposed() {
        toV3Observable(rx.Observable.just(1))
        .subscribe(new io.reactivex.rxjava3.core.Observer<Integer>() {
            @Override
            public void onSubscribe(io.reactivex.rxjava3.disposables.Disposable d) {
                assertFalse(d.isDisposed());
            }

            @Override
            public void onNext(Integer value) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }


    // ----------------------------------------------------------
    // 1.x Single -> 3.x Single
    // ----------------------------------------------------------

    @Test
    public void s1s3Normal() {
        toV3Single(rx.Single.just(1)).test()
        .assertResult(1);
    }

    @Test
    public void s1s3Cancel() {
        rx.subjects.PublishSubject<Integer> ps = rx.subjects.PublishSubject.create();
        io.reactivex.rxjava3.observers.TestObserver<Integer> to = toV3Single(ps.toSingle()).test();

        assertTrue("1.x PublishSubject has no observers!", ps.hasObservers());

        to.dispose();

        assertFalse("1.x PublishSubject has observers!", ps.hasObservers());

    }

    @Test
    public void s1s3Error() {
        assertFailureAndMessage(toV3Single(rx.Single.error(new RuntimeException("Forced failure"))).test()
        , RuntimeException.class, "Forced failure");
    }

    @Test
    public void s1s3ErrorNull() {
        toV3Single(rx.Single.just(null)).test()
        .assertFailure(NullPointerException.class);
    }

    @Test
    public void s1s3IsDisposed() {
        toV3Single(rx.Single.just(1))
        .subscribe(new io.reactivex.rxjava3.core.SingleObserver<Integer>() {
            @Override
            public void onSubscribe(io.reactivex.rxjava3.disposables.Disposable d) {
                assertFalse(d.isDisposed());
            }

            @Override
            public void onSuccess(Integer v) {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }


    // ----------------------------------------------------------
    // 1.x Single -> 3.x Maybe
    // ----------------------------------------------------------

    @Test
    public void s1m3Normal() {
        toV3Maybe(rx.Single.just(1)).test()
            .assertResult(1);
    }

    @Test
    public void s1m3Cancel() {
        rx.subjects.PublishSubject<Integer> ps = rx.subjects.PublishSubject.create();
        io.reactivex.rxjava3.observers.TestObserver<Integer> to = toV3Maybe(ps.toSingle()).test();

        assertTrue("1.x PublishSubject has no observers!", ps.hasObservers());

        to.dispose();

        assertFalse("1.x PublishSubject has observers!", ps.hasObservers());

    }

    @Test
    public void s1m3Error() {
        assertFailureAndMessage(toV3Maybe(rx.Single.error(new RuntimeException("Forced failure"))).test()
            , RuntimeException.class, "Forced failure");
    }

    @Test
    public void s1m3ErrorNull() {
        toV3Maybe(rx.Single.just(null)).test()
            .assertFailure(NullPointerException.class);
    }

    @Test
    public void s1m3IsDisposed() {
        toV3Maybe(rx.Single.just(1))
            .subscribe(new io.reactivex.rxjava3.core.MaybeObserver<Integer>() {
                @Override
                public void onSubscribe(io.reactivex.rxjava3.disposables.Disposable d) {
                    assertFalse(d.isDisposed());
                }

                @Override
                public void onSuccess(Integer v) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void onError(Throwable e) {

                }
            });
    }

    // ----------------------------------------------------------
    // 1.x Completable -> 3.x Completable
    // ----------------------------------------------------------

    @Test
    public void c1c3Normal() {
        toV3Completable(rx.Completable.complete()).test()
        .assertResult();
    }

    @Test
    public void c1c3Cancel() {
        rx.subjects.PublishSubject<Integer> ps = rx.subjects.PublishSubject.create();
        io.reactivex.rxjava3.observers.TestObserver<Void> to = toV3Completable(ps.toCompletable()).test();

        assertTrue("1.x PublishSubject has no observers!", ps.hasObservers());

        to.dispose();

        assertFalse("1.x PublishSubject has observers!", ps.hasObservers());
    }

    @Test
    public void c1c3Error() {
        assertFailureAndMessage(toV3Completable(rx.Completable.error(new RuntimeException("Forced failure"))).test()
        , RuntimeException.class, "Forced failure");
    }

    @Test
    public void c1c3IsDisposed() {
        toV3Completable(rx.Completable.complete())
        .subscribe(new io.reactivex.rxjava3.core.CompletableObserver() {
            @Override
            public void onSubscribe(io.reactivex.rxjava3.disposables.Disposable d) {
                assertFalse(d.isDisposed());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    // ----------------------------------------------------------
    // 1.x Completable -> 3.x Maybe
    // ----------------------------------------------------------

    @Test
    public void c1m3Normal() {
        toV3Maybe(rx.Completable.complete()).test()
            .assertResult();
    }

    @Test
    public void c1m3Cancel() {
        rx.subjects.PublishSubject<Integer> ps = rx.subjects.PublishSubject.create();
        io.reactivex.rxjava3.observers.TestObserver<Void> ts = RxJavaInterop.<Void>toV3Maybe(ps.toCompletable()).test();

        assertTrue("1.x PublishSubject has no observers!", ps.hasObservers());

        ts.dispose();

        assertFalse("1.x PublishSubject has observers!", ps.hasObservers());
    }

    @Test
    public void c1m3Error() {
        assertFailureAndMessage(toV3Maybe(rx.Completable.error(new RuntimeException("Forced failure"))).test()
            , RuntimeException.class, "Forced failure");
    }


    @Test
    public void c1m3IsDisposed() {
        RxJavaInterop.<Void>toV3Maybe(rx.Completable.complete())
            .subscribe(new io.reactivex.rxjava3.core.MaybeObserver<Void>() {
                @Override
                public void onSubscribe(io.reactivex.rxjava3.disposables.Disposable d) {
                    assertFalse(d.isDisposed());
                }

                @Override
                public void onSuccess(Void value) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void onError(Throwable e) {

                }
            });
    }

    // ----------------------------------------------------------
    // 3.x Flowable -> 1.x Observable
    // ----------------------------------------------------------

    static <T> rx.observers.TestSubscriber<T> test1(rx.Observable<T> s) {
        rx.observers.TestSubscriber<T> ts = new rx.observers.TestSubscriber<T>();
        s.subscribe(ts);
        return ts;
    }

    static <T> rx.observers.TestSubscriber<T> test1(rx.Observable<T> s, long initialRequest) {
        rx.observers.TestSubscriber<T> ts = new rx.observers.TestSubscriber<T>(initialRequest);
        s.subscribe(ts);
        return ts;
    }

    @Test
    public void f3o1Normal() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Observable(
                io.reactivex.rxjava3.core.Flowable.range(1, 5)));

        ts.assertValues(1, 2, 3, 4, 5);
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void f3o1NormalTake() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Observable(
                io.reactivex.rxjava3.core.Flowable.range(1, 5)).take(2));

        ts.assertValues(1, 2);
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void f3o1NormalBackpressured() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Observable(
                io.reactivex.rxjava3.core.Flowable.range(1, 5)), 1);
        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertNotCompleted();
    }

    @Test
    public void f3o1Empty() {
        rx.observers.TestSubscriber<Object> ts = test1(toV1Observable(
                io.reactivex.rxjava3.core.Flowable.empty()));
        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void f3o1Error() {
        rx.observers.TestSubscriber<Object> ts = test1(toV1Observable(
                io.reactivex.rxjava3.core.Flowable.error(new RuntimeException("Forced failure"))));
        ts.assertNoValues();
        ts.assertError(RuntimeException.class);
        assertEquals("Forced failure", ts.getOnErrorEvents().get(0).getMessage());
    }

    // ----------------------------------------------------------
    // 3.x Observable -> 1.x Observable
    // ----------------------------------------------------------

    @Test
    public void o3o1Normal() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Observable(
                io.reactivex.rxjava3.core.Observable.range(1, 5), io.reactivex.rxjava3.core.BackpressureStrategy.BUFFER));

        ts.assertValues(1, 2, 3, 4, 5);
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void o3o1NormalTake() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Observable(
                io.reactivex.rxjava3.core.Observable.range(1, 5), io.reactivex.rxjava3.core.BackpressureStrategy.BUFFER).take(2));

        ts.assertValues(1, 2);
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void o3o1NormalBackpressured() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Observable(
                io.reactivex.rxjava3.core.Observable.range(1, 5), io.reactivex.rxjava3.core.BackpressureStrategy.BUFFER), 1);
        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertNotCompleted();
    }

    @Test
    public void o3o1Empty() {
        rx.observers.TestSubscriber<Object> ts = test1(toV1Observable(
                io.reactivex.rxjava3.core.Observable.empty(), io.reactivex.rxjava3.core.BackpressureStrategy.BUFFER));
        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void o3o1Error() {
        rx.observers.TestSubscriber<Object> ts = test1(toV1Observable(
                io.reactivex.rxjava3.core.Observable.error(new RuntimeException("Forced failure")), io.reactivex.rxjava3.core.BackpressureStrategy.BUFFER));
        ts.assertNoValues();
        ts.assertError(RuntimeException.class);
        assertEquals("Forced failure", ts.getOnErrorEvents().get(0).getMessage());
    }

    // ----------------------------------------------------------
    // 3.x Single -> 1.x Single
    // ----------------------------------------------------------

    static <T> rx.observers.TestSubscriber<T> test1(rx.Single<T> s) {
        rx.observers.TestSubscriber<T> ts = new rx.observers.TestSubscriber<T>();
        s.subscribe(ts);
        return ts;
    }

    @Test
    public void s3s1Normal() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Single(io.reactivex.rxjava3.core.Single.just(1)));
        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void s3s1Cancel() {
        io.reactivex.rxjava3.subjects.PublishSubject<Integer> ps = io.reactivex.rxjava3.subjects.PublishSubject.create();
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Single(ps.single(-99)));

        assertTrue("3.x PublishSubject has no observers!", ps.hasObservers());

        ts.unsubscribe();

        assertFalse("3.x PublishSubject has observers!", ps.hasObservers());

    }

    @Test
    public void s3s1Error() {
        rx.observers.TestSubscriber<Object> ts = test1(toV1Single(
                io.reactivex.rxjava3.core.Single.error(new RuntimeException("Forced failure"))));

        ts.assertNoValues();
        ts.assertError(RuntimeException.class);
        assertEquals("Forced failure", ts.getOnErrorEvents().get(0).getMessage());
    }

    // ----------------------------------------------------------
    // 3.x Completable -> 1.x Completable
    // ----------------------------------------------------------

    static <T> rx.observers.TestSubscriber<T> test1(rx.Completable s) {
        rx.observers.TestSubscriber<T> ts = new rx.observers.TestSubscriber<T>();
        s.subscribe(ts);
        return ts;
    }

    @Test
    public void c3c1Normal() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Completable(
                io.reactivex.rxjava3.core.Completable.complete()));
        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void c3c1Cancel() {
        io.reactivex.rxjava3.subjects.PublishSubject<Integer> ps = io.reactivex.rxjava3.subjects.PublishSubject.create();
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Completable(ps.ignoreElements()));

        assertTrue("3.x PublishSubject has no observers!", ps.hasObservers());

        ts.unsubscribe();

        assertFalse("3.x PublishSubject has observers!", ps.hasObservers());

    }

    @Test
    public void c3c1Error() {
        rx.observers.TestSubscriber<Object> ts = test1(toV1Completable(
                io.reactivex.rxjava3.core.Completable.error(new RuntimeException("Forced failure"))));

        ts.assertNoValues();
        ts.assertError(RuntimeException.class);
        assertEquals("Forced failure", ts.getOnErrorEvents().get(0).getMessage());
    }

    // ----------------------------------------------------------
    // 3.x Maybe -> 1.x Single
    // ----------------------------------------------------------

    @Test
    public void m3s1Normal() {
        rx.observers.TestSubscriber<Integer> ts = test1(
                toV1Single(io.reactivex.rxjava3.core.Maybe.just(1)));

        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertCompleted();
    }


    @Test
    public void m3s1Error() {
        rx.observers.TestSubscriber<Integer> ts = test1(
                toV1Single(io.reactivex.rxjava3.core.Maybe.<Integer>error(new RuntimeException())));

        ts.assertNoValues();
        ts.assertError(RuntimeException.class);
        ts.assertNotCompleted();
    }

    @Test
    public void m3s1Empty() {
        rx.observers.TestSubscriber<Integer> ts = test1(
                toV1Single(io.reactivex.rxjava3.core.Maybe.<Integer>empty()));

        ts.assertNoValues();
        ts.assertError(NoSuchElementException.class);
        ts.assertNotCompleted();
    }

    @Test
    public void m3s1Cancel() {
        io.reactivex.rxjava3.subjects.PublishSubject<Integer> ps = io.reactivex.rxjava3.subjects.PublishSubject.create();
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Single(ps.singleElement()));

        assertTrue("3.x PublishSubject has no observers!", ps.hasObservers());

        ts.unsubscribe();

        assertFalse("3.x PublishSubject has observers!", ps.hasObservers());
    }

    // ----------------------------------------------------------
    // 3.x Maybe -> 1.x Completable
    // ----------------------------------------------------------

    @Test
    public void m3c1Normal() {
        rx.observers.TestSubscriber<Integer> ts = test1(
                toV1Completable(io.reactivex.rxjava3.core.Maybe.just(1)));

        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertCompleted();
    }


    @Test
    public void m3c1Error() {
        rx.observers.TestSubscriber<Integer> ts = test1(
                toV1Completable(io.reactivex.rxjava3.core.Maybe.<Integer>error(new RuntimeException())));

        ts.assertNoValues();
        ts.assertError(RuntimeException.class);
        ts.assertNotCompleted();
    }

    @Test
    public void m3c1Empty() {
        rx.observers.TestSubscriber<Integer> ts = test1(
                toV1Completable(io.reactivex.rxjava3.core.Maybe.<Integer>empty()));

        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void m3c1Cancel() {
        io.reactivex.rxjava3.subjects.PublishSubject<Integer> ps = io.reactivex.rxjava3.subjects.PublishSubject.create();
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Completable(ps.singleElement()));

        assertTrue("3.x PublishSubject has no observers!", ps.hasObservers());

        ts.unsubscribe();

        assertFalse("3.x PublishSubject has observers!", ps.hasObservers());
    }

    @Test
    public void v1RequestZero() {
        toV1Observable(Flowable.range(1, 5))
        .filter(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer v) {
                return v % 2 == 0;
            }
        })
        .test()
        .assertResult(2, 4);
    }

    // ----------------------------------------------------------
    // 1.x Subject -> 3.x Subject
    // ----------------------------------------------------------

    @Test
    public void sj1ToSj3Normal() {
        rx.subjects.PublishSubject<Integer> ps1 = rx.subjects.PublishSubject.create();
        io.reactivex.rxjava3.subjects.Subject<Integer> sj3 = toV3Subject(ps1);

        io.reactivex.rxjava3.observers.TestObserver<Integer> to = sj3.test();

        assertTrue(sj3.hasObservers());
        assertTrue(ps1.hasObservers());
        assertFalse(sj3.hasComplete());
        assertFalse(sj3.hasThrowable());
        assertNull(sj3.getThrowable());

        sj3.onNext(1);
        sj3.onNext(2);
        sj3.onComplete();

        assertFalse(sj3.hasObservers());
        assertFalse(ps1.hasObservers());

        assertTrue(sj3.hasComplete());
        assertFalse(sj3.hasThrowable());
        assertNull(sj3.getThrowable());

        to.assertResult(1, 2);
    }

    @Test
    public void sj1ToSj3Error() {
        rx.subjects.PublishSubject<Integer> ps1 = rx.subjects.PublishSubject.create();
        io.reactivex.rxjava3.subjects.Subject<Integer> sj3 = toV3Subject(ps1);

        io.reactivex.rxjava3.observers.TestObserver<Integer> to = sj3.test();

        assertTrue(sj3.hasObservers());
        assertTrue(ps1.hasObservers());
        assertFalse(sj3.hasComplete());
        assertFalse(sj3.hasThrowable());
        assertNull(sj3.getThrowable());

        sj3.onError(new IOException());

        assertFalse(sj3.hasObservers());
        assertFalse(ps1.hasObservers());

        assertFalse(sj3.hasComplete());
        assertTrue(sj3.hasThrowable());
        assertNotNull(sj3.getThrowable());
        assertTrue(sj3.getThrowable() instanceof IOException);

        to.assertFailure(IOException.class);
    }

    @Test
    public void sj1ToSj3Lifecycle() {
        rx.subjects.PublishSubject<Integer> ps1 = rx.subjects.PublishSubject.create();
        io.reactivex.rxjava3.subjects.Subject<Integer> sj3 = toV3Subject(ps1);

        io.reactivex.rxjava3.observers.TestObserver<Integer> to = sj3.test();

        assertTrue(sj3.hasObservers());
        assertTrue(ps1.hasObservers());
        assertFalse(sj3.hasComplete());
        assertFalse(sj3.hasThrowable());
        assertNull(sj3.getThrowable());

        Disposable d1 = Disposables.empty();
        sj3.onSubscribe(d1);

        assertFalse(d1.isDisposed());

        sj3.onNext(1);
        sj3.onNext(2);
        sj3.onComplete();
        sj3.onComplete();
        sj3.onError(new IOException());
        sj3.onNext(3);

        Disposable d3 = Disposables.empty();
        sj3.onSubscribe(d3);

        assertFalse(d1.isDisposed());
        assertTrue(d3.isDisposed());

        assertFalse(sj3.hasObservers());
        assertFalse(ps1.hasObservers());

        assertTrue(sj3.hasComplete());
        assertFalse(sj3.hasThrowable());
        assertNull(sj3.getThrowable());

        to.assertResult(1, 2);
    }

    @Test
    public void sj1ToSj3NullValue() {
        rx.subjects.PublishSubject<Integer> ps1 = rx.subjects.PublishSubject.create();
        io.reactivex.rxjava3.subjects.Subject<Integer> sj3 = toV3Subject(ps1);

        io.reactivex.rxjava3.observers.TestObserver<Integer> to = sj3.test();

        sj3.onNext(null);

        assertFalse(sj3.hasObservers());
        assertFalse(ps1.hasObservers());

        to.assertFailure(NullPointerException.class);
    }

    @Test
    public void sj1ToSj3NullException() {
        rx.subjects.PublishSubject<Integer> ps1 = rx.subjects.PublishSubject.create();
        io.reactivex.rxjava3.subjects.Subject<Integer> sj3 = toV3Subject(ps1);

        io.reactivex.rxjava3.observers.TestObserver<Integer> to = sj3.test();

        sj3.onError(null);

        assertFalse(sj3.hasObservers());
        assertFalse(ps1.hasObservers());

        to.assertFailure(NullPointerException.class);
    }

    // ----------------------------------------------------------
    // 3.x Subject -> 1.x Subject
    // ----------------------------------------------------------

    @Test
    public void sj3ToSj1Normal() {
        io.reactivex.rxjava3.subjects.PublishSubject<Integer> ps3 = io.reactivex.rxjava3.subjects.PublishSubject.create();
        rx.subjects.Subject<Integer, Integer> sj1 = toV1Subject(ps3);

        rx.observers.AssertableSubscriber<Integer> to = sj1.test();

        assertTrue(sj1.hasObservers());
        assertTrue(ps3.hasObservers());

        sj1.onNext(1);
        sj1.onNext(2);
        sj1.onCompleted();

        assertFalse(sj1.hasObservers());
        assertFalse(ps3.hasObservers());

        to.assertResult(1, 2);
    }

    @Test
    public void sj3ToSj1Error() {
        io.reactivex.rxjava3.subjects.PublishSubject<Integer> ps3 = io.reactivex.rxjava3.subjects.PublishSubject.create();
        rx.subjects.Subject<Integer, Integer> sj1 = toV1Subject(ps3);

        rx.observers.AssertableSubscriber<Integer> to = sj1.test();

        assertTrue(sj1.hasObservers());
        assertTrue(ps3.hasObservers());

        sj1.onError(new IOException());

        assertFalse(sj1.hasObservers());
        assertFalse(ps3.hasObservers());

        to.assertFailure(IOException.class);
    }

    @Test
    public void sj3ToSj1Backpressured() {
        io.reactivex.rxjava3.subjects.PublishSubject<Integer> pp3 = io.reactivex.rxjava3.subjects.PublishSubject.create();
        rx.subjects.Subject<Integer, Integer> sj1 = toV1Subject(pp3);

        rx.observers.AssertableSubscriber<Integer> to = sj1.test(0L);

        assertTrue(sj1.hasObservers());
        assertTrue(pp3.hasObservers());

        sj1.onNext(1);

        assertFalse(sj1.hasObservers());
        assertFalse(pp3.hasObservers());

        assertFalse(pp3.hasComplete());
        assertFalse(pp3.hasThrowable());
        assertNull(pp3.getThrowable());

        to.assertFailure(rx.exceptions.MissingBackpressureException.class);
    }

    @Test
    public void sj3ToSj1Lifecycle() {
        io.reactivex.rxjava3.subjects.PublishSubject<Integer> pp3 = io.reactivex.rxjava3.subjects.PublishSubject.create();
        rx.subjects.Subject<Integer, Integer> sj1 = toV1Subject(pp3);

        rx.observers.AssertableSubscriber<Integer> to = sj1.test(0L);

        assertTrue(sj1.hasObservers());
        assertTrue(pp3.hasObservers());

        sj1.onNext(1);
        sj1.onError(new IOException());

        assertFalse(sj1.hasObservers());
        assertFalse(pp3.hasObservers());

        assertFalse(pp3.hasComplete());
        assertTrue(pp3.hasThrowable());
        assertNotNull(pp3.getThrowable());

        to.assertFailure(rx.exceptions.MissingBackpressureException.class);
    }

    // ----------------------------------------------------------
    // 3.x FlowableProcessor -> 1.x Subject
    // ----------------------------------------------------------

    @Test
    public void fp3ToSj1Normal() {
        io.reactivex.rxjava3.processors.FlowableProcessor<Integer> pp3 = io.reactivex.rxjava3.processors.PublishProcessor.create();
        rx.subjects.Subject<Integer, Integer> sj1 = toV1Subject(pp3);

        rx.observers.AssertableSubscriber<Integer> to = sj1.test();

        assertTrue(sj1.hasObservers());
        assertTrue(pp3.hasSubscribers());

        sj1.onNext(1);
        sj1.onNext(2);
        sj1.onCompleted();

        assertFalse(sj1.hasObservers());
        assertFalse(pp3.hasSubscribers());

        assertTrue(pp3.hasComplete());
        assertFalse(pp3.hasThrowable());
        assertNull(pp3.getThrowable());

        to.assertResult(1, 2);
    }

    @Test
    public void fp3ToSj1Error() {
        io.reactivex.rxjava3.processors.FlowableProcessor<Integer> pp3 = io.reactivex.rxjava3.processors.PublishProcessor.create();
        rx.subjects.Subject<Integer, Integer> sj1 = toV1Subject(pp3);

        rx.observers.AssertableSubscriber<Integer> to = sj1.test();

        assertTrue(sj1.hasObservers());
        assertTrue(pp3.hasSubscribers());

        sj1.onError(new IOException());

        assertFalse(sj1.hasObservers());
        assertFalse(pp3.hasSubscribers());

        assertFalse(pp3.hasComplete());
        assertTrue(pp3.hasThrowable());
        assertNotNull(pp3.getThrowable());
        assertTrue(pp3.getThrowable() instanceof IOException);

        to.assertFailure(IOException.class);
    }

    @Test
    public void fp3ToSj1Backpressured() {
        io.reactivex.rxjava3.processors.FlowableProcessor<Integer> pp3 = io.reactivex.rxjava3.processors.ReplayProcessor.create();
        rx.subjects.Subject<Integer, Integer> sj1 = toV1Subject(pp3);

        rx.observers.AssertableSubscriber<Integer> to = sj1.test(0L);

        assertTrue(sj1.hasObservers());
        assertTrue(pp3.hasSubscribers());

        sj1.onNext(1);
        sj1.onNext(2);
        sj1.onNext(3);
        sj1.onNext(4);

        to.assertNoValues().assertNoErrors().assertNotCompleted();

        to.requestMore(1).assertValue(1).assertNoErrors().assertNotCompleted();

        to.requestMore(2).assertValues(1, 2, 3).assertNoErrors().assertNotCompleted();

        sj1.onCompleted();

        to.assertValues(1, 2, 3).assertNoErrors().assertNotCompleted();

        assertFalse(sj1.hasObservers());
        assertFalse(pp3.hasSubscribers());

        assertTrue(pp3.hasComplete());
        assertFalse(pp3.hasThrowable());
        assertNull(pp3.getThrowable());

        to
        .requestMore(1)
        .assertResult(1, 2, 3, 4);
    }

    // ----------------------------------------------------------
    // 1.x Subject -> 3.x FlowableProcessor
    // ----------------------------------------------------------

    @Test
    public void sj1ToFp3Normal() {
        rx.subjects.Subject<Integer, Integer> sj1 = rx.subjects.PublishSubject.create();
        io.reactivex.rxjava3.processors.FlowableProcessor<Integer> pp3 = toV3Processor(sj1);

        io.reactivex.rxjava3.subscribers.TestSubscriber<Integer> to = pp3.test();

        assertTrue(sj1.hasObservers());
        assertTrue(pp3.hasSubscribers());

        pp3.onNext(1);
        pp3.onNext(2);
        pp3.onComplete();

        assertFalse(sj1.hasObservers());
        assertFalse(pp3.hasSubscribers());

        assertTrue(pp3.hasComplete());
        assertFalse(pp3.hasThrowable());
        assertNull(pp3.getThrowable());

        to.assertResult(1, 2);
    }

    @Test
    public void sj1ToFp3Error() {
        rx.subjects.Subject<Integer, Integer> sj1 = rx.subjects.PublishSubject.create();
        io.reactivex.rxjava3.processors.FlowableProcessor<Integer> pp3 = toV3Processor(sj1);

        io.reactivex.rxjava3.subscribers.TestSubscriber<Integer> to = pp3.test();

        assertTrue(sj1.hasObservers());
        assertTrue(pp3.hasSubscribers());

        pp3.onError(new IOException());

        assertFalse(sj1.hasObservers());
        assertFalse(pp3.hasSubscribers());

        assertFalse(pp3.hasComplete());
        assertTrue(pp3.hasThrowable());
        assertNotNull(pp3.getThrowable());
        assertTrue(pp3.getThrowable() instanceof IOException);

        to.assertFailure(IOException.class);
    }

    @Test
    public void sj1ToFp3Backpressured() {
        rx.subjects.Subject<Integer, Integer> sj1 = rx.subjects.ReplaySubject.create();
        io.reactivex.rxjava3.processors.FlowableProcessor<Integer> pp3 = toV3Processor(sj1);

        io.reactivex.rxjava3.subscribers.TestSubscriber<Integer> to = pp3.test(0L);

        assertTrue(sj1.hasObservers());
        assertTrue(pp3.hasSubscribers());

        pp3.onNext(1);
        pp3.onNext(2);
        pp3.onNext(3);
        pp3.onNext(4);

        to.assertNoValues().assertNoErrors().assertNotComplete();

        to.requestMore(1).assertValue(1).assertNoErrors().assertNotComplete();

        to.requestMore(2).assertValues(1, 2, 3).assertNoErrors().assertNotComplete();

        pp3.onComplete();

        to.assertValues(1, 2, 3).assertNoErrors().assertNotComplete();

        assertFalse(sj1.hasObservers());
        assertFalse(pp3.hasSubscribers());

        assertTrue(pp3.hasComplete());
        assertFalse(pp3.hasThrowable());
        assertNull(pp3.getThrowable());

        to
        .requestMore(1)
        .assertResult(1, 2, 3, 4);
    }


    @Test
    public void sj1ToFp3Lifecycle() {
        rx.subjects.Subject<Integer, Integer> sj1 = rx.subjects.PublishSubject.create();
        io.reactivex.rxjava3.processors.FlowableProcessor<Integer> pp3 = toV3Processor(sj1);

        io.reactivex.rxjava3.subscribers.TestSubscriber<Integer> to = pp3.test(0L);

        assertTrue(sj1.hasObservers());
        assertTrue(pp3.hasSubscribers());

        pp3.onNext(1);
        pp3.onError(new IOException());

        assertFalse(sj1.hasObservers());
        assertFalse(pp3.hasSubscribers());

        assertFalse(pp3.hasComplete());
        assertTrue(pp3.hasThrowable());
        assertNotNull(pp3.getThrowable());

        to.assertFailure(rx.exceptions.MissingBackpressureException.class);
    }

    @Test
    public void sj1ToFp3Lifecycle2() {
        rx.subjects.Subject<Integer, Integer> sj1 = rx.subjects.PublishSubject.create();
        io.reactivex.rxjava3.processors.FlowableProcessor<Integer> pp3 = toV3Processor(sj1);

        io.reactivex.rxjava3.subscribers.TestSubscriber<Integer> to = pp3.test();

        assertTrue(pp3.hasSubscribers());
        assertTrue(sj1.hasObservers());
        assertFalse(pp3.hasComplete());
        assertFalse(pp3.hasThrowable());
        assertNull(pp3.getThrowable());

        BooleanSubscription d1 = new BooleanSubscription();
        pp3.onSubscribe(d1);

        assertFalse(d1.isCancelled());

        pp3.onNext(1);
        pp3.onNext(2);
        pp3.onComplete();
        pp3.onComplete();
        pp3.onError(new IOException());
        pp3.onNext(3);

        BooleanSubscription d3 = new BooleanSubscription();
        pp3.onSubscribe(d3);

        assertFalse(d1.isCancelled());
        assertTrue(d3.isCancelled());

        assertFalse(pp3.hasSubscribers());
        assertFalse(sj1.hasObservers());

        assertTrue(pp3.hasComplete());
        assertFalse(pp3.hasThrowable());
        assertNull(pp3.getThrowable());

        to.assertResult(1, 2);
    }

    @Test
    public void sj1ToFp3NullValue() {
        rx.subjects.Subject<Integer, Integer> sj1 = rx.subjects.PublishSubject.create();
        io.reactivex.rxjava3.processors.FlowableProcessor<Integer> pp3 = toV3Processor(sj1);

        io.reactivex.rxjava3.subscribers.TestSubscriber<Integer> to = pp3.test();

        pp3.onNext(null);

        assertFalse(pp3.hasSubscribers());
        assertFalse(sj1.hasObservers());

        to.assertFailure(NullPointerException.class);
    }

    @Test
    public void sj1ToFp3NullException() {
        rx.subjects.Subject<Integer, Integer> sj1 = rx.subjects.PublishSubject.create();
        io.reactivex.rxjava3.processors.FlowableProcessor<Integer> pp3 = toV3Processor(sj1);

        io.reactivex.rxjava3.subscribers.TestSubscriber<Integer> to = pp3.test();

        pp3.onError(null);

        assertFalse(pp3.hasSubscribers());
        assertFalse(sj1.hasObservers());

        to.assertFailure(NullPointerException.class);
    }

    @Test
    public void ft1ToFt3() {
        rx.Observable.Transformer<Integer, Integer> transformer = new Transformer<Integer, Integer>() {
            @Override
            public Observable<Integer> call(Observable<Integer> o) {
                return o.map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer v) {
                        return v + 1;
                    }
                });
            }
        };

        Flowable.just(1)
        .compose(toV3Transformer(transformer))
        .test()
        .assertResult(2);
    }

    @Test
    public void ft3ToFt1() {
        FlowableTransformer<Integer, Integer> transformer = new FlowableTransformer<Integer, Integer>() {
            @Override
            public Flowable<Integer> apply(Flowable<Integer> o) {
                return o.map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer v) {
                        return v + 1;
                    }
                });
            }
        };

        rx.Observable.just(1)
        .compose(toV1Transformer(transformer))
        .test()
        .assertResult(2);
    }

  @Test
  public void ot1ToOt3() {
    rx.Observable.Transformer<Integer, Integer> transformer = new rx.Observable.Transformer<Integer, Integer>() {
      @Override
      public Observable<Integer> call(Observable<Integer> o) {
        return o.map(new Func1<Integer, Integer>() {
          @Override
          public Integer call(Integer v) {
            return v + 1;
          }
        });
      }
    };

    io.reactivex.rxjava3.core.Observable.just(1)
        .compose(toV3Transformer(transformer, BackpressureStrategy.BUFFER))
        .test()
        .assertResult(2);
  }

    @Test
    public void ot3ToOt1() {
        ObservableTransformer<Integer, Integer> transformer = new ObservableTransformer<Integer, Integer>() {
            @Override
            public io.reactivex.rxjava3.core.Observable<Integer> apply(io.reactivex.rxjava3.core.Observable<Integer> o) {
                return o.map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer v) {
                        return v + 1;
                    }
                });
            }
        };

        rx.Observable.just(1)
            .compose(toV1Transformer(transformer, BackpressureStrategy.BUFFER))
            .test()
            .assertResult(2);
    }

    @Test
    public void st1ToSt3() {
        rx.Single.Transformer<Integer, Integer> transformer = new rx.Single.Transformer<Integer, Integer>() {
            @Override
            public rx.Single<Integer> call(rx.Single<Integer> o) {
                return o.map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer v) {
                        return v + 1;
                    }
                });
            }
        };

        Single.just(1)
        .compose(toV3Transformer(transformer))
        .test()
        .assertResult(2);
    }

    @Test
    public void st3ToSt1() {
        SingleTransformer<Integer, Integer> transformer = new SingleTransformer<Integer, Integer>() {
            @Override
            public Single<Integer> apply(Single<Integer> o) {
                return o.map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer v) {
                        return v + 1;
                    }
                });
            }
        };

        rx.Single.just(1)
        .compose(toV1Transformer(transformer))
        .test()
        .assertResult(2);
    }

    @Test
    public void ct1ToCt3() {

        final int[] calls = { 0 };

        rx.Completable.Transformer transformer = new rx.Completable.Transformer() {
            @Override
            public rx.Completable call(rx.Completable o) {
                return o.doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        calls[0]++;
                    }
                });
            }
        };

        Completable.complete()
        .compose(toV3Transformer(transformer))
        .test()
        .assertResult();

        assertEquals(1, calls[0]);
    }

    @Test
    public void ct3ToCt1() {

        final int[] calls = { 0 };

        CompletableTransformer transformer = new CompletableTransformer() {
            @Override
            public Completable apply(Completable o) {
                return o.doOnComplete(new io.reactivex.rxjava3.functions.Action() {
                    @Override
                    public void run() {
                        calls[0]++;
                    }
                });
            }
        };

        rx.Completable.complete()
        .compose(toV1Transformer(transformer))
        .test()
        .assertResult();

        assertEquals(1, calls[0]);
    }

    @Test
    public void fo1ToFo3() {
        rx.Observable.Operator<Integer, Integer> transformer = new rx.Observable.Operator<Integer, Integer>() {
            @Override
            public rx.Subscriber<? super Integer> call(final rx.Subscriber<? super Integer> o) {
                return new rx.Subscriber<Integer>(o) {
                    @Override
                    public void onNext(Integer t) {
                        o.onNext(t + 1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        o.onError(e);
                    }

                    @Override
                    public void onCompleted() {
                        o.onCompleted();
                    }
                };
            }
        };

        Flowable.just(1)
        .lift(toV3Operator(transformer))
        .test()
        .assertResult(2);
    }

    @Test
    public void fo3ToFo1() {
        FlowableOperator<Integer, Integer> transformer = new FlowableOperator<Integer, Integer>() {
            @Override
            public org.reactivestreams.Subscriber<? super Integer> apply(final org.reactivestreams.Subscriber<? super Integer> o) {
                return new org.reactivestreams.Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        o.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer t) {
                        o.onNext(t + 1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        o.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        o.onComplete();
                    }
                };
            }
        };

        Observable.just(1)
        .lift(toV1Operator(transformer))
        .test()
        .assertResult(2);
    }

    @Test
    public void fo3ToFo1Crash() {
        FlowableOperator<Integer, Integer> transformer = new FlowableOperator<Integer, Integer>() {
            @Override
            public org.reactivestreams.Subscriber<? super Integer> apply(final org.reactivestreams.Subscriber<? super Integer> o) {
                throw new IllegalArgumentException();
            }
        };

        Observable.just(1)
        .lift(toV1Operator(transformer))
        .test()
        .assertFailure(IllegalArgumentException.class);
    }

    @Test
    public void fo1ToFo3Crash() {
        rx.Observable.Operator<Integer, Integer> transformer = new rx.Observable.Operator<Integer, Integer>() {
            @Override
            public rx.Subscriber<? super Integer> call(final rx.Subscriber<? super Integer> o) {
                throw new IllegalArgumentException();
            }
        };

        Flowable.just(1)
        .lift(toV3Operator(transformer))
        .test()
        .assertFailure(IllegalArgumentException.class);
    }

    @Test(expected = NullPointerException.class)
    public void toV3DisposableNullSubscription() {
        RxJavaInterop.toV3Disposable(null);
    }

    @Test
    public void toV3DisposableIsDisposedTrue() {
        assertTrue(RxJavaInterop.toV3Disposable(Subscriptions.unsubscribed()).isDisposed());
    }

    @Test
    public void toV3DisposableIsDisposedFalse() {
        assertFalse(RxJavaInterop.toV3Disposable(Subscriptions.empty()).isDisposed());
    }

    @Test
    public void toV3DisposableCallsUnsubscribe() {
        rx.Subscription subscription = mock(rx.Subscription.class);

        Disposable disposable = RxJavaInterop.toV3Disposable(subscription);
        verifyZeroInteractions(subscription);

        disposable.dispose();
        verify(subscription).unsubscribe();
    }

    @Test(expected = NullPointerException.class)
    public void toV1SubscriptionNullDisposable() {
        RxJavaInterop.toV1Subscription(null);
    }

    @Test
    public void toV1SubscriptionIsUnsubscribedTrue() {
        assertTrue(RxJavaInterop.toV1Subscription(Disposables.disposed()).isUnsubscribed());
    }

    @Test
    public void toV1SubscriptionIsUnsubscribedFalse() {
        assertFalse(RxJavaInterop.toV1Subscription(Disposables.empty()).isUnsubscribed());
    }

    @Test
    public void toV1SubscriptionCallsDispose() {
        Disposable disposable = mock(Disposable.class);

        rx.Subscription subscription = RxJavaInterop.toV1Subscription(disposable);
        verifyZeroInteractions(disposable);

        subscription.unsubscribe();
        verify(disposable).dispose();
    }

    @Test
    public void v1ObservableIsUnsubscribedOnError() {
        Action0 onUnsubscribe = mock(Action0.class);

        RxJavaInterop.toV3Observable(rx.Observable.error(new IOException())
        .doOnUnsubscribe(onUnsubscribe))
        .test()
        .assertFailure(IOException.class);

        verify(onUnsubscribe).call();
    }


    @Test
    public void v1ObservableIsUnsubscribedOnCompletion() {
        Action0 onUnsubscribe = mock(Action0.class);

        RxJavaInterop.toV3Observable(rx.Observable.just(1)
        .doOnUnsubscribe(onUnsubscribe))
        .test()
        .assertResult(1);

        verify(onUnsubscribe).call();
    }

    @Test
    public void v1ObservableIsUnsubscribedOnError2() {
        Action0 onUnsubscribe = mock(Action0.class);

        RxJavaInterop.toV3Flowable(rx.Observable.error(new IOException())
        .doOnUnsubscribe(onUnsubscribe))
        .test()
        .assertFailure(IOException.class);

        verify(onUnsubscribe).call();
    }

    @Test
    public void v1ObservableIsUnsubscribedOnCompletion2() {
        Action0 onUnsubscribe = mock(Action0.class);

        RxJavaInterop.toV3Flowable(rx.Observable.just(1)
        .doOnUnsubscribe(onUnsubscribe))
        .test()
        .assertResult(1);

        verify(onUnsubscribe).call();
    }
}
