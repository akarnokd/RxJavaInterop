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
 * Conversion methods for converting between 1.x and 2.x reactive types, composing backpressure
 * and cancellation through.
 */
public final class RxJavaInterop {
    /** Utility class. */
    private RxJavaInterop() {
        throw new IllegalStateException("No instances!");
    }

    // -----------------------------------------------------------------------------------------
    // Conversions to 2.x
    // -----------------------------------------------------------------------------------------

    /**
     * Converts an 1.x Observable into a 2.x Flowable, composing the backpressure and
     * cancellation (unsubscription) through.
     * <p>
     * Note that in 1.x Observable's backpressure somewhat optional; you may need to apply
     * one of the onBackpressureXXX operators on the source Observable or the resulting Flowable.
     * <dl>
     *  <dt><b>Backpressure:</b></dt>
     *  <dd>The operator doesn't interfere with backpressure which is determined by the
     *  source 1.x {@code Observable}'s backpressure behavior.</dd>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the value type
     * @param source the source 1.x Observable instance, not null
     * @return the new 2.x Flowable instance
     * @throws NullPointerException if {@code source} is null
     */
    public static <T> io.reactivex.Flowable<T> toV2Flowable(rx.Observable<T> source) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return new ObservableV1ToFlowableV2<T>(source);
    }

    /**
     * Converts an 1.x Observable into a 2.x Observable, cancellation (unsubscription) through.
     * <dl>
     *  <dt><b>Backpressure:</b></dt>
     *  <dd>The operator consumes the source 1.x {@code Observable} in an
     *  unbounded manner (without applying backpressure).</dd>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the value type
     * @param source the source 1.x Observable instance, not null
     * @return the new 2.x Observable instance
     * @throws NullPointerException if {@code source} is null
     */
    public static <T> io.reactivex.Observable<T> toV2Observable(rx.Observable<T> source) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return new ObservableV1ToObservableV2<T>(source);
    }

    /**
     * Converts an 1.x Completable into a 2.x Maybe, composing cancellation (unsubscription) through.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the value type
     * @param source the source 1.x Completable instance, not null
     * @return the new 2.x Maybe instance
     * @throws NullPointerException if {@code source} is null
     */
    public static <T> io.reactivex.Maybe<T> toV2Maybe(rx.Completable source) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return new CompletableV1ToMaybeV2<T>(source);
    }

    /**
     * Converts an 1.x Single into a 2.x Maybe, composing cancellation (unsubscription) through.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the value type
     * @param source the source 1.x Single instance, not null
     * @return the new 2.x Maybe instance
     * @throws NullPointerException if {@code source} is null
     */
    public static <T> io.reactivex.Maybe<T> toV2Maybe(rx.Single<T> source) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return new SingleV1ToMaybeV2<T>(source);
    }

    /**
     * Converts an 1.x Single into a 2.x Single, composing cancellation (unsubscription) through.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the value type
     * @param source the source 1.x Single instance, not null
     * @return the new 2.x Single instance
     * @throws NullPointerException if {@code source} is null
     */
    public static <T> io.reactivex.Single<T> toV2Single(rx.Single<T> source) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return new SingleV1ToSingleV2<T>(source);
    }

    /**
     * Converts an 1.x Completable into a 2.x Completable, composing cancellation (unsubscription) through.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param source the source 1.x Completable instance, not null
     * @return the new 2.x Completable instance
     * @throws NullPointerException if {@code source} is null
     */
    public static io.reactivex.Completable toV2Completable(rx.Completable source) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return new CompletableV1ToCompletableV2(source);
    }

    // -----------------------------------------------------------------------------------------
    // Conversions to 1.x
    // -----------------------------------------------------------------------------------------

    /**
     * Converts a Reactive-Streams Publisher of any kind (the base type of 2.x Flowable)
     * into an 1.x Observable, composing the backpressure and cancellation
     * (unsubscription) through.
     * <p>
     * Note that this method can convert <strong>any</strong> Reactive-Streams compliant
     * source into an 1.x Observable, not just the 2.x Flowable.
     * <dl>
     *  <dt><b>Backpressure:</b></dt>
     *  <dd>The operator doesn't interfere with backpressure which is determined by the
     *  source {@code Publisher}'s backpressure behavior.</dd>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the value type
     * @param source the source Reactive-Streams Publisher instance, not null
     * @return the new 1.x Observable instance
     * @throws NullPointerException if {@code source} is null
     */
    public static <T> rx.Observable<T> toV1Observable(org.reactivestreams.Publisher<T> source) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return rx.Observable.create(new FlowableV2ToObservableV1<T>(source));
    }

    /**
     * Converts a 2.x ObservableSource (the base type of 2.x Observable) into an 1.x
     * Observable instance, applying the specified backpressure strategy and composing
     * the cancellation (unsubscription) through.
     * <dl>
     *  <dt><b>Backpressure:</b></dt>
     *  <dd>The operator applies the backpressure strategy you specify.</dd>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the value type
     * @param source the source ObservableSource instance, not null
     * @param strategy the backpressure strategy to apply: BUFFER, DROP or LATEST.
     * @return the new 1.x Observable instance
     * @throws NullPointerException if {@code source} is null
     */
    public static <T> rx.Observable<T> toV1Observable(io.reactivex.ObservableSource<T> source, io.reactivex.BackpressureStrategy strategy) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(strategy, "strategy is null");
        return toV1Observable(io.reactivex.Observable.wrap(source).toFlowable(strategy));
    }

    /**
     * Converts an 2.x SingleSource (the base type of 2.x Single) into a
     * 1.x Single, composing cancellation (unsubscription) through.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the value type
     * @param source the source 2.x SingleSource instance, not null
     * @return the new 1.x Single instance
     * @throws NullPointerException if {@code source} is null
     */
    public static <T> rx.Single<T> toV1Single(io.reactivex.SingleSource<T> source) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return rx.Single.create(new SingleV2ToSingleV1<T>(source));
    }

    /**
     * Converts an 2.x CompletableSource (the base type of 2.x Completable) into a
     * 1.x Completable, composing cancellation (unsubscription) through.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param source the source 2.x CompletableSource instance, not null
     * @return the new 1.x Completable instance
     * @throws NullPointerException if {@code source} is null
     */
    public static rx.Completable toV1Completable(io.reactivex.CompletableSource source) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return rx.Completable.create(new CompletableV2ToCompletableV1(source));
    }

    /**
     * Converts an 2.x MaybeSource (the base type of 2.x Maybe) into a
     * 1.x Single, composing cancellation (unsubscription) through and
     * signalling NoSuchElementException if the MaybeSource is empty.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the source's value type
     * @param source the source 2.x MaybeSource instance, not null
     * @return the new 1.x Single instance
     * @throws NullPointerException if {@code source} is null
     */
    public static <T> rx.Single<T> toV1Single(io.reactivex.MaybeSource<T> source) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return rx.Single.create(new MaybeV2ToSingleV1<T>(source));
    }

    /**
     * Converts an 2.x MaybeSource (the base type of 2.x Maybe) into a
     * 1.x Completable, composing cancellation (unsubscription) through
     * and ignoring the success value.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the source's value type
     * @param source the source 2.x MaybeSource instance, not null
     * @return the new 1.x Completable instance
     * @throws NullPointerException if {@code source} is null
     */
    public static <T> rx.Completable toV1Completable(io.reactivex.MaybeSource<T> source) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return rx.Completable.create(new MaybeV2ToCompletableV1<T>(source));
    }
}
