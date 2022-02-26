/*
 * This file is Copyright (c) 2020-2022 lokka30.
 * This file is/was present in the LevelledMobs resource.
 * Repository: <https://github.com/lokka30/LevelledMobs>
 * Use of this source code is governed by the GNU GPL v3.0
 * license that can be found in the LICENSE.md file.
 */

package me.lokka30.levelledmobs.file.internal.unlevellables;

import me.lokka30.levelledmobs.file.internal.JsonInternalFile;

import java.io.File;

public class UnlevellablesFile implements JsonInternalFile {

    @Override
    public void load(final boolean fromReload) {
        if (fromReload) {
            return;
        }
        //TODO Gson
    }

    @Override
    public String getNameWithoutExtension() {
        return "unlevellables";
    }

    @Override
    public String getRelativePath() {
        return "internal" + File.separator + getName();
    }
}
