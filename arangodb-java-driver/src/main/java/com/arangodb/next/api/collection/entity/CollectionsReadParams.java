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


import com.arangodb.next.entity.ApiEntity;

import java.util.Optional;

/**
 * @author Mark Vollmary
 * @author Michele Rastelli
 * @see <a href="https://www.arangodb.com/docs/stable/http/collection-getting.html#reads-all-collections">API
 * Documentation</a>
 */
@ApiEntity
public interface CollectionsReadParams {

    String EXCLUDE_SYSTEM_PARAM = "excludeSystem";

    static CollectionsReadParamsBuilder builder() {
        return new CollectionsReadParamsBuilder();
    }

    /**
     * @return whether or not system collections should be excluded from the result.
     */
    Optional<Boolean> getExcludeSystem();
}
