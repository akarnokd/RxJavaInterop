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

import static hu.akarnokd.rxjava.interop.RxJavaInterop.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.reactivestreams.Subscription;

import io.reactivex.*;
import io.reactivex.disposables.*;
import io.reactivex.functions.Function;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import rx.Observable;
import rx.Observable.*;
import rx.Subscriber;
import rx.functions.*;
import rx.subscriptions.Subscriptions;

public class RxJavaInteropTest {

    @Test
    public void privateConstructor() {
        try {
            Constructor<?> c = hu.akarnokd.rxjava.interop.RxJavaInterop.class.getDeclaredConstructor();
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
    public void o1f2Null() {
        toV2Flowable(null);
    }

    @Test(expected = NullPointerException.class)
    public void o1o2Null() {
        toV2Observable(null);
    }

    @Test(expected = NullPointerException.class)
    public void s1s2Null() {
        toV2Single(null);
    }

    @Test(expected = NullPointerException.class)
    public void s1m2Null() {
        toV2Maybe((rx.Single<?>)null);
    }

    @Test(expected = NullPointerException.class)
    public void c1c2Null() {
        toV2Completable(null);
    }

    @Test(expected = NullPointerException.class)
    public void c1m2Null() {
        toV2Maybe((rx.Completable)null);
    }

    @Test(expected = NullPointerException.class)
    public void f2o1Null() {
        toV1Observable(null);
    }

    @Test(expected = NullPointerException.class)
    public void o2o1Null() {
        toV1Observable(null, io.reactivex.BackpressureStrategy.BUFFER);
    }

    @Test(expected = NullPointerException.class)
    public void o2o1NullStrategy() {
        toV1Observable(io.reactivex.Observable.empty(), null);
    }

    @Test(expected = NullPointerException.class)
    public void s2s1Null() {
        toV1Single((SingleSource<?>)null);
    }

    @Test(expected = NullPointerException.class)
    public void m2s1Null() {
        toV1Single((MaybeSource<?>)null);
    }

    @Test(expected = NullPointerException.class)
    public void c2c1Null() {
        toV1Completable((io.reactivex.CompletableSource)null);
    }

    @Test(expected = NullPointerException.class)
    public void m2c1Null() {
        toV1Completable((MaybeSource<?>)null);
    }

    // ----------------------------------------------------------
    // 1.x Observable -> 2.x Flowable
    // ----------------------------------------------------------

    @Test
    public void o1f2Normal() {
        toV2Flowable(rx.Observable.range(1, 5))
        .test()
        .assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void o1f2NormalTake() {
        toV2Flowable(rx.Observable.range(1, 5))
        .take(2)
        .test()
        .assertResult(1, 2);
    }

    @Test
    public void o1f2NormalBackpressured() {
        toV2Flowable(rx.Observable.range(1, 5))
        .test(1)
        .assertValue(1)
        .assertNoErrors()
        .assertNotComplete();
    }

    @Test
    public void o1f2Empty() {
        toV2Flowable(rx.Observable.empty())
        .test()
        .assertResult();
    }

    @Test
    public void o1f2Error() {
        toV2Flowable(rx.Observable.error(new RuntimeException("Forced failure")))
        .test()
        .assertFailureAndMessage(RuntimeException.class, "Forced failure");
    }

    @Test
    public void o1f2ErrorNull() {
        toV2Flowable(rx.Observable.just(null))
        .test()
        .assertFailure(NullPointerException.class);
    }

    @Test
    public void o1f2IgnoreCancel() {
        toV2Flowable(rx.Observable.unsafeCreate(new OnSubscribe<Object>() {
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
    // 1.x Observable -> 2.x Observable
    // ----------------------------------------------------------

    @Test
    public void o1o2Normal() {
        toV2Observable(rx.Observable.range(1, 5))
        .test()
        .assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void o1o2NormalTake() {
        toV2Observable(rx.Observable.range(1, 5))
        .take(2)
        .test()
        .assertResult(1, 2);
    }

    @Test
    public void o1o2Empty() {
        toV2Observable(rx.Observable.empty())
        .test()
        .assertResult();
    }

    @Test
    public void o1o2Error() {
        toV2Observable(rx.Observable.error(new RuntimeException("Forced failure")))
        .test()
        .assertFailureAndMessage(RuntimeException.class, "Forced failure");
    }

    @Test
    public void o1o2ErrorNull() {
        toV2Observable(rx.Observable.just(null))
        .test()
        .assertFailure(NullPointerException.class);
    }

    @Test
    public void o1fo2IgnoreCancel() {
        toV2Observable(rx.Observable.unsafeCreate(new OnSubscribe<Object>() {
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
    public void o1o2IsDisposed() {
        toV2Observable(rx.Observable.just(1))
        .subscribe(new io.reactivex.Observer<Integer>() {
            @Override
            public void onSubscribe(io.reactivex.disposables.Disposable d) {
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
    // 1.x Single -> 2.x Single
    // ----------------------------------------------------------

    @Test
    public void s1s2Normal() {
        toV2Single(rx.Single.just(1)).test()
        .assertResult(1);
    }

    @Test
    public void s1s2Cancel() {
        rx.subjects.PublishSubject<Integer> ps = rx.subjects.PublishSubject.create();
        io.reactivex.observers.TestObserver<Integer> ts = toV2Single(ps.toSingle()).test();

        assertTrue("1.x PublishSubject has no observers!", ps.hasObservers());

        ts.cancel();

        assertFalse("1.x PublishSubject has observers!", ps.hasObservers());

    }

    @Test
    public void s1s2Error() {
        toV2Single(rx.Single.error(new RuntimeException("Forced failure"))).test()
        .assertFailureAndMessage(RuntimeException.class, "Forced failure");
    }

    @Test
    public void s1s2ErrorNull() {
        toV2Single(rx.Single.just(null)).test()
        .assertFailure(NullPointerException.class);
    }

    @Test
    public void s1s2IsDisposed() {
        toV2Single(rx.Single.just(1))
        .subscribe(new io.reactivex.SingleObserver<Integer>() {
            @Override
            public void onSubscribe(io.reactivex.disposables.Disposable d) {
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
    // 1.x Single -> 2.x Maybe
    // ----------------------------------------------------------

    @Test
    public void s1m2Normal() {
        toV2Maybe(rx.Single.just(1)).test()
            .assertResult(1);
    }

    @Test
    public void s1m2Cancel() {
        rx.subjects.PublishSubject<Integer> ps = rx.subjects.PublishSubject.create();
        io.reactivex.observers.TestObserver<Integer> ts = toV2Maybe(ps.toSingle()).test();

        assertTrue("1.x PublishSubject has no observers!", ps.hasObservers());

        ts.cancel();

        assertFalse("1.x PublishSubject has observers!", ps.hasObservers());

    }

    @Test
    public void s1m2Error() {
        toV2Maybe(rx.Single.error(new RuntimeException("Forced failure"))).test()
            .assertFailureAndMessage(RuntimeException.class, "Forced failure");
    }

    @Test
    public void s1m2ErrorNull() {
        toV2Maybe(rx.Single.just(null)).test()
            .assertFailure(NullPointerException.class);
    }

    @Test
    public void s1m2IsDisposed() {
        toV2Maybe(rx.Single.just(1))
            .subscribe(new io.reactivex.MaybeObserver<Integer>() {
                @Override
                public void onSubscribe(io.reactivex.disposables.Disposable d) {
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
    // 1.x Completable -> 2.x Completable
    // ----------------------------------------------------------

    @Test
    public void c1c2Normal() {
        toV2Completable(rx.Completable.complete()).test()
        .assertResult();
    }

    @Test
    public void c1c2Cancel() {
        rx.subjects.PublishSubject<Integer> ps = rx.subjects.PublishSubject.create();
        io.reactivex.observers.TestObserver<Void> ts = toV2Completable(ps.toCompletable()).test();

        assertTrue("1.x PublishSubject has no observers!", ps.hasObservers());

        ts.cancel();

        assertFalse("1.x PublishSubject has observers!", ps.hasObservers());
    }

    @Test
    public void c1c2Error() {
        toV2Completable(rx.Completable.error(new RuntimeException("Forced failure"))).test()
        .assertFailureAndMessage(RuntimeException.class, "Forced failure");
    }

    @Test
    public void c1c2IsDisposed() {
        toV2Completable(rx.Completable.complete())
        .subscribe(new io.reactivex.CompletableObserver() {
            @Override
            public void onSubscribe(io.reactivex.disposables.Disposable d) {
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
    // 1.x Completable -> 2.x Maybe
    // ----------------------------------------------------------

    @Test
    public void c1m2Normal() {
        toV2Maybe(rx.Completable.complete()).test()
            .assertResult();
    }

    @Test
    public void c1m2Cancel() {
        rx.subjects.PublishSubject<Integer> ps = rx.subjects.PublishSubject.create();
        io.reactivex.observers.TestObserver<Void> ts = RxJavaInterop.<Void>toV2Maybe(ps.toCompletable()).test();

        assertTrue("1.x PublishSubject has no observers!", ps.hasObservers());

        ts.cancel();

        assertFalse("1.x PublishSubject has observers!", ps.hasObservers());
    }

    @Test
    public void c1m2Error() {
        toV2Maybe(rx.Completable.error(new RuntimeException("Forced failure"))).test()
            .assertFailureAndMessage(RuntimeException.class, "Forced failure");
    }


    @Test
    public void c1m2IsDisposed() {
        RxJavaInterop.<Void>toV2Maybe(rx.Completable.complete())
            .subscribe(new io.reactivex.MaybeObserver<Void>() {
                @Override
                public void onSubscribe(io.reactivex.disposables.Disposable d) {
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
    // 2.x Flowable -> 1.x Observable
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
    public void f2o1Normal() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Observable(
                io.reactivex.Flowable.range(1, 5)));

        ts.assertValues(1, 2, 3, 4, 5);
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void f2o1NormalTake() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Observable(
                io.reactivex.Flowable.range(1, 5)).take(2));

        ts.assertValues(1, 2);
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void f2o1NormalBackpressured() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Observable(
                io.reactivex.Flowable.range(1, 5)), 1);
        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertNotCompleted();
    }

    @Test
    public void f2o1Empty() {
        rx.observers.TestSubscriber<Object> ts = test1(toV1Observable(
                io.reactivex.Flowable.empty()));
        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void f2o1Error() {
        rx.observers.TestSubscriber<Object> ts = test1(toV1Observable(
                io.reactivex.Flowable.error(new RuntimeException("Forced failure"))));
        ts.assertNoValues();
        ts.assertError(RuntimeException.class);
        assertEquals("Forced failure", ts.getOnErrorEvents().get(0).getMessage());
    }

    // ----------------------------------------------------------
    // 2.x Observable -> 1.x Observable
    // ----------------------------------------------------------

    @Test
    public void o2o1Normal() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Observable(
                io.reactivex.Observable.range(1, 5), io.reactivex.BackpressureStrategy.BUFFER));

        ts.assertValues(1, 2, 3, 4, 5);
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void o2o1NormalTake() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Observable(
                io.reactivex.Observable.range(1, 5), io.reactivex.BackpressureStrategy.BUFFER).take(2));

        ts.assertValues(1, 2);
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void o2o1NormalBackpressured() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Observable(
                io.reactivex.Observable.range(1, 5), io.reactivex.BackpressureStrategy.BUFFER), 1);
        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertNotCompleted();
    }

    @Test
    public void o2o1Empty() {
        rx.observers.TestSubscriber<Object> ts = test1(toV1Observable(
                io.reactivex.Observable.empty(), io.reactivex.BackpressureStrategy.BUFFER));
        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void o2o1Error() {
        rx.observers.TestSubscriber<Object> ts = test1(toV1Observable(
                io.reactivex.Observable.error(new RuntimeException("Forced failure")), io.reactivex.BackpressureStrategy.BUFFER));
        ts.assertNoValues();
        ts.assertError(RuntimeException.class);
        assertEquals("Forced failure", ts.getOnErrorEvents().get(0).getMessage());
    }

    // ----------------------------------------------------------
    // 2.x Single -> 1.x Single
    // ----------------------------------------------------------

    static <T> rx.observers.TestSubscriber<T> test1(rx.Single<T> s) {
        rx.observers.TestSubscriber<T> ts = new rx.observers.TestSubscriber<T>();
        s.subscribe(ts);
        return ts;
    }

    @Test
    public void s2s1Normal() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Single(io.reactivex.Single.just(1)));
        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void s2s1Cancel() {
        io.reactivex.subjects.PublishSubject<Integer> ps = io.reactivex.subjects.PublishSubject.create();
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Single(ps.single(-99)));

        assertTrue("2.x PublishSubject has no observers!", ps.hasObservers());

        ts.unsubscribe();

        assertFalse("2.x PublishSubject has observers!", ps.hasObservers());

    }

    @Test
    public void s2s1Error() {
        rx.observers.TestSubscriber<Object> ts = test1(toV1Single(
                io.reactivex.Single.error(new RuntimeException("Forced failure"))));

        ts.assertNoValues();
        ts.assertError(RuntimeException.class);
        assertEquals("Forced failure", ts.getOnErrorEvents().get(0).getMessage());
    }

    // ----------------------------------------------------------
    // 2.x Completable -> 1.x Completable
    // ----------------------------------------------------------

    static <T> rx.observers.TestSubscriber<T> test1(rx.Completable s) {
        rx.observers.TestSubscriber<T> ts = new rx.observers.TestSubscriber<T>();
        s.subscribe(ts);
        return ts;
    }

    @Test
    public void c2c1Normal() {
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Completable(
                io.reactivex.Completable.complete()));
        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void c2c1Cancel() {
        io.reactivex.subjects.PublishSubject<Integer> ps = io.reactivex.subjects.PublishSubject.create();
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Completable(ps.ignoreElements()));

        assertTrue("2.x PublishSubject has no observers!", ps.hasObservers());

        ts.unsubscribe();

        assertFalse("2.x PublishSubject has observers!", ps.hasObservers());

    }

    @Test
    public void c2c1Error() {
        rx.observers.TestSubscriber<Object> ts = test1(toV1Completable(
                io.reactivex.Completable.error(new RuntimeException("Forced failure"))));

        ts.assertNoValues();
        ts.assertError(RuntimeException.class);
        assertEquals("Forced failure", ts.getOnErrorEvents().get(0).getMessage());
    }

    // ----------------------------------------------------------
    // 2.x Maybe -> 1.x Single
    // ----------------------------------------------------------

    @Test
    public void m2s1Normal() {
        rx.observers.TestSubscriber<Integer> ts = test1(
                toV1Single(io.reactivex.Maybe.just(1)));

        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertCompleted();
    }


    @Test
    public void m2s1Error() {
        rx.observers.TestSubscriber<Integer> ts = test1(
                toV1Single(io.reactivex.Maybe.<Integer>error(new RuntimeException())));

        ts.assertNoValues();
        ts.assertError(RuntimeException.class);
        ts.assertNotCompleted();
    }

    @Test
    public void m2s1Empty() {
        rx.observers.TestSubscriber<Integer> ts = test1(
                toV1Single(io.reactivex.Maybe.<Integer>empty()));

        ts.assertNoValues();
        ts.assertError(NoSuchElementException.class);
        ts.assertNotCompleted();
    }

    @Test
    public void m2s1Cancel() {
        io.reactivex.subjects.PublishSubject<Integer> ps = io.reactivex.subjects.PublishSubject.create();
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Single(ps.singleElement()));

        assertTrue("2.x PublishSubject has no observers!", ps.hasObservers());

        ts.unsubscribe();

        assertFalse("2.x PublishSubject has observers!", ps.hasObservers());
    }

    // ----------------------------------------------------------
    // 2.x Maybe -> 1.x Completable
    // ----------------------------------------------------------

    @Test
    public void m2c1Normal() {
        rx.observers.TestSubscriber<Integer> ts = test1(
                toV1Completable(io.reactivex.Maybe.just(1)));

        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertCompleted();
    }


    @Test
    public void m2c1Error() {
        rx.observers.TestSubscriber<Integer> ts = test1(
                toV1Completable(io.reactivex.Maybe.<Integer>error(new RuntimeException())));

        ts.assertNoValues();
        ts.assertError(RuntimeException.class);
        ts.assertNotCompleted();
    }

    @Test
    public void m2c1Empty() {
        rx.observers.TestSubscriber<Integer> ts = test1(
                toV1Completable(io.reactivex.Maybe.<Integer>empty()));

        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertCompleted();
    }

    @Test
    public void m2c1Cancel() {
        io.reactivex.subjects.PublishSubject<Integer> ps = io.reactivex.subjects.PublishSubject.create();
        rx.observers.TestSubscriber<Integer> ts = test1(toV1Completable(ps.singleElement()));

        assertTrue("2.x PublishSubject has no observers!", ps.hasObservers());

        ts.unsubscribe();

        assertFalse("2.x PublishSubject has observers!", ps.hasObservers());
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
    // 1.x Subject -> 2.x Subject
    // ----------------------------------------------------------

    @Test
    public void sj1ToSj2Normal() {
        rx.subjects.PublishSubject<Integer> ps1 = rx.subjects.PublishSubject.create();
        io.reactivex.subjects.Subject<Integer> sj2 = toV2Subject(ps1);

        io.reactivex.observers.TestObserver<Integer> to = sj2.test();

        assertTrue(sj2.hasObservers());
        assertTrue(ps1.hasObservers());
        assertFalse(sj2.hasComplete());
        assertFalse(sj2.hasThrowable());
        assertNull(sj2.getThrowable());

        sj2.onNext(1);
        sj2.onNext(2);
        sj2.onComplete();

        assertFalse(sj2.hasObservers());
        assertFalse(ps1.hasObservers());

        assertTrue(sj2.hasComplete());
        assertFalse(sj2.hasThrowable());
        assertNull(sj2.getThrowable());

        to.assertResult(1, 2);
    }

    @Test
    public void sj1ToSj2Error() {
        rx.subjects.PublishSubject<Integer> ps1 = rx.subjects.PublishSubject.create();
        io.reactivex.subjects.Subject<Integer> sj2 = toV2Subject(ps1);

        io.reactivex.observers.TestObserver<Integer> to = sj2.test();

        assertTrue(sj2.hasObservers());
        assertTrue(ps1.hasObservers());
        assertFalse(sj2.hasComplete());
        assertFalse(sj2.hasThrowable());
        assertNull(sj2.getThrowable());

        sj2.onError(new IOException());

        assertFalse(sj2.hasObservers());
        assertFalse(ps1.hasObservers());

        assertFalse(sj2.hasComplete());
        assertTrue(sj2.hasThrowable());
        assertNotNull(sj2.getThrowable());
        assertTrue(sj2.getThrowable() instanceof IOException);

        to.assertFailure(IOException.class);
    }

    @Test
    public void sj1ToSj2Lifecycle() {
        rx.subjects.PublishSubject<Integer> ps1 = rx.subjects.PublishSubject.create();
        io.reactivex.subjects.Subject<Integer> sj2 = toV2Subject(ps1);

        io.reactivex.observers.TestObserver<Integer> to = sj2.test();

        assertTrue(sj2.hasObservers());
        assertTrue(ps1.hasObservers());
        assertFalse(sj2.hasComplete());
        assertFalse(sj2.hasThrowable());
        assertNull(sj2.getThrowable());

        Disposable d1 = Disposables.empty();
        sj2.onSubscribe(d1);

        assertFalse(d1.isDisposed());

        sj2.onNext(1);
        sj2.onNext(2);
        sj2.onComplete();
        sj2.onComplete();
        sj2.onError(new IOException());
        sj2.onNext(3);

        Disposable d2 = Disposables.empty();
        sj2.onSubscribe(d2);

        assertFalse(d1.isDisposed());
        assertTrue(d2.isDisposed());

        assertFalse(sj2.hasObservers());
        assertFalse(ps1.hasObservers());

        assertTrue(sj2.hasComplete());
        assertFalse(sj2.hasThrowable());
        assertNull(sj2.getThrowable());

        to.assertResult(1, 2);
    }

    @Test
    public void sj1ToSj2NullValue() {
        rx.subjects.PublishSubject<Integer> ps1 = rx.subjects.PublishSubject.create();
        io.reactivex.subjects.Subject<Integer> sj2 = toV2Subject(ps1);

        io.reactivex.observers.TestObserver<Integer> to = sj2.test();

        sj2.onNext(null);

        assertFalse(sj2.hasObservers());
        assertFalse(ps1.hasObservers());

        to.assertFailure(NullPointerException.class);
    }

    @Test
    public void sj1ToSj2NullException() {
        rx.subjects.PublishSubject<Integer> ps1 = rx.subjects.PublishSubject.create();
        io.reactivex.subjects.Subject<Integer> sj2 = toV2Subject(ps1);

        io.reactivex.observers.TestObserver<Integer> to = sj2.test();

        sj2.onError(null);

        assertFalse(sj2.hasObservers());
        assertFalse(ps1.hasObservers());

        to.assertFailure(NullPointerException.class);
    }

    // ----------------------------------------------------------
    // 2.x Subject -> 1.x Subject
    // ----------------------------------------------------------

    @Test
    public void sj2ToSj1Normal() {
        io.reactivex.subjects.PublishSubject<Integer> ps2 = io.reactivex.subjects.PublishSubject.create();
        rx.subjects.Subject<Integer, Integer> sj1 = toV1Subject(ps2);

        rx.observers.AssertableSubscriber<Integer> to = sj1.test();

        assertTrue(sj1.hasObservers());
        assertTrue(ps2.hasObservers());

        sj1.onNext(1);
        sj1.onNext(2);
        sj1.onCompleted();

        assertFalse(sj1.hasObservers());
        assertFalse(ps2.hasObservers());

        to.assertResult(1, 2);
    }

    @Test
    public void sj2ToSj1Error() {
        io.reactivex.subjects.PublishSubject<Integer> ps2 = io.reactivex.subjects.PublishSubject.create();
        rx.subjects.Subject<Integer, Integer> sj1 = toV1Subject(ps2);

        rx.observers.AssertableSubscriber<Integer> to = sj1.test();

        assertTrue(sj1.hasObservers());
        assertTrue(ps2.hasObservers());

        sj1.onError(new IOException());

        assertFalse(sj1.hasObservers());
        assertFalse(ps2.hasObservers());

        to.assertFailure(IOException.class);
    }

    @Test
    public void sj2ToSj1Backpressured() {
        io.reactivex.subjects.PublishSubject<Integer> pp2 = io.reactivex.subjects.PublishSubject.create();
        rx.subjects.Subject<Integer, Integer> sj1 = toV1Subject(pp2);

        rx.observers.AssertableSubscriber<Integer> to = sj1.test(0L);

        assertTrue(sj1.hasObservers());
        assertTrue(pp2.hasObservers());

        sj1.onNext(1);

        assertFalse(sj1.hasObservers());
        assertFalse(pp2.hasObservers());

        assertFalse(pp2.hasComplete());
        assertFalse(pp2.hasThrowable());
        assertNull(pp2.getThrowable());

        to.assertFailure(rx.exceptions.MissingBackpressureException.class);
    }

    @Test
    public void sj2ToSj1Lifecycle() {
        io.reactivex.subjects.PublishSubject<Integer> pp2 = io.reactivex.subjects.PublishSubject.create();
        rx.subjects.Subject<Integer, Integer> sj1 = toV1Subject(pp2);

        rx.observers.AssertableSubscriber<Integer> to = sj1.test(0L);

        assertTrue(sj1.hasObservers());
        assertTrue(pp2.hasObservers());

        sj1.onNext(1);
        sj1.onError(new IOException());

        assertFalse(sj1.hasObservers());
        assertFalse(pp2.hasObservers());

        assertFalse(pp2.hasComplete());
        assertTrue(pp2.hasThrowable());
        assertNotNull(pp2.getThrowable());

        to.assertFailure(rx.exceptions.MissingBackpressureException.class);
    }

    // ----------------------------------------------------------
    // 2.x FlowableProcessor -> 1.x Subject
    // ----------------------------------------------------------

    @Test
    public void fp2ToSj1Normal() {
        io.reactivex.processors.FlowableProcessor<Integer> pp2 = io.reactivex.processors.PublishProcessor.create();
        rx.subjects.Subject<Integer, Integer> sj1 = toV1Subject(pp2);

        rx.observers.AssertableSubscriber<Integer> to = sj1.test();

        assertTrue(sj1.hasObservers());
        assertTrue(pp2.hasSubscribers());

        sj1.onNext(1);
        sj1.onNext(2);
        sj1.onCompleted();

        assertFalse(sj1.hasObservers());
        assertFalse(pp2.hasSubscribers());

        assertTrue(pp2.hasComplete());
        assertFalse(pp2.hasThrowable());
        assertNull(pp2.getThrowable());

        to.assertResult(1, 2);
    }

    @Test
    public void fp2ToSj1Error() {
        io.reactivex.processors.FlowableProcessor<Integer> pp2 = io.reactivex.processors.PublishProcessor.create();
        rx.subjects.Subject<Integer, Integer> sj1 = toV1Subject(pp2);

        rx.observers.AssertableSubscriber<Integer> to = sj1.test();

        assertTrue(sj1.hasObservers());
        assertTrue(pp2.hasSubscribers());

        sj1.onError(new IOException());

        assertFalse(sj1.hasObservers());
        assertFalse(pp2.hasSubscribers());

        assertFalse(pp2.hasComplete());
        assertTrue(pp2.hasThrowable());
        assertNotNull(pp2.getThrowable());
        assertTrue(pp2.getThrowable() instanceof IOException);

        to.assertFailure(IOException.class);
    }

    @Test
    public void fp2ToSj1Backpressured() {
        io.reactivex.processors.FlowableProcessor<Integer> pp2 = io.reactivex.processors.ReplayProcessor.create();
        rx.subjects.Subject<Integer, Integer> sj1 = toV1Subject(pp2);

        rx.observers.AssertableSubscriber<Integer> to = sj1.test(0L);

        assertTrue(sj1.hasObservers());
        assertTrue(pp2.hasSubscribers());

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
        assertFalse(pp2.hasSubscribers());

        assertTrue(pp2.hasComplete());
        assertFalse(pp2.hasThrowable());
        assertNull(pp2.getThrowable());

        to
        .requestMore(1)
        .assertResult(1, 2, 3, 4);
    }

    // ----------------------------------------------------------
    // 1.x Subject -> 2.x FlowableProcessor
    // ----------------------------------------------------------

    @Test
    public void sj1ToFp2Normal() {
        rx.subjects.Subject<Integer, Integer> sj1 = rx.subjects.PublishSubject.create();
        io.reactivex.processors.FlowableProcessor<Integer> pp2 = toV2Processor(sj1);

        io.reactivex.subscribers.TestSubscriber<Integer> to = pp2.test();

        assertTrue(sj1.hasObservers());
        assertTrue(pp2.hasSubscribers());

        pp2.onNext(1);
        pp2.onNext(2);
        pp2.onComplete();

        assertFalse(sj1.hasObservers());
        assertFalse(pp2.hasSubscribers());

        assertTrue(pp2.hasComplete());
        assertFalse(pp2.hasThrowable());
        assertNull(pp2.getThrowable());

        to.assertResult(1, 2);
    }

    @Test
    public void sj1ToFp2Error() {
        rx.subjects.Subject<Integer, Integer> sj1 = rx.subjects.PublishSubject.create();
        io.reactivex.processors.FlowableProcessor<Integer> pp2 = toV2Processor(sj1);

        io.reactivex.subscribers.TestSubscriber<Integer> to = pp2.test();

        assertTrue(sj1.hasObservers());
        assertTrue(pp2.hasSubscribers());

        pp2.onError(new IOException());

        assertFalse(sj1.hasObservers());
        assertFalse(pp2.hasSubscribers());

        assertFalse(pp2.hasComplete());
        assertTrue(pp2.hasThrowable());
        assertNotNull(pp2.getThrowable());
        assertTrue(pp2.getThrowable() instanceof IOException);

        to.assertFailure(IOException.class);
    }

    @Test
    public void sj1ToFp2Backpressured() {
        rx.subjects.Subject<Integer, Integer> sj1 = rx.subjects.ReplaySubject.create();
        io.reactivex.processors.FlowableProcessor<Integer> pp2 = toV2Processor(sj1);

        io.reactivex.subscribers.TestSubscriber<Integer> to = pp2.test(0L);

        assertTrue(sj1.hasObservers());
        assertTrue(pp2.hasSubscribers());

        pp2.onNext(1);
        pp2.onNext(2);
        pp2.onNext(3);
        pp2.onNext(4);

        to.assertNoValues().assertNoErrors().assertNotComplete();

        to.requestMore(1).assertValue(1).assertNoErrors().assertNotComplete();

        to.requestMore(2).assertValues(1, 2, 3).assertNoErrors().assertNotComplete();

        pp2.onComplete();

        to.assertValues(1, 2, 3).assertNoErrors().assertNotComplete();

        assertFalse(sj1.hasObservers());
        assertFalse(pp2.hasSubscribers());

        assertTrue(pp2.hasComplete());
        assertFalse(pp2.hasThrowable());
        assertNull(pp2.getThrowable());

        to
        .requestMore(1)
        .assertResult(1, 2, 3, 4);
    }


    @Test
    public void sj1ToFp2Lifecycle() {
        rx.subjects.Subject<Integer, Integer> sj1 = rx.subjects.PublishSubject.create();
        io.reactivex.processors.FlowableProcessor<Integer> pp2 = toV2Processor(sj1);

        io.reactivex.subscribers.TestSubscriber<Integer> to = pp2.test(0L);

        assertTrue(sj1.hasObservers());
        assertTrue(pp2.hasSubscribers());

        pp2.onNext(1);
        pp2.onError(new IOException());

        assertFalse(sj1.hasObservers());
        assertFalse(pp2.hasSubscribers());

        assertFalse(pp2.hasComplete());
        assertTrue(pp2.hasThrowable());
        assertNotNull(pp2.getThrowable());

        to.assertFailure(rx.exceptions.MissingBackpressureException.class);
    }

    @Test
    public void sj1ToFp2Lifecycle2() {
        rx.subjects.Subject<Integer, Integer> sj1 = rx.subjects.PublishSubject.create();
        io.reactivex.processors.FlowableProcessor<Integer> pp2 = toV2Processor(sj1);

        io.reactivex.subscribers.TestSubscriber<Integer> to = pp2.test();

        assertTrue(pp2.hasSubscribers());
        assertTrue(sj1.hasObservers());
        assertFalse(pp2.hasComplete());
        assertFalse(pp2.hasThrowable());
        assertNull(pp2.getThrowable());

        BooleanSubscription d1 = new BooleanSubscription();
        pp2.onSubscribe(d1);

        assertFalse(d1.isCancelled());

        pp2.onNext(1);
        pp2.onNext(2);
        pp2.onComplete();
        pp2.onComplete();
        pp2.onError(new IOException());
        pp2.onNext(3);

        BooleanSubscription d2 = new BooleanSubscription();
        pp2.onSubscribe(d2);

        assertFalse(d1.isCancelled());
        assertTrue(d2.isCancelled());

        assertFalse(pp2.hasSubscribers());
        assertFalse(sj1.hasObservers());

        assertTrue(pp2.hasComplete());
        assertFalse(pp2.hasThrowable());
        assertNull(pp2.getThrowable());

        to.assertResult(1, 2);
    }

    @Test
    public void sj1ToFp2NullValue() {
        rx.subjects.Subject<Integer, Integer> sj1 = rx.subjects.PublishSubject.create();
        io.reactivex.processors.FlowableProcessor<Integer> pp2 = toV2Processor(sj1);

        io.reactivex.subscribers.TestSubscriber<Integer> to = pp2.test();

        pp2.onNext(null);

        assertFalse(pp2.hasSubscribers());
        assertFalse(sj1.hasObservers());

        to.assertFailure(NullPointerException.class);
    }

    @Test
    public void sj1ToFp2NullException() {
        rx.subjects.Subject<Integer, Integer> sj1 = rx.subjects.PublishSubject.create();
        io.reactivex.processors.FlowableProcessor<Integer> pp2 = toV2Processor(sj1);

        io.reactivex.subscribers.TestSubscriber<Integer> to = pp2.test();

        pp2.onError(null);

        assertFalse(pp2.hasSubscribers());
        assertFalse(sj1.hasObservers());

        to.assertFailure(NullPointerException.class);
    }

    @Test
    public void ft1ToFt2() {
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
        .compose(toV2Transformer(transformer))
        .test()
        .assertResult(2);
    }

    @Test
    public void ft2ToFt1() {
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
  public void ot1ToOt2() {
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

    io.reactivex.Observable.just(1)
        .compose(toV2Transformer(transformer, BackpressureStrategy.BUFFER))
        .test()
        .assertResult(2);
  }

    @Test
    public void ot2ToOt1() {
        ObservableTransformer<Integer, Integer> transformer = new ObservableTransformer<Integer, Integer>() {
            @Override
            public io.reactivex.Observable<Integer> apply(io.reactivex.Observable<Integer> o) {
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
    public void st1ToSt2() {
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
        .compose(toV2Transformer(transformer))
        .test()
        .assertResult(2);
    }

    @Test
    public void st2ToSt1() {
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
    public void ct1ToCt2() {

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
        .compose(toV2Transformer(transformer))
        .test()
        .assertResult();

        assertEquals(1, calls[0]);
    }

    @Test
    public void ct2ToCt1() {

        final int[] calls = { 0 };

        CompletableTransformer transformer = new CompletableTransformer() {
            @Override
            public Completable apply(Completable o) {
                return o.doOnComplete(new io.reactivex.functions.Action() {
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
    public void fo1ToFo2() {
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
        .lift(toV2Operator(transformer))
        .test()
        .assertResult(2);
    }

    @Test
    public void fo2ToFo1() {
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
    public void fo2ToFo1Crash() {
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
    public void fo1ToFo2Crash() {
        rx.Observable.Operator<Integer, Integer> transformer = new rx.Observable.Operator<Integer, Integer>() {
            @Override
            public rx.Subscriber<? super Integer> call(final rx.Subscriber<? super Integer> o) {
                throw new IllegalArgumentException();
            }
        };

        Flowable.just(1)
        .lift(toV2Operator(transformer))
        .test()
        .assertFailure(IllegalArgumentException.class);
    }

    @Test(expected = NullPointerException.class)
    public void toV2DisposableNullSubscription() {
        RxJavaInterop.toV2Disposable(null);
    }

    @Test
    public void toV2DisposableIsDisposedTrue() {
        assertTrue(RxJavaInterop.toV2Disposable(Subscriptions.unsubscribed()).isDisposed());
    }

    @Test
    public void toV2DisposableIsDisposedFalse() {
        assertFalse(RxJavaInterop.toV2Disposable(Subscriptions.empty()).isDisposed());
    }

    @Test
    public void toV2DisposableCallsUnsubscribe() {
        rx.Subscription subscription = mock(rx.Subscription.class);

        Disposable disposable = RxJavaInterop.toV2Disposable(subscription);
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

        RxJavaInterop.toV2Observable(rx.Observable.error(new IOException())
        .doOnUnsubscribe(onUnsubscribe))
        .test()
        .assertFailure(IOException.class);

        verify(onUnsubscribe).call();
    }


    @Test
    public void v1ObservableIsUnsubscribedOnCompletion() {
        Action0 onUnsubscribe = mock(Action0.class);

        RxJavaInterop.toV2Observable(rx.Observable.just(1)
        .doOnUnsubscribe(onUnsubscribe))
        .test()
        .assertResult(1);

        verify(onUnsubscribe).call();
    }

    @Test
    public void v1ObservableIsUnsubscribedOnError2() {
        Action0 onUnsubscribe = mock(Action0.class);

        RxJavaInterop.toV2Flowable(rx.Observable.error(new IOException())
        .doOnUnsubscribe(onUnsubscribe))
        .test()
        .assertFailure(IOException.class);

        verify(onUnsubscribe).call();
    }

    @Test
    public void v1ObservableIsUnsubscribedOnCompletion2() {
        Action0 onUnsubscribe = mock(Action0.class);

        RxJavaInterop.toV2Flowable(rx.Observable.just(1)
        .doOnUnsubscribe(onUnsubscribe))
        .test()
        .assertResult(1);

        verify(onUnsubscribe).call();
    }
}
