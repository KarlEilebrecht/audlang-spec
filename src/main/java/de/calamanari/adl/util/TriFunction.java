//@formatter:off
/*
 * TriFunction
 * Copyright 2024 Karl Eilebrecht
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"):
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//@formatter:on

package de.calamanari.adl.util;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Functional interface accepting tree input arguments.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <S> the type of the third argument to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface TriFunction<T, U, S, R> {

    /**
     * Computes result based on three inputs
     * 
     * @param arg1 first argument to the function
     * @param arg2 second argument to the function
     * @param arg3 the third argument to the function
     * @return result
     */
    R apply(T arg1, U arg2, S arg3);

    /**
     * To keep the implementation consistent with {@link BiFunction} this method allows chaining {@link TriFunction}s in the same way.
     *
     * @param <V> output type of the {@code after} function, and of the composed function
     * @param after function to be applied after this function
     * @return composed function to first apply this function and then the {@code after} function
     * @throws NullPointerException if after is null
     */
    default <V> TriFunction<T, U, S, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t, U u, S s) -> after.apply(apply(t, u, s));
    }
}
