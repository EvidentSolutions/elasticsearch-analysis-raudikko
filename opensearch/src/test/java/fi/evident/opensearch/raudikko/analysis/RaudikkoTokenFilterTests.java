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

package fi.evident.opensearch.raudikko.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RaudikkoTokenFilterTests  {

    private final RaudikkoTokenFilterConfiguration configuration = new RaudikkoTokenFilterConfiguration();

    @Test
    public void testDefaultSettings() {
        assertTokens("Testaan voikon analyysiä tällä tavalla yksinkertaisesti.",
                token("Testaan", "testata", 1),
                token("voikon", "Voikko", 1),
                token("voikon", "voikko", 0),
                token("analyysiä", "analyysi", 1),
                token("tällä", "tämä", 1),
                token("tavalla", "tapa", 1),
                token("yksinkertaisesti", "yksinkertainen", 1));
    }

    @Test
    public void testDisableVariations() {
        configuration.analyzeAll = false;
        assertTokens("Testaan voikon analyysiä tällä tavalla yksinkertaisesti.",
                token("Testaan", "testata", 1),
                token("voikon", "Voikko", 1),
                token("analyysiä", "analyysi", 1),
                token("tällä", "tämä", 1),
                token("tavalla", "tapa", 1),
                token("yksinkertaisesti", "yksinkertainen", 1));
    }

    @Test
    public void testNonSeparatedTokens() {
        assertTokens("Testaan voikon analyysiä tällä tavalla yksinkertaisesti.",
                token("Testaan", "testata", 1),
                token("voikon", "Voikko", 1),
                token("voikon", "voikko", 0),
                token("analyysiä", "analyysi", 1),
                token("tällä", "tämä", 1),
                token("tavalla", "tapa", 1),
                token("yksinkertaisesti", "yksinkertainen", 1));
    }

    @Test
    public void testUnknownWord() {
        assertTokens("Mitenkä foobarbaz edellinen sana tunnistetaan?",
                token("Mitenkä", "miten", 1),
                token("foobarbaz", "foobarbaz", 1),
                token("edellinen", "edellinen", 1),
                token("sana", "sana", 1),
                token("tunnistetaan", "tunnistaa", 1));
    }

    @Test
    public void testCompoundWords() {
        assertTokens("isoisälle", token("isoisälle", "isoisä", 1));
        assertTokens("tekokuulla keinokuuhun",
                token("tekokuulla", "tekokuu", 1),
                token("keinokuuhun", "keinokuu", 1));
    }

    @Test
    public void testCompoundWordsWithHyphens() {
        assertTokens("rippi-isälle", token("rippi-isälle", "rippi-isä", 1));
    }

    @Test
    public void testCompoundWordsWithParts() {
        configuration.splitCompoundWords = true;
        assertTokens("lammasfarmeineen",
                token("lammasfarmeineen", "lammasfarmi", 1),
                token("lammasfarmeineen", "lammas", 0),
                token("lammasfarmeineen", "farmi", 0)
        );
        assertTokens("tämä on moniosainen lammasfarmi",
                token("tämä", "tämä", 1),
                token("on", "on", 1),
                token("moniosainen", "moniosainen", 1),
                token("moniosainen", "moni", 0),
                token("moniosainen", "osa", 0),
                token("lammasfarmi", "lammasfarmi", 1),
                token("lammasfarmi", "lammas", 0),
                token("lammasfarmi", "farmi", 0)
        );
    }

    private static TokenData token(String original, String token, int positionIncrement) {
        return new TokenData(original, token, positionIncrement);
    }

    private void assertTokens(String text, TokenData... expected) {
        List<TokenData> tokens = parse(text);
        assertEquals(asList(expected), tokens);
    }

    private List<TokenData> parse(String text) {
        Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer source = new FinnishTokenizer();
                TokenStream filter = RaudikkoTokenFilterFactory.createTokenFilter(source, configuration, new AnalysisCache(100), MorphologyFactory.getInstance());
                return new TokenStreamComponents(source, filter);
            }
        };

        try {
            try (TokenStream ts = analyzer.tokenStream("test", new StringReader(text))) {
                List<TokenData> result = new ArrayList<>();
                CharTermAttribute charTerm = ts.addAttribute(CharTermAttribute.class);
                OffsetAttribute offset = ts.addAttribute(OffsetAttribute.class);
                PositionIncrementAttribute position = ts.addAttribute(PositionIncrementAttribute.class);
                ts.reset();
                while (ts.incrementToken()) {
                    String original = text.substring(offset.startOffset(), offset.endOffset());
                    result.add(token(original, charTerm.toString(), position.getPositionIncrement()));
                }
                ts.end();

                return result;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class TokenData {

        private final String original;
        private final String token;
        private final int positionIncrement;

        TokenData(String original, String token, int positionIncrement) {
            this.original = original;
            this.token = token;
            this.positionIncrement = positionIncrement;
        }

        @Override
        public String toString() {
            return original + " -> " + token + " (+" + positionIncrement + ')';
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            TokenData tokenData = (TokenData) obj;

            return positionIncrement == tokenData.positionIncrement
                && original.equals(tokenData.original)
                && token.equals(tokenData.token);
        }

        @Override
        public int hashCode() {
            return 31 * (31 * original.hashCode() + token.hashCode()) + positionIncrement;
        }
    }
}
