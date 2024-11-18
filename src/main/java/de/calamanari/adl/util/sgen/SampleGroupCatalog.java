//@formatter:off
/*
 * SampleGroupCatalog
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

import java.util.List;

/**
 * When we have many sample expressions organized in groups it makes sense to store them in multiple files, one per group.<br>
 * However, this complicates handling. Thus I decided to provide a catalog (file), so a single entry point will allow discovering all samples.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public record SampleGroupCatalog(List<SampleGroupCatalogEntry> groups, int numberOfGroups, int numberOfSamples) {

}
