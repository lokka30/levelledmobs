/*
 * This file is Copyright (c) 2020-2022 lokka30.
 * This file is/was present in the LevelledMobs resource.
 * Repository: <https://github.com/lokka30/LevelledMobs>
 * Use of this source code is governed by the GNU GPL v3.0
 * license that can be found in the LICENSE.md file.
 */

package me.lokka30.levelledmobs.file.internal;

import de.leonhard.storage.Json;
import org.jetbrains.annotations.NotNull;

public interface JsonInternalFile extends InternalFile {

    void load();

    @NotNull
    Json getData();

    @Override
    default String getName() {
        return getNameWithoutExtension() + ".json";
    }
}
