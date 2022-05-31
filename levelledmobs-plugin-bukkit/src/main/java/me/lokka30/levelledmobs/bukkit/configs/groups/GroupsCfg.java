package me.lokka30.levelledmobs.bukkit.configs.groups;

import me.lokka30.levelledmobs.bukkit.configs.Config;
import me.lokka30.levelledmobs.bukkit.utils.Log;
import org.spongepowered.configurate.ConfigurateException;

public final class GroupsCfg extends Config {

    /* vars */

    /*
    The minimum file version which the updater is willing to migrate from.
     */
    private static final int UPDATER_CUTOFF_FILE_VERSION = 1;


    /* constructors */

    public GroupsCfg() {
        super("groups.yml", 1);
    }

    /* methods */

    @Override
    protected boolean updateLogic(int fromVersion) {
        if(fromVersion < UPDATER_CUTOFF_FILE_VERSION) {
            Log.sev("Configuration '" + getFileName() + "' is too old to be migrated: it " +
                "is version '" + fromVersion + "', but the 'cutoff' version is '" +
                UPDATER_CUTOFF_FILE_VERSION + "'.");
            return false;
        }

        var currentFileVersion = fromVersion;
        while(currentFileVersion < getLatestFileVersion()) {
            Log.inf("Updating configuration '" + getFileName() + "' from file version '" +
                currentFileVersion + "' to '" + (currentFileVersion + 1) + "'.");

            switch(currentFileVersion) {
                case 12345 -> {
                    currentFileVersion++;
                    try {
                        getRoot().node("metadata", "version", "current").set(currentFileVersion);
                        getLoader().save(getRoot());
                    } catch(ConfigurateException ex) {
                        Log.sev("Update failed: unable to write updates to file. Stack trace:");
                        ex.printStackTrace();
                        return false;
                    }
                }
                default -> {
                    Log.sev("Attempted to update from file version '" + currentFileVersion +
                        "' of configuration '" + getFileName() + "', but no updater logic is " +
                        "present for that file version. Please inform LevelledMobs maintainers, " +
                        "as this should be impossible.");
                    return false;
                }
            }
        }
        return true;
    }
}
