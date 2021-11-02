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

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

final class AnalysisCache {

    private final Cache<String, List<String>> cache;
    private final ReentrantLock lock = new ReentrantLock(true);

    AnalysisCache(int cacheSize) {
        cache = new Cache2kBuilder<String, List<String>>() {}
                .eternal(true)
                .entryCapacity(cacheSize)
                .build();
    }

    List<String> get(String word) {
        // Note that it seems that we could use a read/write -lock here and grab only the read-lock
        // when retrieving stuff from cache, but this will not work because the cache uses access-order,
        // meaning that every read will actually mutate the cache.
        lock.lock();
        try {
            return cache.get(word);
        } finally {
            lock.unlock();
        }
    }

    void put(String word, List<String> result) {
        lock.lock();
        try {
            cache.put(word, result);
        } finally {
            lock.unlock();
        }
    }
}
