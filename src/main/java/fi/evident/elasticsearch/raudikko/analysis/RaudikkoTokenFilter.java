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

import fi.evident.raudikko.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

final class RaudikkoTokenFilter extends TokenFilter {

    private State current;
    private final Analyzer raudikkoAnalyzer;
    private final RaudikkoTokenFilterConfiguration cfg;

    private final CharTermAttribute charTermAttribute = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute positionIncrementAttribute = addAttribute(PositionIncrementAttribute.class);

    private final Deque<String> alternatives = new ArrayDeque<>();
    private final AnalysisCache analysisCache;

    private static final Pattern VALID_WORD_PATTERN = Pattern.compile("[a-zA-ZåäöÅÄÖ-]+");

    RaudikkoTokenFilter(TokenStream input,
                        Analyzer analyzer,
                        AnalysisCache analysisCache,
                        RaudikkoTokenFilterConfiguration cfg) {
        super(input);
        this.raudikkoAnalyzer = analyzer;
        this.analysisCache = analysisCache;
        this.cfg = cfg;
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!alternatives.isEmpty()) {
            outputAlternative(alternatives.removeFirst());
            return true;
        }

        if (input.incrementToken()) {
            analyzeToken();
            return true;
        }

        return false;
    }

    private void analyzeToken() {
        if (!isCandidateForAnalysis(charTermAttribute))
            return;

        List<String> baseForms = analyze(charTermAttribute);
        if (baseForms.isEmpty())
            return;

        charTermAttribute.setEmpty().append(baseForms.get(0));

        if (cfg.analyzeAll && baseForms.size() > 1) {
            current = captureState();

            alternatives.addAll(baseForms.subList(1, baseForms.size()));
        }
    }

    private List<String> analyze(CharSequence wordSeq) {
        String word = wordSeq.toString();
        return analysisCache.computeIfAbsent(word, this::analyzeUncached);
    }

    private List<String> analyzeUncached(String word) {
        List<String> results = raudikkoAnalyzer.baseForms(word);

        switch (results.size()) {
            case 0:
                return Collections.emptyList();
            case 1:
                return Collections.singletonList(results.get(0));
            default:
                return new ArrayList<>(results);
        }
    }

    private void outputAlternative(String token) {
        restoreState(current);

        positionIncrementAttribute.setPositionIncrement(0);
        charTermAttribute.setEmpty().append(token);
    }

    private boolean isCandidateForAnalysis(CharSequence word) {
        return word.length() >= cfg.minimumWordSize && word.length() <= cfg.maximumWordSize && VALID_WORD_PATTERN.matcher(word).matches();
    }
}
