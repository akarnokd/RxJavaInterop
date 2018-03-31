# RxJava2Interop


<a href='https://travis-ci.org/akarnokd/RxJava2Interop/builds'><img src='https://travis-ci.org/akarnokd/RxJava2Interop.svg?branch=master'></a>
[![codecov.io](http://codecov.io/github/akarnokd/RxJava2Interop/coverage.svg?branch=master)](http://codecov.io/github/akarnokd/RxJava2Interop?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.akarnokd/rxjava2-interop/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.akarnokd/rxjava2-interop)

RxJava 1.x: [![RxJava 1.x](https://maven-badges.herokuapp.com/maven-central/io.reactivex/rxjava/badge.svg)](https://maven-badges.herokuapp.com/maven-central/maven-central/io.reactivex/rxjava)
RxJava 2.x: [![RxJava 2.x](https://maven-badges.herokuapp.com/maven-central/io.reactivex.rxjava2/rxjava/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.reactivex.rxjava2/rxjava)

Library to convert between RxJava 1.x and 2.x reactive types.

# Releases


**gradle**

```
dependencies {
    compile "com.github.akarnokd:rxjava2-interop:0.12.6"
}
```


Maven search:

[http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.akarnokd%22)

# Usage

### Convert between the reactive base types

```java
import hu.akarnokd.rxjava.interop.RxJavaInterop;

// convert from 1.x to 2.x

io.reactivex.Flowable    f2 = RxJavaInterop.toV2Flowable(rx.Observable);

io.reactivex.Observable  o2 = RxJavaInterop.toV2Observable(rx.Observable);

io.reactive.Single       s2 = RxJavaInterop.toV2Single(rx.Single);

io.reactivex.Completable c2 = RxJavaInterop.toV2Completable(rx.Completable);

io.reactivex.Maybe m2s = RxJavaInterop.toV2Maybe(rx.Single);

io.reactivex.Maybe m2c = RxJavaInterop.toV2Maybe(rx.Completable);

// convert from 2.x to 1.x

rx.Observable  o1 = RxJavaInterop.toV1Observable(Publisher);

rx.Observable  q1 = RxJavaInterop.toV1Observable(ObservableSource, BackpressureStrategy);

rx.Single      s1 = RxJavaInterop.toV1Single(SingleSource);

rx.Completable c1 = RxJavaInterop.toV1Completable(CompletableSource);

rx.Single      s1m = RxJavaInterop.toV1Single(MaybeSource);

rx.Completable c1m = RxJavaInterop.toV1Completable(MaybeSource);
```

### Convert between Subjects and Processors. 

Note that 2.x `Subject`s and `FlowableProcessor`s support only the same input and output types.

```java
// convert from 1.x to 2.x

io.reactivex.subjects.Subject sj2 = RxJavaInterop.toV2Subject(rx.subjects.Subject);

io.reactivex.processors.FlowableProcessor fp2 = RxJavaInterop.toV2Processor(rx.subjects.Subject);

// convert from 2.x to 1.x

rx.subjects.Subject sj1 = RxJavaInterop.toV1Subject(io.reactivex.subjects.Subject);

rx.subjects.Subject sj1b = RxJavaInterop.toV1Subject(io.reactivex.processors.FlowableProcessor);
```

### Convert between 1.x `X.Transformer`s and 2.x `XTransformer`s.

```java
// convert from 1.x to 2.x

io.reactivex.FlowableTransformer ft2 = RxJavaInterop.toV2Transformer(rx.Observable.Transformer);

io.reactivex.ObservableTransformer ot2 = RxJavaInterop.toV2Transformer(
                                             rx.Observable.Transformer, io.reactivex.BackpressureStrategy);

io.reactivex.SingleTransformer st2 = RxJavaInterop.toV2Transformer(rx.Single.Transformer);

io.reactivex.CompletableTransformer ct2 = RxJavaInterop.toV2Transformer(rx.Completable.Transformer);

// convert from 2.x to 1.x

rx.Observable.Transformer ft1 = RxJavaInterop.toV1Transformer(io.reactivex.FlowableTransformer);

rx.Observable.Transformer ot1 = RxJavaInterop.toV1Transformer(
                                                  io.reactivex.ObservableTransformer, io.reactivex.BackpressureStrategy);

rx.Single.Transformer st1 = RxJavaInterop.toV1Transformer(io.reactivex.SingleTransformer);

rx.Completable.Transformer ct1 = RxJavaInterop.toV1Transformer(io.reactivex.CompletableTransformer);
```

### Convert between 1.x `Flowable.Operator` and 2.x `FlowableOperator`

```java
// convert from 1.x to 2.x

io.reactivex.FlowableOperator fo2 = RxJavaInterop.toV2Operator(rx.Observable.Operator);

// convert from 2.x to 1.x

rx.Observable.Operator fo1 = RxJavaInterop.toV1Operator(io.reactivex.FlowableOperator);
```

### Convert between 1.x `Subscription` and 2.x `Disposable`

```java
// convert from 1.x to 2.x

io.reactivex.disposables.Disposable d2 = RxJavaInterop.toV2Disposable(rx.Subscription);

// convert from 2.x to 1.x

rx.Subscription s1 = RxJavaInterop.toV1Subscription(io.reactivex.disposables.Disposable);
```


### Convert between 1.x `Scheduler`s and 2.x `Scheduler`s

```java
// convert from 1.x to 2.x

io.reactivex.Scheduler s2 = RxJavaInterop.toV2Scheduler(rx.Scheduler);

// convert from 2.x to 1.x

rx.Scheduler s1 = RxJavaInterop.toV1Scheduler(io.reactivex.Scheduler);
```
