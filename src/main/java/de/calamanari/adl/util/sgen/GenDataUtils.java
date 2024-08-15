//@formatter:off
/*
 * GenDataUtils
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import de.calamanari.adl.AdlException;

/**
 * A few utilities required to create meta data and random choices during the generation process.
 * <p/>
 * Any random-choices are semi-stable because the randomness does not depend on the time or order but solely on the expression's id and text generated until
 * this point.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class GenDataUtils {

    /**
     * Thread-local holder for message digest of type SHA-1, static, because it can be used by any generator (independent from the prefix), making generator
     * instances light-weight.
     */
    private static final ThreadLocal<MessageDigest> DIGEST_HOLDER = ThreadLocal.withInitial(() -> GenDataUtils.getSha1MessageDigestInstance("SHA-1"));

    static final MessageDigest getSha1MessageDigestInstance(String digestName) {
        try {
            return MessageDigest.getInstance(digestName);
        }
        catch (NoSuchAlgorithmException | RuntimeException ex) {
            throw new AdlException("Unexpected issue during SHA-1-MessageDigest initialization.", ex);
        }
    }

    /**
     * Creates a 64-bit hash from the given String
     * 
     * @param value string
     * @return hash-value 8 bytes
     */
    public static byte[] hash(String value) {

        if (value == null || value.isEmpty()) {
            return new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        }
        MessageDigest digest = DIGEST_HOLDER.get();
        digest.reset();

        digest.update(value.getBytes(StandardCharsets.UTF_8));

        return Arrays.copyOf(digest.digest(), 8);
    }

    /**
     * Creates a 63-bit hash from the given String
     * 
     * @param value string
     * @return hash-value (may be &lt;0) or 0 for empty or null string
     */
    public static long hashLong(String value) {

        byte[] bytes = hash(value);
        long res = 0;
        for (int i = 0; i < 8; i++) {
            res = (res << 8) | Byte.toUnsignedLong(bytes[i]);
        }
        return res >>> 1;
    }

    /**
     * Creates a new random instance based on a hash of the given value.<br/>
     * This avoids the problem of a global random instance with side effects and dependency on execution order when creating a random selection of values from a
     * list.
     * 
     * @param value some data to compute a seed for the random instance
     * @return random instance
     */
    public static Random createRandomWithSeed(String value) {
        return new Random(hashLong(value));
    }

    /**
     * Creates a new random instance based on a hash of the given {@link SampleExpression#expression()} text.<br/>
     * This avoids the problem of a global random instance with side effects and dependency on execution order when creating a random selection of values from a
     * list.
     * 
     * @param expression current input expression
     * @return random instance
     */
    public static Random createRandomWithSeed(SampleExpression inputExpression) {
        return createRandomWithSeed(inputExpression.id() + inputExpression.expression());
    }

    private GenDataUtils() {
        // utility methods
    }

    /**
     * removes the cached digest
     */
    public static void cleanup() {
        DIGEST_HOLDER.remove();
    }
}
