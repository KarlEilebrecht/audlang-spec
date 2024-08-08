//@formatter:off
/*
 * JsonUtilsTest
 * Copyright 2024 Karl Eilebrecht
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"):
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//@formatter:on

package de.calamanari.adl.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.calamanari.adl.util.sgen.SampleExpression;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class JsonUtilsTest {

    @Test
    void testSpecialCases() {

        assertNotNull(JsonUtils.createObjectMapper());

        assertThrows(IllegalArgumentException.class, () -> JsonUtils.writeAsJsonString(null, false));

        List<List<?>> list = new ArrayList<>();
        list.add(list);

        assertThrows(AdlException.class, () -> JsonUtils.writeAsJsonString(list, false));

        assertThrows(AdlException.class, () -> JsonUtils.readFromJsonString("{}", SampleExpression.class));
        assertThrows(AdlException.class, () -> JsonUtils.readFromJsonString(null, SampleExpression.class));

        RuntimeException ex = new RuntimeException();

        assertEquals(ex, new AdlException(ex).getCause());

        AdlException ex2 = new AdlException("bla", ex, false, true);
        assertEquals("bla", ex2.getMessage());
        assertEquals(ex, ex2.getCause());

    }

}
