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

import org.apache.lucene.analysis.util.CharTokenizer;

/**
 * A tokenizer that tries to break text into tokens based on rules of Finnish language.
 * This is almost same as {@link org.apache.lucene.analysis.core.LetterTokenizer}, but
 * treats hyphen as a letter as well, since hyphens can be part of words. (e.g. "rippi-isä").
 */
class FinnishTokenizer extends CharTokenizer {

    @Override
    protected boolean isTokenChar(int c) {
        return Character.isLetterOrDigit(c) || c == '-';
    }
}
