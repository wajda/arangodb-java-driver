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

package com.arangodb.codegen;


import api.TestApi;
import api.TestClientSync;
import api.TestClientSyncImpl;
import com.sun.tools.javac.Main;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michele Rastelli
 */
class GenerateSyncApiProcessorTest {
    private final static String GENERATED_DIR = "generated";
    private final static String SOURCE_DIR = GENERATED_DIR + "/source";
    private final static String COMPILED_DIR = GENERATED_DIR + "/compiled";

    @Test
    void loadGeneratedClasses() throws Exception {
        ClassLoader cl = new URLClassLoader(new URL[]{new File(COMPILED_DIR).toURI().toURL()});

        String testApiSyncClassName = TestApi.class.getCanonicalName() + "Sync";
        String testApiSyncImplClassName = testApiSyncClassName + "Impl";

        Class<?> testApiSyncXClass = cl.loadClass(testApiSyncClassName);
        Class<?> testApiSyncXImplClass = cl.loadClass(testApiSyncImplClassName);

        assertThat(testApiSyncXClass.getCanonicalName()).isEqualTo(testApiSyncClassName);
        assertThat(testApiSyncXImplClass.getCanonicalName()).isEqualTo(testApiSyncImplClassName);
    }

    @BeforeAll
    static void generateAndCompile() throws IOException {
        cleanup();

        // generate sources
        Main.compile(Stream
                .concat(
                        Stream.of("-d", SOURCE_DIR, "-proc:only",
                                "-processor", GenerateSyncApiProcessor.class.getCanonicalName()),
                        Stream.of(TestApi.class, TestClientSync.class, TestClientSyncImpl.class)
                                .map(i -> "src/test/java/" + i.getCanonicalName().replace(".", "/") + ".java")
                )
                .toArray(String[]::new)
        );

        // compile sources
        Main.compile(Stream
                .concat(
                        Stream.of("-d", COMPILED_DIR),
                        Files.walk(Paths.get(SOURCE_DIR))
                                .filter(i -> i.getFileName().toString().endsWith(".java"))
                                .map(Path::toString)
                )
                .toArray(String[]::new)
        );
    }

    @AfterAll
    static void cleanup() throws IOException {
        // clean generated directory
        FileUtils.deleteDirectory(new File(GENERATED_DIR));
    }

}
