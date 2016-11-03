# RxJava2Interop
Library to convert between RxJava 1.x and 2.x reactive types.

# Releases

<a href='https://travis-ci.org/akarnokd/RxJava2Interop/builds'><img src='https://travis-ci.org/akarnokd/RxJava2Interop.svg?branch=master'></a>
[![codecov.io](http://codecov.io/github/akarnokd/RxJava2Interop/coverage.svg?branch=master)](http://codecov.io/github/akarnokd/RxJava2Interop?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.akarnokd/rxjava2-interop/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.akarnokd/rxjava2-interop)


**gradle**

```
dependencies {
    compile "com.github.akarnokd:rxjava2-interop:0.6.1"
}
```


Maven search:

[http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.akarnokd%22)

# Usage

```java
import static hu.akarnokd.rxjava.interop.RxJavaInterop.*;

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
