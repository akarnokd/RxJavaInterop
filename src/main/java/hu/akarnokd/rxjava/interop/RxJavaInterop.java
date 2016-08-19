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
 * Hosts methods to convert between 1.x and 2.x types, composing backpressure
 * and cancellation through.
 */
public final class RxJavaInterop {
    /** Utility class. */
    private RxJavaInterop() {
        throw new IllegalStateException("No instances!");
    }

}
