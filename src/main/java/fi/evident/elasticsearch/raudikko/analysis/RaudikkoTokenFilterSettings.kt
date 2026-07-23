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
package fi.evident.elasticsearch.raudikko.analysis

import org.elasticsearch.plugin.settings.AnalysisSettings
import org.elasticsearch.plugin.settings.BooleanSetting
import org.elasticsearch.plugin.settings.IntSetting

@AnalysisSettings
interface RaudikkoTokenFilterSettings {
    @BooleanSetting(path = "analyzeAll", defaultValue = true)
    fun analyzeAll(): Boolean

    @BooleanSetting(path = "splitCompoundWords", defaultValue = false)
    fun splitCompoundWords(): Boolean

    @IntSetting(path = "minimumWordSize", defaultValue = 3)
    fun minimumWordSize(): Int

    @IntSetting(path = "maximumWordSize", defaultValue = 100)
    fun maximumWordSize(): Int

    @IntSetting(path = "analysisCacheSize", defaultValue = 1024)
    fun analysisCacheSize(): Int
}
