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

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.elasticsearch.SpecialPermission;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.function.Function;

final class AnalysisCache {

    private final Cache<String, List<String>> cache;

    AnalysisCache(int cacheSize) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null)
            sm.checkPermission(new SpecialPermission());

        cache = AccessController.doPrivileged((PrivilegedAction<Cache<String, List<String>>>) () -> new Cache2kBuilder<String, List<String>>() {}
                .eternal(true)
                .entryCapacity(cacheSize)
                .build());
    }

    List<String> computeIfAbsent(String word, Function<String, List<String>> func) {
        return cache.computeIfAbsent(word, () -> func.apply(word));
    }
}
