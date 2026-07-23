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

import fi.evident.raudikko.AnalyzerConfiguration;
import fi.evident.raudikko.Morphology;
import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.plugin.Inject;
import org.elasticsearch.plugin.NamedComponent;
import org.elasticsearch.plugin.analysis.TokenFilterFactory;

@NamedComponent("raudikko")
public class RaudikkoTokenFilterFactory implements TokenFilterFactory {

    private final AnalysisCache analysisCache;

    private final RaudikkoTokenFilterConfiguration cfg = new RaudikkoTokenFilterConfiguration();

    private final Morphology morphology;

    @Inject
    public RaudikkoTokenFilterFactory(RaudikkoTokenFilterSettings settings) {

        cfg.analyzeAll = settings.analyzeAll();
        cfg.splitCompoundWords = settings.splitCompoundWords();
        cfg.minimumWordSize = settings.minimumWordSize();
        cfg.maximumWordSize = settings.maximumWordSize();

        analysisCache = new AnalysisCache(settings.analysisCacheSize());

        // TODO support configurable morphology
        this.morphology = MorphologyFactory.getInstance();
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return createTokenFilter(tokenStream, cfg, analysisCache, morphology);
    }

    static RaudikkoTokenFilter createTokenFilter(TokenStream tokenStream,
                                                 RaudikkoTokenFilterConfiguration filterCfg,
                                                 AnalysisCache analysisCache,
                                                 Morphology morphology) {
        AnalyzerConfiguration analyzerCfg = new AnalyzerConfiguration();
        analyzerCfg.setIncludeStructure(false);
        analyzerCfg.setIncludeBasicAttributes(false);
        analyzerCfg.setIncludeFstOutput(false);
        analyzerCfg.setIncludeOrganizationNameAnalysis(false);
        analyzerCfg.setIncludeBaseForm(true);
        analyzerCfg.setIncludeBaseFormParts(filterCfg.splitCompoundWords);

        return new RaudikkoTokenFilter(tokenStream, morphology.newAnalyzer(analyzerCfg), analysisCache, filterCfg);
    }
}
