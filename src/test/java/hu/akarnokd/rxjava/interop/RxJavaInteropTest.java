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

import java.lang.reflect.*;

import org.junit.Test;

import rx.Observable.OnSubscribe;
import rx.Subscriber;

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
    public void c1c2Null() {
        toV2Completable(null);
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
        toV1Single(null);
    }

    @Test(expected = NullPointerException.class)
    public void c2c1Null() {
        toV1Completable(null);
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
        toV2Flowable(rx.Observable.create(new OnSubscribe<Object>() {
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
        toV2Observable(rx.Observable.create(new OnSubscribe<Object>() {
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

}
