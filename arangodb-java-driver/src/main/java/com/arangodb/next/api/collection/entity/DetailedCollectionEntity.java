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

package com.arangodb.next.api.collection.entity;

import com.arangodb.next.api.entity.ReplicationFactor;
import com.arangodb.next.entity.GeneratePackagePrivateBuilder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Michele Rastelli
 * @see <a href="https://www.arangodb.com/docs/stable/http/collection-creating.html">API Documentation</a>
 */
@GeneratePackagePrivateBuilder
@JsonDeserialize(builder = DetailedCollectionEntityBuilder.class)
public interface DetailedCollectionEntity extends CollectionEntity {

    /**
     * @note cluster only
     * @see CollectionCreateOptions#getReplicationFactor()
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ReplicationFactor getReplicationFactor();

    /**
     * @note cluster only
     * @see CollectionCreateOptions#getMinReplicationFactor()
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer getMinReplicationFactor();

    /**
     * @see CollectionCreateOptions#getKeyOptions()
     */
    KeyOptions getKeyOptions();

    /**
     * @see CollectionCreateOptions#getWaitForSync()
     */
    Boolean getWaitForSync();

    /**
     * @note cluster only
     * @see CollectionCreateOptions#getShardKeys()
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<String> getShardKeys();

    /**
     * @note cluster only
     * @see CollectionCreateOptions#getNumberOfShards()
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer getNumberOfShards();

    /**
     * @apiNote enterprise cluster only
     * @see CollectionCreateOptions#getDistributeShardsLike()
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String getDistributeShardsLike();

    /**
     * @note cluster only
     * @see CollectionCreateOptions#getShardingStrategy()
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ShardingStrategy getShardingStrategy();

    /**
     * @apiNote enterprise cluster only
     * @see CollectionCreateOptions#getSmartJoinAttribute()
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String getSmartJoinAttribute();

    /**
     * @see CollectionCreateOptions#getCacheEnabled()
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Boolean getCacheEnabled();

}
