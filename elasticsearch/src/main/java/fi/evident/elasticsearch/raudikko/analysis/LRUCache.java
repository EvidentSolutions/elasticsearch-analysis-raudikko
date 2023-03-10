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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A bounded cache that evicts least recently used items when it becomes full.
 */
final class LRUCache<K,V> extends LinkedHashMap<K,V> {

    private final int maxSize;
    private static final float LOAD_FACTOR = 0.75F;

    LRUCache(int maxSize) {
        super(maxSize + 1, LOAD_FACTOR, true);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > maxSize;
    }
}
