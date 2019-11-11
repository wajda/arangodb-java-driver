/*
 * DISCLAIMER
 *
 * Copyright 2016 ArangoDB GmbH, Cologne, Germany
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
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 */

package com.arangodb.next.connection.vst;


import com.arangodb.next.connection.ArangoRequest;
import com.arangodb.next.connection.ArangoResponse;
import com.arangodb.velocypack.VPackBuilder;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.ValueType;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michele Rastelli
 */
class InternalSerdeTest {

    @Test
    void deserializeArangoResponse() {
        ArangoResponse response = ArangoResponse.builder()
                .version(1)
                .type(2)
                .responseCode(200)
                .putMeta("metaKey", "metaValue")
                .body(Unpooled.EMPTY_BUFFER)
                .build();

        final VPackBuilder builder = new VPackBuilder();
        builder.add(ValueType.ARRAY);
        builder.add(response.getVersion());
        builder.add(response.getType());
        builder.add(response.getResponseCode());
        builder.add(ValueType.OBJECT);
        response.getMeta().forEach(builder::add);
        builder.close();
        builder.close();

        final VPackSlice vpack = builder.slice();
        Map.Entry<String, VPackSlice> metaEntry = vpack.get(3).objectIterator().next();
        ArangoResponse deserializedResponse = ArangoResponse.builder()
                .body(Unpooled.EMPTY_BUFFER)
                .version(vpack.get(0).getAsInt())
                .type(vpack.get(1).getAsInt())
                .responseCode(vpack.get(2).getAsInt())
                .putMeta(metaEntry.getKey(), metaEntry.getValue().getAsString())
                .build();

        assertThat(deserializedResponse).isEqualTo(response);
    }

    @Test
    void serializeArangoRequest() {
        ArangoRequest request = ArangoRequest.builder()
                .database("database")
                .requestType(RequestType.GET)
                .path("path")
                .body(Unpooled.EMPTY_BUFFER)
                .putHeaderParam("headerParamKey", "headerParamValue")
                .putQueryParam("queryParamKey", "queryParamValue")
                .build();

        final VPackBuilder builder = new VPackBuilder();
        builder.add(ValueType.ARRAY);
        builder.add(request.getVersion());
        builder.add(request.getType());
        builder.add(request.getDatabase());
        builder.add(request.getRequestType().getType());
        builder.add(request.getPath());
        builder.add(ValueType.OBJECT);
        request.getQueryParam().forEach(builder::add);
        builder.close();
        builder.add(ValueType.OBJECT);
        request.getHeaderParam().forEach(builder::add);
        builder.close();
        builder.close();

        final VPackSlice slice = builder.slice();
        Iterator<VPackSlice> iterator = slice.arrayIterator();
        assertThat(iterator.next().getAsInt()).isEqualTo(request.getVersion());
        assertThat(iterator.next().getAsInt()).isEqualTo(request.getType());
        assertThat(iterator.next().getAsString()).isEqualTo(request.getDatabase());
        assertThat(iterator.next().getAsInt()).isEqualTo(request.getRequestType().getType());
        assertThat(iterator.next().getAsString()).isEqualTo(request.getPath());

        Map.Entry<String, VPackSlice> firstQueryParamSliceEntry = iterator.next().objectIterator().next();
        Map.Entry<String, String> firstQueryParamEntry = request.getQueryParam().entrySet().iterator().next();
        assertThat(firstQueryParamSliceEntry.getKey()).isEqualTo(firstQueryParamEntry.getKey());
        assertThat(firstQueryParamSliceEntry.getValue().getAsString()).isEqualTo(firstQueryParamEntry.getValue());

        Map.Entry<String, VPackSlice> firstHeaderParamSliceEntry = iterator.next().objectIterator().next();
        Map.Entry<String, String> firstHeaderParamEntry = request.getHeaderParam().entrySet().iterator().next();
        assertThat(firstHeaderParamSliceEntry.getKey()).isEqualTo(firstHeaderParamEntry.getKey());
        assertThat(firstHeaderParamSliceEntry.getValue().getAsString()).isEqualTo(firstHeaderParamEntry.getValue());
    }

}