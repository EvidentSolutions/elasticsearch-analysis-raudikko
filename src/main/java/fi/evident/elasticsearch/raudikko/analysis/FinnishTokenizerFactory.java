/*
 * Copyright (C) 2021  Evident Solutions Oy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fi.evident.elasticsearch.raudikko.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;

/**
 * Factory that creates {@link FinnishTokenizer}.
 */
public class FinnishTokenizerFactory extends AbstractTokenizerFactory {

    public FinnishTokenizerFactory(IndexSettings indexSettings,
                                   @SuppressWarnings("unused") Environment environment,
                                   @SuppressWarnings("unused") String name, // TODO do we need name somewhere?
                                   Settings settings) {
        super(indexSettings, settings);
    }

    @Override
    public Tokenizer create() {
        return new FinnishTokenizer();
    }
}
