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

/**
 * Wrap a 2.x FlowableProcessor into a 1.x Subject.
 * @param <T> the input and output value type
 * @since 0.9.0
 */
final class ProcessorV2ToSubjectV1<T> extends rx.subjects.Subject<T, T> {

    static <T> rx.subjects.Subject<T, T> createWith(io.reactivex.processors.FlowableProcessor<T> processor) {
        State<T> state = new State<T>(processor);
        return new ProcessorV2ToSubjectV1<T>(state);
    }

    final State<T> state;

    private ProcessorV2ToSubjectV1(State<T> state) {
        super(state);
        this.state = state;
    }

    @Override
    public void onNext(T t) {
        state.onNext(t);
    }

    @Override
    public void onError(Throwable e) {
        state.onError(e);
    }

    @Override
    public void onCompleted() {
        state.onCompleted();
    }

    @Override
    public boolean hasObservers() {
        return state.hasObservers();
    }

    static final class State<T>
    implements rx.Observable.OnSubscribe<T> {

        final io.reactivex.processors.FlowableProcessor<T> processor;

        State(io.reactivex.processors.FlowableProcessor<T> processor) {
            this.processor = processor;
        }

        @Override
        public void call(rx.Subscriber<? super T> t) {
            hu.akarnokd.rxjava.interop.FlowableV2ToObservableV1.SourceSubscriber<T> parent =
                    new hu.akarnokd.rxjava.interop.FlowableV2ToObservableV1.SourceSubscriber<T>(t);

            t.add(parent);
            t.setProducer(parent);

            processor.subscribe(parent);
        }

        void onNext(T t) {
            processor.onNext(t);
        }

        void onError(Throwable e) {
            processor.onError(e);
        }

        void onCompleted() {
            processor.onComplete();
        }

        boolean hasObservers() {
            return processor.hasSubscribers();
        }
    }
}
