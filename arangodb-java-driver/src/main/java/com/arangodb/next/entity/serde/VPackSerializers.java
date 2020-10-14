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

package com.arangodb.next.entity.serde;

import com.arangodb.next.api.entity.NumericReplicationFactor;
import com.arangodb.next.api.entity.ReplicationFactor;
import com.arangodb.next.api.entity.SatelliteReplicationFactor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author Michele Rastelli
 */
final class VPackSerializers {

    private VPackSerializers() {
    }

    static final JsonSerializer<ReplicationFactor> REPLICATION_FACTOR =
            new JsonSerializer<ReplicationFactor>() {
                @Override
                public void serialize(final ReplicationFactor value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
                    if (value instanceof NumericReplicationFactor) {
                        gen.writeNumber(((NumericReplicationFactor) value).getValue());
                    } else if (value instanceof SatelliteReplicationFactor) {
                        gen.writeString(((SatelliteReplicationFactor) value).getValue());
                    } else {
                        throw new IllegalArgumentException("Unknown class for replication factor: " + value.getClass().getName());
                    }
                }
            };
}
