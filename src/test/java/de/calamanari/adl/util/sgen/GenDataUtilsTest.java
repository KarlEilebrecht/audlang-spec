//@formatter:off
/*
 * GenDataUtilsTest
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

package de.calamanari.adl.util.sgen;

import java.util.Random;

import org.junit.jupiter.api.Test;

import de.calamanari.adl.AdlException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class GenDataUtilsTest {

    @Test
    void testHash() {

        assertEquals(0, GenDataUtils.hashLong(null));
        assertEquals(0, GenDataUtils.hashLong(""));
        assertEquals(6641795237113757060L, GenDataUtils.hashLong(" "));

    }

    @Test
    void testRandom() {

        Random rand = GenDataUtils.createRandomWithSeed("fooBar");

        assertEquals(1300, rand.nextInt(10_000));
        assertEquals(3939, rand.nextInt(10_000));
        assertEquals(727, rand.nextInt(10_000));
        assertEquals(1053, rand.nextInt(10_000));
        assertEquals(1921, rand.nextInt(10_000));

        Random rand2 = GenDataUtils.createRandomWithSeed("fooBar");

        assertEquals(1300, rand2.nextInt(10_000));
        assertEquals(3939, rand2.nextInt(10_000));
        assertEquals(727, rand2.nextInt(10_000));
        assertEquals(1053, rand2.nextInt(10_000));
        assertEquals(1921, rand2.nextInt(10_000));

        Random rand3 = GenDataUtils.createRandomWithSeed("fooBar2");

        assertEquals(3361, rand3.nextInt(10_000));
        assertEquals(2237, rand3.nextInt(10_000));
        assertEquals(4420, rand3.nextInt(10_000));
        assertEquals(9446, rand3.nextInt(10_000));
        assertEquals(5569, rand3.nextInt(10_000));

        GenDataUtils.cleanup();

    }

    @Test
    void testSpecialCases() {
        assertThrows(AdlException.class, () -> GenDataUtils.getSha1MessageDigestInstance("fooBar"));
    }

}
