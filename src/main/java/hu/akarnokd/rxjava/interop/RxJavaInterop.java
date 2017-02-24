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
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
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
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
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
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
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
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
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
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
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
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
    public static io.reactivex.Completable toV2Completable(rx.Completable source) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return new CompletableV1ToCompletableV2(source);
    }

    /**
     * Wraps a 1.x Subject into a 2.x Subject.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input and output value type of the Subjects
     * @param subject the subject to wrap with the same input and output type;
     * 2.x Subject supports only the same input and output type
     * @return the new 2.x Subject instance
     * @throws NullPointerException if {@code source} is null
     * @since 0.9.0
     */
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
    public static <T> io.reactivex.subjects.Subject<T> toV2Subject(rx.subjects.Subject<T, T> subject) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(subject, "subject is null");
        return new SubjectV1ToSubjectV2<T>(subject);
    }

    /**
     * Wraps a 1.x Subject into a 2.x FlowableProcessor.
     * <dl>
     *  <dt><b>Backpressure:</b></dt>
     *  <dd>The operator doesn't interfere with backpressure which is determined by the
     *  source 1.x {@code Subject}'s backpressure behavior.</dd>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input and output value type of the Subjects
     * @param subject the subject to wrap with the same input and output type;
     * 2.x FlowableProcessor supports only the same input and output type
     * @return the new 2.x FlowableProcessor instance
     * @throws NullPointerException if {@code source} is null
     * @since 0.9.0
     */
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
    public static <T> io.reactivex.processors.FlowableProcessor<T> toV2Processor(rx.subjects.Subject<T, T> subject) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(subject, "subject is null");
        return new SubjectV1ToProcessorV2<T>(subject);
    }

    /**
     * Convert the 1.x Observable.Transformer into a 2.x FlowableTransformer.
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
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
    public static <T, R> io.reactivex.FlowableTransformer<T, R> toV2Transformer(final rx.Observable.Transformer<T, R> transformer) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(transformer, "transformer is null");
        return new io.reactivex.FlowableTransformer<T, R>() {
            @Override
            public org.reactivestreams.Publisher<R> apply(io.reactivex.Flowable<T> f) {
                return toV2Flowable(transformer.call(toV1Observable(f)));
            }
        };
    }

    /**
     * Convert the 1.x Single.Transformer into a 2.x SingleTransformer.
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
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
    public static <T, R> io.reactivex.SingleTransformer<T, R> toV2Transformer(final rx.Single.Transformer<T, R> transformer) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(transformer, "transformer is null");
        return new io.reactivex.SingleTransformer<T, R>() {
            @Override
            public io.reactivex.Single<R> apply(io.reactivex.Single<T> f) {
                return toV2Single(transformer.call(toV1Single(f)));
            }
        };
    }

    /**
     * Convert the 1.x Completable.Transformer into a 2.x CompletableTransformer.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param transformer the 1.x Completable.Transformer to convert
     * @return the new CompletableTransformer instance
     * @since 0.9.0
     */
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
    public static io.reactivex.CompletableTransformer toV2Transformer(final rx.Completable.Transformer transformer) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(transformer, "transformer is null");
        return new io.reactivex.CompletableTransformer() {
            @Override
            public io.reactivex.Completable apply(io.reactivex.Completable f) {
                return toV2Completable(transformer.call(toV1Completable(f)));
            }
        };
    }

    /**
     * Convert the 1.x Observable.Operator into a 2.x FlowableOperator.
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
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
    public static <T, R> io.reactivex.FlowableOperator<R, T> toV2Operator(final rx.Observable.Operator<R, T> operator) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(operator, "operator is null");
        return new io.reactivex.FlowableOperator<R, T>() {
            @Override
            public org.reactivestreams.Subscriber<? super T> apply(org.reactivestreams.Subscriber<? super R> s) throws Exception {
                hu.akarnokd.rxjava.interop.ObservableV1ToFlowableV2.ObservableSubscriber<R> parent = new hu.akarnokd.rxjava.interop.ObservableV1ToFlowableV2.ObservableSubscriber<R>(s);
                hu.akarnokd.rxjava.interop.ObservableV1ToFlowableV2.ObservableSubscriberSubscription parentSubscription = new hu.akarnokd.rxjava.interop.ObservableV1ToFlowableV2.ObservableSubscriberSubscription(parent);
                s.onSubscribe(parentSubscription);

                rx.Subscriber<? super T> t;
                
                try {
                    t = io.reactivex.internal.functions.ObjectHelper.requireNonNull(operator.call(parent), "The operator returned a null rx.Subscriber");
                } catch (Throwable ex) {
                    io.reactivex.exceptions.Exceptions.throwIfFatal(ex);
                    rx.exceptions.Exceptions.throwIfFatal(ex);
                    s.onError(ex);
                    t = rx.observers.Subscribers.empty();
                    t.unsubscribe();
                }

                hu.akarnokd.rxjava.interop.FlowableV2ToObservableV1.SourceSubscriber<T> z = new hu.akarnokd.rxjava.interop.FlowableV2ToObservableV1.SourceSubscriber<T>(t);

                t.add(z);
                t.setProducer(z);

                return z;
            }
        };
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
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
    public static <T> rx.Observable<T> toV1Observable(org.reactivestreams.Publisher<T> source) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return rx.Observable.unsafeCreate(new FlowableV2ToObservableV1<T>(source));
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
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
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
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
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
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
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
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
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
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
    public static <T> rx.Completable toV1Completable(io.reactivex.MaybeSource<T> source) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(source, "source is null");
        return rx.Completable.create(new MaybeV2ToCompletableV1<T>(source));
    }

    /**
     * Wraps a 2.x Subject into a 1.x Subject.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input and output value type of the Subjects
     * @param subject the subject to wrap with the same input and output type;
     * 2.x Subject supports only the same input and output type
     * @return the new 1.x Subject instance
     * @throws NullPointerException if {@code source} is null
     * @since 0.9.0
     */
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
    public static <T> rx.subjects.Subject<T, T> toV1Subject(io.reactivex.subjects.Subject<T> subject) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(subject, "subject is null");
        return SubjectV2ToSubjectV1.<T>createWith(subject);
    }

    /**
     * Wraps a 2.x FlowableProcessor into a 1.x Subject.
     * <dl>
     *  <dt><b>Backpressure:</b></dt>
     *  <dd>The operator doesn't interfere with backpressure which is determined by the
     *  source {@code FlowableProcessor}'s backpressure behavior.</dd>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input and output value type of the Subjects
     * @param processor the flowable-processor to wrap with the same input and output type;
     * 2.x FlowableProcessor supports only the same input and output type
     * @return the new 1.x Subject instance
     * @throws NullPointerException if {@code source} is null
     * @since 0.9.0
     */
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
    public static <T> rx.subjects.Subject<T, T> toV1Subject(io.reactivex.processors.FlowableProcessor<T> processor) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(processor, "processor is null");
        return ProcessorV2ToSubjectV1.<T>createWith(processor);
    }

    /**
     * Convert the 2.x FlowableTransformer into a 1.x Observable.Transformer.
     * <dl>
     *  <dt><b>Backpressure:</b></dt>
     *  <dd>The operator doesn't interfere with backpressure which is determined by the
     *  2.x Flowable returned by the 2.x FlowableTransformer.</dd>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input value type
     * @param <R> the output value type
     * @param transformer the 2.x FlowableTransformer to convert
     * @return the new Observable.Transformer instance
     * @since 0.9.0
     */
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
    public static <T, R> rx.Observable.Transformer<T, R> toV1Transformer(final io.reactivex.FlowableTransformer<T, R> transformer) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(transformer, "transformer is null");
        return new rx.Observable.Transformer<T, R>() {
            @Override
            public rx.Observable<R> call(rx.Observable<T> f) {
                return toV1Observable(transformer.apply(toV2Flowable(f)));
            }
        };
    }

    /**
     * Convert the 2.x SingleTransformer into a 1.x Single.Transformer.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input value type
     * @param <R> the output value type
     * @param transformer the 2.x SingleTransformer to convert
     * @return the new Single.Transformer instance
     * @since 0.9.0
     */
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
    public static <T, R> rx.Single.Transformer<T, R> toV1Transformer(final io.reactivex.SingleTransformer<T, R> transformer) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(transformer, "transformer is null");
        return new rx.Single.Transformer<T, R>() {
            @Override
            public rx.Single<R> call(rx.Single<T> f) {
                return toV1Single(transformer.apply(toV2Single(f)));
            }
        };
    }

    /**
     * Convert the 2.x CompletableTransformer into a 1.x Completable.Transformer.
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param transformer the 2.x CompletableTransformer to convert
     * @return the new Completable.Transformer instance
     * @since 0.9.0
     */
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
    public static rx.Completable.Transformer toV1Transformer(final io.reactivex.CompletableTransformer transformer) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(transformer, "transformer is null");
        return new rx.Completable.Transformer() {
            @Override
            public rx.Completable call(rx.Completable f) {
                return toV1Completable(transformer.apply(toV2Completable(f)));
            }
        };
    }

    /**
     * Convert the 2.x FlowableOperator into a 1.x Observable.Operator.
     * <dl>
     *  <dt><b>Backpressure:</b></dt>
     *  <dd>The operator doesn't interfere with backpressure which is determined by the
     *  2.x Subscriber returned by the 2.x FlowableOperator.</dd>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>The method does not operate by default on a particular {@code Scheduler}.</dd>
     * </dl>
     * @param <T> the input value type
     * @param <R> the output value type
     * @param operator the 2.x FlowableOperator to convert
     * @return the new Observable.Operator instance
     * @since 0.9.0
     */
    @io.reactivex.annotations.SchedulerSupport(io.reactivex.annotations.SchedulerSupport.NONE)
    public static <T, R> rx.Observable.Operator<R, T> toV1Operator(final io.reactivex.FlowableOperator<R, T> operator) {
        io.reactivex.internal.functions.ObjectHelper.requireNonNull(operator, "operator is null");
        return new rx.Observable.Operator<R, T>() {
            @Override
            public rx.Subscriber<? super T> call(rx.Subscriber<? super R> t) {
                hu.akarnokd.rxjava.interop.FlowableV2ToObservableV1.SourceSubscriber<R> z = new hu.akarnokd.rxjava.interop.FlowableV2ToObservableV1.SourceSubscriber<R>(t);

                t.add(z);
                t.setProducer(z);

                org.reactivestreams.Subscriber<? super T> s;

                try {
                    s = io.reactivex.internal.functions.ObjectHelper.requireNonNull(operator.apply(z), "The operator returned a null Subscriber");
                } catch (Throwable ex) {
                    io.reactivex.exceptions.Exceptions.throwIfFatal(ex);
                    rx.exceptions.Exceptions.throwIfFatal(ex);
                    t.onError(ex);
                    rx.Subscriber<? super T> r = rx.observers.Subscribers.empty();
                    r.unsubscribe();
                    return r;
                }

                hu.akarnokd.rxjava.interop.ObservableV1ToFlowableV2.ObservableSubscriber<T> parent = new hu.akarnokd.rxjava.interop.ObservableV1ToFlowableV2.ObservableSubscriber<T>(s);
                hu.akarnokd.rxjava.interop.ObservableV1ToFlowableV2.ObservableSubscriberSubscription parentSubscription = new hu.akarnokd.rxjava.interop.ObservableV1ToFlowableV2.ObservableSubscriberSubscription(parent);
                s.onSubscribe(parentSubscription);

                return parent;
            }
        };
    }
}
