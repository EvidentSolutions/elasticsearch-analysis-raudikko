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

import fi.evident.raudikko.Morphology;

import java.lang.ref.SoftReference;

public final class MorphologyFactory {
    private static SoftReference<Morphology> reference;

    public static synchronized Morphology getInstance() {
        Morphology instance = reference != null ? reference.get() : null;
        if (instance == null) {
            Morphology morphology = Morphology.loadBundled();
            reference = new SoftReference<>(morphology);
            instance = morphology;
        }

        return instance;
    }
}
