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

package com.arangodb.next.api.utils;

import com.arangodb.next.api.collection.CollectionApi;
import com.arangodb.next.api.reactive.ArangoDB;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * @author Michele Rastelli
 */
public class ArangoApiParameterResolver implements ParameterResolver {

    private final TestContext testContext;
    private final ArangoDB testClient;

    public ArangoApiParameterResolver(TestContext testContext, ArangoDB testClient) {
        this.testContext = testContext;
        this.testClient = testClient;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return resolve(parameterContext.getParameter().getType()) != null;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Object o = resolve(parameterContext.getParameter().getType());
        if (o != null) {
            return o;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + parameterContext.getParameter().getType());
        }
    }

    private Object resolve(Class<?> clazz) {
        if (clazz == TestContext.class) {
            return testContext;
        } else if (clazz == CollectionApi.class) {
            return testClient.db().collectionApi();
        } else {
            return null;
        }
    }
}
