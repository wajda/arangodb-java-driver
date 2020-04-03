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


/**
 * @author Michele Rastelli
 */
public enum EnforceReplicationFactor {

    /**
     * disable this extra check
     */
    FALSE(0),

    /**
     * the server will check if there are enough replicas available at creation time and bail out otherwise
     * (default)
     */
    TRUE(1);

    private final int value;

    EnforceReplicationFactor(int waitForSyncReplicationValue) {
        value = waitForSyncReplicationValue;
    }

    public int getValue() {
        return value;
    }

    public static EnforceReplicationFactor of(int value) {
        for (EnforceReplicationFactor keyType : EnforceReplicationFactor.values()) {
            if (keyType.value == value) {
                return keyType;
            }
        }
        throw new IllegalArgumentException("Unknown WaitForSyncReplication value: " + value);
    }

}
