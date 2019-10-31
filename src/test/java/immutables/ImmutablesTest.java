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

package immutables;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Michele Rastelli
 */
public class ImmutablesTest {


    @Test
    public void immutablesTest() {
        MyClass myObj = ImmutableMyClass.builder()
                .name("yoyo")
                .addRoles("addr1")
                .addRoles("addr2")
                .build();

        assertThat(myObj.getName()).isEqualTo("yoyo");
        assertThat(myObj.getRoles()).containsExactly("addr1", "addr2");

        assertThatThrownBy(() -> myObj.getRoles().add("oo"))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasStackTraceContaining("UnmodifiableCollection");
    }

}
