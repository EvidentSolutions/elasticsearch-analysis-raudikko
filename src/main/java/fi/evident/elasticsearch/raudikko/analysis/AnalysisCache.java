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

import org.elasticsearch.common.cache.Cache;
import org.elasticsearch.common.cache.CacheBuilder;

import java.util.List;

final class AnalysisCache {

    // "Secondly, there's a is an LRU cache implementation in core Elasticsearch (org.elasticsearch.common.cache.Cache); 
    // you should just use that and avoid this problem altogether."
    // https://discuss.elastic.co/t/customize-java-security-manager-settings-provide-ability-to-turn-it-off/72303/5
    private final Cache<String, List<String>> cache;

    AnalysisCache(int cacheSize) {
        CacheBuilder<String, List<String>> builder = CacheBuilder.builder();
        builder.weigher((a, b) -> 1); // simple weigher in order to retain compatibility with cacheSize parameter, for now 
        builder.setMaximumWeight(cacheSize);
        this.cache = builder.build();
    }

    List<String> get(String word) {
        return cache.get(word);
    }

    void put(String word, List<String> result) {
        cache.put(word, result);
    }
}
