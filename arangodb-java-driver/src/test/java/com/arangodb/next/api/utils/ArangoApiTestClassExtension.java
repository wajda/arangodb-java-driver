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

import com.arangodb.next.api.database.DatabaseApiSync;
import com.arangodb.next.api.reactive.impl.ArangoDBImpl;
import com.arangodb.next.api.sync.ArangoDBSync;
import com.arangodb.next.communication.ArangoTopology;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Michele Rastelli
 */
public class ArangoApiTestClassExtension implements BeforeAllCallback, AfterAllCallback {

    private final static List<TestContext> contexts = TestContextProvider.INSTANCE.get();

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        String dbName = context.getRequiredTestClass().getSimpleName();
        doForeachTopology(dbApi -> dbApi.dropDatabase(dbName));
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        String dbName = context.getRequiredTestClass().getSimpleName();
        doForeachTopology(dbApi -> dbApi.createDatabase(dbName));
    }

    private void doForeachTopology(Consumer<DatabaseApiSync> action) {
        Map<ArangoTopology, List<TestContext>> contextsByTopology = contexts.stream()
                .collect(Collectors.groupingBy(it -> it.getConfig().getTopology()));
        contextsByTopology.values().forEach(ctxList -> {
            ArangoDBSync testClient = new ArangoDBImpl(ctxList.get(0).getConfig()).sync();
            action.accept(testClient.db().databaseApi());
        });
    }
}
