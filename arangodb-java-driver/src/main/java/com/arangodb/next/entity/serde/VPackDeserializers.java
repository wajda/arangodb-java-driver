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

import com.arangodb.next.api.collection.entity.CollectionStatus;
import com.arangodb.next.api.collection.entity.CollectionType;
import com.arangodb.next.api.collection.entity.KeyType;
import com.arangodb.next.api.collection.entity.ShardingStrategy;
import com.arangodb.next.api.database.entity.Sharding;
import com.arangodb.next.api.entity.ReplicationFactor;
import com.arangodb.next.api.entity.SatelliteReplicationFactor;
import com.arangodb.velocypack.VPackDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;


/**
 * @author Michele Rastelli
 */
public final class VPackDeserializers {

    private VPackDeserializers() {
    }

    static final JsonDeserializer<ReplicationFactor> REPLICATION_FACTOR = new JsonDeserializer<ReplicationFactor>() {
        @Override
        public ReplicationFactor deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (JsonToken.VALUE_NUMBER_INT.equals(p.getCurrentToken())) {
                return ReplicationFactor.of(p.getValueAsInt());
            } else if (JsonToken.VALUE_STRING.equals(p.getCurrentToken())
                    && SatelliteReplicationFactor.VALUE.equals(p.getValueAsString())) {
                return ReplicationFactor.ofSatellite();
            } else {
                throw new IllegalArgumentException("Unknown value for replication factor!");
            }
        }
    };

    // TODO

    //region DatabaseApi
    public static final VPackDeserializer<Sharding> SHARDING = (parent, vpack, context) ->
            Sharding.of(vpack.getAsString());
    //endregion

    //region CollectionApi
    public static final VPackDeserializer<CollectionType> COLLECTION_TYPE = (parent, vpack, context) ->
            CollectionType.of(vpack.getAsInt());

    public static final VPackDeserializer<CollectionStatus> COLLECTION_STATUS = (parent, vpack, context) ->
            CollectionStatus.of(vpack.getAsInt());

    public static final VPackDeserializer<ShardingStrategy> SHARDING_STRATEGY = (parent, vpack, context) ->
            ShardingStrategy.of(vpack.getAsString());

    public static final VPackDeserializer<KeyType> KEY_TYPE = (parent, vpack, context) ->
            KeyType.of(vpack.getAsString());
    //endregion

}
