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
    compile "com.github.akarnokd:rxjava2-interop:0.9.0"
}
```


Maven search:

[http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.akarnokd%22)

# Usage

```java
import static hu.akarnokd.rxjava.interop.RxJavaInterop;

// convert from 1.x to 2.x

io.reactivex.Flowable    f2 = RxJavaInterop.toV2Flowable(rx.Observable);

io.reactivex.Observable  o2 = RxJavaInterop.toV2Observabe(rx.Observable);

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

Converting between Subjects and Processors. Note that 2.x `Subject`s and `FlowableProcessor`s support only the same input and output types.

```java
import static hu.akarnokd.rxjava.interop.RxJavaInterop;

// convert from 1.x to 2.x

io.reactivex.subjects.Subject sj2 = RxJavaInterop.toV2Subject(rx.subjects.Subject);

// convert from 2.x to 1.x

rx.subjects.Subject sj1 = RxJavaInterop.toV1Subject(io.reactivex.subjects.Subject);

rx.subjects.Subject sj1b = RxJavaInterop.toV1Subject(io.reactivex.processors.FlowableProcessor);
```
