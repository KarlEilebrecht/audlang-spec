//@formatter:off
/*
 * TemplateParseException
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

import de.calamanari.adl.util.AdlException;

/**
 * Special exception to be thrown when parsing an expression template
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class TemplateParseException extends AdlException {

    private static final long serialVersionUID = 1028357705364693291L;

    /**
     * @param message
     */
    public TemplateParseException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public TemplateParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
