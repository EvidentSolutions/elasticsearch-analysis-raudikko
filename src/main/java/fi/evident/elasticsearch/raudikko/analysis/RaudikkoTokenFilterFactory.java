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
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class RaudikkoTokenFilterFactory extends AbstractTokenFilterFactory {

    private final AnalysisCache analysisCache;

    private final RaudikkoTokenFilterConfiguration cfg = new RaudikkoTokenFilterConfiguration();

    private final Morphology morphology;

    public RaudikkoTokenFilterFactory(IndexSettings indexSettings,
                                    @SuppressWarnings("unused") Environment environment,
                                    String name,
                                    Settings settings) {
        super(indexSettings, name, settings);

        cfg.analyzeAll = settings.getAsBoolean("analyzeAll", cfg.analyzeAll);
        cfg.splitCompoundWords = settings.getAsBoolean("splitCompoundWords", cfg.splitCompoundWords);
        cfg.minimumWordSize = settings.getAsInt("minimumWordSize", cfg.minimumWordSize);
        cfg.maximumWordSize = settings.getAsInt("maximumWordSize", cfg.maximumWordSize);

        analysisCache = new AnalysisCache(settings.getAsInt("analysisCacheSize", 1024));

        // TODO support configurable morphology
        this.morphology = MorphologyFactory.getInstance();
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return createTokenFilter(tokenStream, cfg, analysisCache, morphology);
    }

    public static RaudikkoTokenFilter createTokenFilter(TokenStream tokenStream,
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
