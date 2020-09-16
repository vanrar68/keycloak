/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package org.keycloak.utils;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamsUtil {
    public static <T> Stream<T> closing(Stream<T> stream) {
        return Stream.of(stream).flatMap(Function.identity());
    }

    /**
     * Returns the original stream if the stream is not empty. Otherwise throws the provided exception.
     * @param stream Stream to be examined.
     * @param ex Exception to be thrown if the stream is empty.
     * @return Stream
     */
    public static <T> Stream<T> throwIfEmpty(Stream<T> stream, RuntimeException ex) {
        Iterator<T> iterator = stream.iterator();
        if (iterator.hasNext()) {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
        } else {
            throw ex;
        }
    }
}
