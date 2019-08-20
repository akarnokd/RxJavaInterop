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

/**
 * Conversion methods for converting between 1.x and 3.x reactive types, composing backpressure
 * and cancellation through.
 */
public final class RxJavaInterop {
    /** Utility class. */
    private RxJavaInterop() {
        throw new IllegalStateException("No instances!");
    }

    // -----------------------------------------------------------------------------------------
    // Conversions to 3.x
    // -----------------------------------------------------------------------------------------

    /**
     * Converts an 1.x Observable into a 3.x Flowable, composing the backpressure and
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
     * @return the new 3.x Flowable instance
     * @throws NullPointerException if {@code source} is null
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T> io.reactivex.rxjava3.core.Flowable<T> toV3Flowable(rx.Observable<T> source) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return new ObservableV1ToFlowableV3<T>(source);
    }

    /**
     * Converts an 1.x Observable into a 3.x Observable, cancellation (unsubscription) through.
     * <dl>
     *  <dt><b>Backpressure:</b></dt>
     *  <dd>The operator consumes the source 1.x {@code Observable} in an
     *  unbounded manner (without applying backpressure).</dd>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the value type
     * @param source the source 1.x Observable instance, not null
     * @return the new 3.x Observable instance
     * @throws NullPointerException if {@code source} is null
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T> io.reactivex.rxjava3.core.Observable<T> toV3Observable(rx.Observable<T> source) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return new ObservableV1ToObservableV3<T>(source);
    }

    /**
     * Converts an 1.x Completable into a 3.x Maybe, composing cancellation (unsubscription) through.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the value type
     * @param source the source 1.x Completable instance, not null
     * @return the new 3.x Maybe instance
     * @throws NullPointerException if {@code source} is null
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T> io.reactivex.rxjava3.core.Maybe<T> toV3Maybe(rx.Completable source) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return new CompletableV1ToMaybeV3<T>(source);
    }

    /**
     * Converts an 1.x Single into a 3.x Maybe, composing cancellation (unsubscription) through.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the value type
     * @param source the source 1.x Single instance, not null
     * @return the new 3.x Maybe instance
     * @throws NullPointerException if {@code source} is null
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T> io.reactivex.rxjava3.core.Maybe<T> toV3Maybe(rx.Single<T> source) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return new SingleV1ToMaybeV3<T>(source);
    }

    /**
     * Converts an 1.x Single into a 3.x Single, composing cancellation (unsubscription) through.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the value type
     * @param source the source 1.x Single instance, not null
     * @return the new 3.x Single instance
     * @throws NullPointerException if {@code source} is null
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T> io.reactivex.rxjava3.core.Single<T> toV3Single(rx.Single<T> source) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return new SingleV1ToSingleV3<T>(source);
    }

    /**
     * Converts an 1.x Completable into a 3.x Completable, composing cancellation (unsubscription) through.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param source the source 1.x Completable instance, not null
     * @return the new 3.x Completable instance
     * @throws NullPointerException if {@code source} is null
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static io.reactivex.rxjava3.core.Completable toV3Completable(rx.Completable source) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return new CompletableV1ToCompletableV3(source);
    }

    /**
     * Wraps a 1.x Subject into a 3.x Subject.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input and output value type of the Subjects
     * @param subject the subject to wrap with the same input and output type;
     * 3.x Subject supports only the same input and output type
     * @return the new 3.x Subject instance
     * @throws NullPointerException if {@code source} is null
     * @since 0.9.0
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T> io.reactivex.rxjava3.subjects.Subject<T> toV3Subject(rx.subjects.Subject<T, T> subject) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(subject, "subject is null");
        return new SubjectV1ToSubjectV3<T>(subject);
    }

    /**
     * Wraps a 1.x Subject into a 3.x FlowableProcessor.
     * <dl>
     *  <dt><b>Backpressure:</b></dt>
     *  <dd>The operator doesn't interfere with backpressure which is determined by the
     *  source 1.x {@code Subject}'s backpressure behavior.</dd>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input and output value type of the Subjects
     * @param subject the subject to wrap with the same input and output type;
     * 3.x FlowableProcessor supports only the same input and output type
     * @return the new 3.x FlowableProcessor instance
     * @throws NullPointerException if {@code source} is null
     * @since 0.9.0
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T> io.reactivex.rxjava3.processors.FlowableProcessor<T> toV3Processor(rx.subjects.Subject<T, T> subject) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(subject, "subject is null");
        return new SubjectV1ToProcessorV3<T>(subject);
    }

    /**
     * Convert the 1.x Observable.Transformer into a 3.x FlowableTransformer.
     * <dl>
     *  <dt><b>Backpressure:</b></dt>
     *  <dd>The operator doesn't interfere with backpressure which is determined by the
     *  1.x Observable returned by the 1.x Transformer.</dd>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input value type
     * @param <R> the output value type
     * @param transformer the 1.x Observable.Transformer to convert
     * @return the new FlowableTransformer instance
     * @since 0.9.0
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T, R> io.reactivex.rxjava3.core.FlowableTransformer<T, R> toV3Transformer(final rx.Observable.Transformer<T, R> transformer) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(transformer, "transformer is null");
        return new io.reactivex.rxjava3.core.FlowableTransformer<T, R>() {
            @Override
            public org.reactivestreams.Publisher<R> apply(io.reactivex.rxjava3.core.Flowable<T> f) {
                return toV3Flowable(transformer.call(toV1Observable(f)));
            }
        };
    }

    /**
     * Convert the 1.x Observable.Transformer into a 3.x ObservableTransformer.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input value type
     * @param <R> the output value type
     * @param strategy the backpressure strategy to apply: BUFFER, DROP or LATEST.
     * @param transformer the 1.x Observable.Transformer to convert
     * @return the new ObservableTransformer instance
     * @since 0.12.0
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T, R> io.reactivex.rxjava3.core.ObservableTransformer<T, R> toV3Transformer(final rx.Observable.Transformer<T, R> transformer,
        final io.reactivex.rxjava3.core.BackpressureStrategy strategy) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(transformer, "transformer is null");
        return new io.reactivex.rxjava3.core.ObservableTransformer<T, R>() {
            @Override
            public io.reactivex.rxjava3.core.ObservableSource<R> apply(io.reactivex.rxjava3.core.Observable<T> obs) {
                return toV3Observable(transformer.call(toV1Observable(obs, strategy)));
            }
        };
    }

    /**
     * Convert the 1.x Single.Transformer into a 3.x SingleTransformer.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input value type
     * @param <R> the output value type
     * @param transformer the 1.x Single.Transformer to convert
     * @return the new SingleTransformer instance
     * @since 0.9.0
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T, R> io.reactivex.rxjava3.core.SingleTransformer<T, R> toV3Transformer(final rx.Single.Transformer<T, R> transformer) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(transformer, "transformer is null");
        return new io.reactivex.rxjava3.core.SingleTransformer<T, R>() {
            @Override
            public io.reactivex.rxjava3.core.Single<R> apply(io.reactivex.rxjava3.core.Single<T> f) {
                return toV3Single(transformer.call(toV1Single(f)));
            }
        };
    }

    /**
     * Convert the 1.x Completable.Transformer into a 3.x CompletableTransformer.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param transformer the 1.x Completable.Transformer to convert
     * @return the new CompletableTransformer instance
     * @since 0.9.0
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static io.reactivex.rxjava3.core.CompletableTransformer toV3Transformer(final rx.Completable.Transformer transformer) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(transformer, "transformer is null");
        return new io.reactivex.rxjava3.core.CompletableTransformer() {
            @Override
            public io.reactivex.rxjava3.core.Completable apply(io.reactivex.rxjava3.core.Completable f) {
                return toV3Completable(transformer.call(toV1Completable(f)));
            }
        };
    }

    /**
     * Convert the 1.x Observable.Operator into a 3.x FlowableOperator.
     * <dl>
     *  <dt><b>Backpressure:</b></dt>
     *  <dd>The operator doesn't interfere with backpressure which is determined by the
     *  1.x Subscriber returned by the 1.x Operator.</dd>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input value type
     * @param <R> the output value type
     * @param operator the 1.x Observable.Operator to convert
     * @return the new FlowableOperator instance
     * @since 0.9.0
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T, R> io.reactivex.rxjava3.core.FlowableOperator<R, T> toV3Operator(final rx.Observable.Operator<R, T> operator) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(operator, "operator is null");
        return new io.reactivex.rxjava3.core.FlowableOperator<R, T>() {
            @Override
            public org.reactivestreams.Subscriber<? super T> apply(org.reactivestreams.Subscriber<? super R> s) throws Exception {
                hu.akarnokd.rxjava3.interop.ObservableV1ToFlowableV3.ObservableSubscriber<R> parent = new hu.akarnokd.rxjava3.interop.ObservableV1ToFlowableV3.ObservableSubscriber<R>(s);
                hu.akarnokd.rxjava3.interop.ObservableV1ToFlowableV3.ObservableSubscriberSubscription parentSubscription = new hu.akarnokd.rxjava3.interop.ObservableV1ToFlowableV3.ObservableSubscriberSubscription(parent);
                s.onSubscribe(parentSubscription);

                rx.Subscriber<? super T> t;

                try {
                    t = io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(operator.call(parent), "The operator returned a null rx.Subscriber");
                } catch (Throwable ex) {
                    io.reactivex.rxjava3.exceptions.Exceptions.throwIfFatal(ex);
                    rx.exceptions.Exceptions.throwIfFatal(ex);
                    s.onError(ex);
                    t = rx.observers.Subscribers.empty();
                    t.unsubscribe();
                }

                hu.akarnokd.rxjava3.interop.FlowableV3ToObservableV1.SourceSubscriber<T> z = new hu.akarnokd.rxjava3.interop.FlowableV3ToObservableV1.SourceSubscriber<T>(t);

                t.add(z);
                t.setProducer(z);

                return z;
            }
        };
    }

    /**
     * Convert the 1.x {@link rx.Subscription} into a 3.x {@link io.reactivex.rxjava3.disposables.Disposable}.
     * @param subscription the 1.x Subscription to convert
     * @return the new Disposable instance
     * @since 0.11.0
     */
    public static io.reactivex.rxjava3.disposables.Disposable toV3Disposable(final rx.Subscription subscription) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(subscription, "subscription is null");
        return new SubscriptionV1ToDisposableV3(subscription);
    }

    // -----------------------------------------------------------------------------------------
    // Conversions to 1.x
    // -----------------------------------------------------------------------------------------

    /**
     * Converts a Reactive-Streams Publisher of any kind (the base type of 3.x Flowable)
     * into an 1.x Observable, composing the backpressure and cancellation
     * (unsubscription) through.
     * <p>
     * Note that this method can convert <strong>any</strong> Reactive-Streams compliant
     * source into an 1.x Observable, not just the 3.x Flowable.
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
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T> rx.Observable<T> toV1Observable(org.reactivestreams.Publisher<T> source) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return rx.Observable.unsafeCreate(new FlowableV3ToObservableV1<T>(source));
    }

    /**
     * Converts a 3.x ObservableSource (the base type of 3.x Observable) into an 1.x
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
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T> rx.Observable<T> toV1Observable(io.reactivex.rxjava3.core.ObservableSource<T> source, io.reactivex.rxjava3.core.BackpressureStrategy strategy) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(strategy, "strategy is null");
        return toV1Observable(io.reactivex.rxjava3.core.Observable.wrap(source).toFlowable(strategy));
    }

    /**
     * Converts an 3.x SingleSource (the base type of 3.x Single) into a
     * 1.x Single, composing cancellation (unsubscription) through.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the value type
     * @param source the source 3.x SingleSource instance, not null
     * @return the new 1.x Single instance
     * @throws NullPointerException if {@code source} is null
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T> rx.Single<T> toV1Single(io.reactivex.rxjava3.core.SingleSource<T> source) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return rx.Single.create(new SingleV3ToSingleV1<T>(source));
    }

    /**
     * Converts an 3.x CompletableSource (the base type of 3.x Completable) into a
     * 1.x Completable, composing cancellation (unsubscription) through.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param source the source 3.x CompletableSource instance, not null
     * @return the new 1.x Completable instance
     * @throws NullPointerException if {@code source} is null
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static rx.Completable toV1Completable(io.reactivex.rxjava3.core.CompletableSource source) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return rx.Completable.create(new CompletableV3ToCompletableV1(source));
    }

    /**
     * Converts an 3.x MaybeSource (the base type of 3.x Maybe) into a
     * 1.x Single, composing cancellation (unsubscription) through and
     * signalling NoSuchElementException if the MaybeSource is empty.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the source's value type
     * @param source the source 3.x MaybeSource instance, not null
     * @return the new 1.x Single instance
     * @throws NullPointerException if {@code source} is null
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T> rx.Single<T> toV1Single(io.reactivex.rxjava3.core.MaybeSource<T> source) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return rx.Single.create(new MaybeV3ToSingleV1<T>(source));
    }

    /**
     * Converts an 3.x MaybeSource (the base type of 3.x Maybe) into a
     * 1.x Completable, composing cancellation (unsubscription) through
     * and ignoring the success value.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the source's value type
     * @param source the source 3.x MaybeSource instance, not null
     * @return the new 1.x Completable instance
     * @throws NullPointerException if {@code source} is null
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T> rx.Completable toV1Completable(io.reactivex.rxjava3.core.MaybeSource<T> source) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return rx.Completable.create(new MaybeV3ToCompletableV1<T>(source));
    }

    /**
     * Wraps a 3.x Subject into a 1.x Subject.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input and output value type of the Subjects
     * @param subject the subject to wrap with the same input and output type;
     * 3.x Subject supports only the same input and output type
     * @return the new 1.x Subject instance
     * @throws NullPointerException if {@code source} is null
     * @since 0.9.0
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T> rx.subjects.Subject<T, T> toV1Subject(io.reactivex.rxjava3.subjects.Subject<T> subject) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(subject, "subject is null");
        return SubjectV3ToSubjectV1.<T>createWith(subject);
    }

    /**
     * Wraps a 3.x FlowableProcessor into a 1.x Subject.
     * <dl>
     *  <dt><b>Backpressure:</b></dt>
     *  <dd>The operator doesn't interfere with backpressure which is determined by the
     *  source {@code FlowableProcessor}'s backpressure behavior.</dd>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input and output value type of the Subjects
     * @param processor the flowable-processor to wrap with the same input and output type;
     * 3.x FlowableProcessor supports only the same input and output type
     * @return the new 1.x Subject instance
     * @throws NullPointerException if {@code source} is null
     * @since 0.9.0
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T> rx.subjects.Subject<T, T> toV1Subject(io.reactivex.rxjava3.processors.FlowableProcessor<T> processor) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(processor, "processor is null");
        return ProcessorV3ToSubjectV1.<T>createWith(processor);
    }

    /**
     * Convert the 3.x FlowableTransformer into a 1.x Observable.Transformer.
     * <dl>
     *  <dt><b>Backpressure:</b></dt>
     *  <dd>The operator doesn't interfere with backpressure which is determined by the
     *  3.x Flowable returned by the 3.x FlowableTransformer.</dd>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input value type
     * @param <R> the output value type
     * @param transformer the 3.x FlowableTransformer to convert
     * @return the new Observable.Transformer instance
     * @since 0.9.0
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T, R> rx.Observable.Transformer<T, R> toV1Transformer(final io.reactivex.rxjava3.core.FlowableTransformer<T, R> transformer) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(transformer, "transformer is null");
        return new rx.Observable.Transformer<T, R>() {
            @Override
            public rx.Observable<R> call(rx.Observable<T> f) {
                return toV1Observable(transformer.apply(toV3Flowable(f)));
            }
        };
    }

    /**
     * Convert the 3.x ObservableTransformer into a 1.x Observable.Transformer.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input value type
     * @param <R> the output value type
     * @param transformer the 3.x ObservableTransformer to convert
     * @param strategy the backpressure strategy to apply: BUFFER, DROP or LATEST.
     * @return the new Observable.Transformer instance
     * @since 0.12.0
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T, R> rx.Observable.Transformer<T, R> toV1Transformer(final io.reactivex.rxjava3.core.ObservableTransformer<T, R> transformer,
        final io.reactivex.rxjava3.core.BackpressureStrategy strategy) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(transformer, "transformer is null");
        return new rx.Observable.Transformer<T, R>() {
            @Override
            public rx.Observable<R> call(rx.Observable<T> obs) {
                return toV1Observable(transformer.apply(toV3Observable(obs)), strategy);
            }
        };
    }

    /**
     * Convert the 3.x SingleTransformer into a 1.x Single.Transformer.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input value type
     * @param <R> the output value type
     * @param transformer the 3.x SingleTransformer to convert
     * @return the new Single.Transformer instance
     * @since 0.9.0
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T, R> rx.Single.Transformer<T, R> toV1Transformer(final io.reactivex.rxjava3.core.SingleTransformer<T, R> transformer) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(transformer, "transformer is null");
        return new rx.Single.Transformer<T, R>() {
            @Override
            public rx.Single<R> call(rx.Single<T> f) {
                return toV1Single(transformer.apply(toV3Single(f)));
            }
        };
    }

    /**
     * Convert the 3.x CompletableTransformer into a 1.x Completable.Transformer.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param transformer the 3.x CompletableTransformer to convert
     * @return the new Completable.Transformer instance
     * @since 0.9.0
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static rx.Completable.Transformer toV1Transformer(final io.reactivex.rxjava3.core.CompletableTransformer transformer) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(transformer, "transformer is null");
        return new rx.Completable.Transformer() {
            @Override
            public rx.Completable call(rx.Completable f) {
                return toV1Completable(transformer.apply(toV3Completable(f)));
            }
        };
    }

    /**
     * Convert the 3.x FlowableOperator into a 1.x Observable.Operator.
     * <dl>
     *  <dt><b>Backpressure:</b></dt>
     *  <dd>The operator doesn't interfere with backpressure which is determined by the
     *  3.x Subscriber returned by the 3.x FlowableOperator.</dd>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input value type
     * @param <R> the output value type
     * @param operator the 3.x FlowableOperator to convert
     * @return the new Observable.Operator instance
     * @since 0.9.0
     */
    @io.reactivex.rxjava3.annotations.SchedulerSupport(io.reactivex.rxjava3.annotations.SchedulerSupport.NONE)
    public static <T, R> rx.Observable.Operator<R, T> toV1Operator(final io.reactivex.rxjava3.core.FlowableOperator<R, T> operator) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(operator, "operator is null");
        return new rx.Observable.Operator<R, T>() {
            @Override
            public rx.Subscriber<? super T> call(rx.Subscriber<? super R> t) {
                hu.akarnokd.rxjava3.interop.FlowableV3ToObservableV1.SourceSubscriber<R> z = new hu.akarnokd.rxjava3.interop.FlowableV3ToObservableV1.SourceSubscriber<R>(t);

                t.add(z);
                t.setProducer(z);

                org.reactivestreams.Subscriber<? super T> s;

                try {
                    s = io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(operator.apply(z), "The operator returned a null Subscriber");
                } catch (Throwable ex) {
                    io.reactivex.rxjava3.exceptions.Exceptions.throwIfFatal(ex);
                    rx.exceptions.Exceptions.throwIfFatal(ex);
                    t.onError(ex);
                    rx.Subscriber<? super T> r = rx.observers.Subscribers.empty();
                    r.unsubscribe();
                    return r;
                }

                hu.akarnokd.rxjava3.interop.ObservableV1ToFlowableV3.ObservableSubscriber<T> parent = new hu.akarnokd.rxjava3.interop.ObservableV1ToFlowableV3.ObservableSubscriber<T>(s);
                hu.akarnokd.rxjava3.interop.ObservableV1ToFlowableV3.ObservableSubscriberSubscription parentSubscription = new hu.akarnokd.rxjava3.interop.ObservableV1ToFlowableV3.ObservableSubscriberSubscription(parent);
                s.onSubscribe(parentSubscription);

                return parent;
            }
        };
    }

    /**
     * Convert the 3.x {@link io.reactivex.rxjava3.disposables.Disposable} into a 1.x {@link rx.Subscription}.
     * @param disposable the 3.x Disposable to convert
     * @return the new Subscription instance
     * @since 0.11.0
     */
    public static rx.Subscription toV1Subscription(final io.reactivex.rxjava3.disposables.Disposable disposable) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(disposable, "disposable is null");
        return new DisposableV3ToSubscriptionV1(disposable);
    }

    /**
     * Convert the 3.x {@link io.reactivex.rxjava3.core.Scheduler} into a 1.x {@link rx.Scheduler}.
     * @param scheduler the 3.x Scheduler to convert
     * @return the new 1.x Scheduler instance
     * @since 0.12.0
     */
    public static rx.Scheduler toV1Scheduler(io.reactivex.rxjava3.core.Scheduler scheduler) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(scheduler, "scheduler is null");
        return new SchedulerV3ToSchedulerV1(scheduler);
    }

    /**
     * Convert the 3.x {@link io.reactivex.rxjava3.core.Scheduler} into a 1.x {@link rx.Scheduler}.
     * @param scheduler the 1.x Scheduler to convert
     * @return the new 3.x Scheduler instance
     * @since 0.12.0
     */
    public static io.reactivex.rxjava3.core.Scheduler toV3Scheduler(rx.Scheduler scheduler) {
        io.reactivex.rxjava3.internal.functions.ObjectHelper.requireNonNull(scheduler, "scheduler is null");
        return new SchedulerV1ToSchedulerV3(scheduler);
    }
}
