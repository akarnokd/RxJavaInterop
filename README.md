# RxJavaInterop


<a href='https://github.com/akarnokd/RxJavaInterop/actions?query=workflow%3A%22Java+CI+with+Gradle%22'><img src='https://github.com/akarnokd/RxJavaInterop/workflows/Java%20CI%20with%20Gradle/badge.svg'></a>
[![codecov.io](http://codecov.io/github/akarnokd/RxJavaInterop/coverage.svg?branch=3.x)](http://codecov.io/github/akarnokd/RxJavaInterop?branch=3.x)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.akarnokd/rxjava3-interop/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.akarnokd/rxjava3-interop)

RxJava 1.x: [![RxJava 1.x](https://maven-badges.herokuapp.com/maven-central/io.reactivex/rxjava/badge.svg)](https://maven-badges.herokuapp.com/maven-central/maven-central/io.reactivex/rxjava)
RxJava 3.x: [![RxJava 3.x](https://maven-badges.herokuapp.com/maven-central/io.reactivex.rxjava3/rxjava/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.reactivex.rxjava3/rxjava)

Library to convert between RxJava 1.x and 3.x reactive types.

### Interop between 2.x and 3.x

Check out the [https://github.com/akarnokd/RxJavaBridge](https://github.com/akarnokd/RxJavaBridge#RxJavaBridge) project.

# Releases


**gradle**

```
dependencies {
    implementation "com.github.akarnokd:rxjava3-interop:3.0.2"
}
```


Maven search:

[http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.akarnokd%22)

# Usage

### Convert between the reactive base types

```java
import hu.akarnokd.rxjava3.interop.RxJavaInterop;

// convert from 1.x to 3.x

io.reactivex.rxjava3.core.Flowable     f3  = RxJavaInterop.toV3Flowable(rx.Observable);

io.reactivex.rxjava3.core.Observable   o3  = RxJavaInterop.toV3Observable(rx.Observable);

io.reactivex.rxjava3.core.Single       s3  = RxJavaInterop.toV3Single(rx.Single);

io.reactivex.rxjava3.core.Completable  c3  = RxJavaInterop.toV3Completable(rx.Completable);

io.reactivex.rxjava3.core.Maybe        m3s = RxJavaInterop.toV3Maybe(rx.Single);

io.reactivex.rxjava3.core.Maybe        m3c = RxJavaInterop.toV3Maybe(rx.Completable);

// convert from 3.x to 1.x

rx.Observable  o1 = RxJavaInterop.toV1Observable(Publisher);

rx.Observable  q1 = RxJavaInterop.toV1Observable(ObservableSource, BackpressureStrategy);

rx.Single      s1 = RxJavaInterop.toV1Single(SingleSource);

rx.Completable c1 = RxJavaInterop.toV1Completable(CompletableSource);

rx.Single      s1m = RxJavaInterop.toV1Single(MaybeSource);

rx.Completable c1m = RxJavaInterop.toV1Completable(MaybeSource);
```

### Convert between Subjects and Processors. 

Note that 3.x `Subject`s and `FlowableProcessor`s support only the same input and output types.

```java
// convert from 1.x to 3.x

io.reactivex.rxjava3.subjects.Subject sj3 = RxJavaInterop.toV3Subject(rx.subjects.Subject);

io.reactivex.rxjava3.processors.FlowableProcessor fp3 = RxJavaInterop.toV3Processor(rx.subjects.Subject);

// convert from 3.x to 1.x

rx.subjects.Subject sj1 = RxJavaInterop.toV1Subject(io.reactivex.rxjava3.subjects.Subject);

rx.subjects.Subject sj1b = RxJavaInterop.toV1Subject(io.reactivex.rxjava3.processors.FlowableProcessor);
```

### Convert between 1.x `X.Transformer`s and 3.x `XTransformer`s.

```java
// convert from 1.x to 3.x

io.reactivex.rxjava3.core.FlowableTransformer    ft3 = RxJavaInterop.toV3Transformer(
                                                           rx.Observable.Transformer);

io.reactivex.rxjava3.core.ObservableTransformer  ot3 = RxJavaInterop.toV3Transformer(
                                                           rx.Observable.Transformer, 
                                                           io.reactivex.rxjava3.core.BackpressureStrategy);

io.reactivex.rxjava3.core.SingleTransformer      st3 = RxJavaInterop.toV3Transformer(
                                                           rx.Single.Transformer);

io.reactivex.rxjava3.core.CompletableTransformer ct3 = RxJavaInterop.toV3Transformer(
                                                           rx.Completable.Transformer);

// convert from 3.x to 1.x

rx.Observable.Transformer  ft1 = RxJavaInterop.toV1Transformer(
                                     io.reactivex.rxjava3.core.FlowableTransformer);

rx.Observable.Transformer  ot1 = RxJavaInterop.toV1Transformer(
                                     io.reactivex.rxjava3.core.ObservableTransformer,
                                     io.reactivex.rxjava3.core.BackpressureStrategy);

rx.Single.Transformer      st1 = RxJavaInterop.toV1Transformer(
                                     io.reactivex.rxjava3.core.SingleTransformer);

rx.Completable.Transformer ct1 = RxJavaInterop.toV1Transformer(
                                     io.reactivex.rxjava3.core.CompletableTransformer);
```

### Convert between 1.x `Flowable.Operator` and 3.x `FlowableOperator`

```java
// convert from 1.x to 3.x

io.reactivex.rxjava3.core.FlowableOperator fo3 = RxJavaInterop.toV3Operator(rx.Observable.Operator);

// convert from 3.x to 1.x

rx.Observable.Operator fo1 = RxJavaInterop.toV1Operator(io.reactivex.rxjava3.core.FlowableOperator);
```

### Convert between 1.x `Subscription` and 3.x `Disposable`

```java
// convert from 1.x to 3.x

io.reactivex.rxjava3.disposables.Disposable d3 = RxJavaInterop.toV3Disposable(rx.Subscription);

// convert from 3.x to 1.x

rx.Subscription s1 = RxJavaInterop.toV1Subscription(io.reactivex.rxjava3.disposables.Disposable);
```


### Convert between 1.x `Scheduler`s and 3.x `Scheduler`s

```java
// convert from 1.x to 3.x

io.reactivex.rxjava3.core.Scheduler s3 = RxJavaInterop.toV3Scheduler(rx.Scheduler);

// convert from 3.x to 1.x

rx.Scheduler s1 = RxJavaInterop.toV1Scheduler(io.reactivex.rxjava3.core.Scheduler);
```
