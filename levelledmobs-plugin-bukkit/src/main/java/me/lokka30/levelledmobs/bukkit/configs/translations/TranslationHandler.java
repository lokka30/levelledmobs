package me.lokka30.levelledmobs.bukkit.configs.translations;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;
import me.lokka30.levelledmobs.bukkit.LevelledMobs;
import me.lokka30.levelledmobs.bukkit.utils.Log;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

@SuppressWarnings({"FieldCanBeLocal"})
public final class TranslationHandler {

    /* vars */

    private final int latestFileVersion = 1;

    private final int updaterCutoffFileVersion = 1;

    @SuppressWarnings("FieldMayBeFinal")
    private String lang = InbuiltLang.EN_US.toString();

    private YamlConfigurationLoader loader = null;

    private CommentedConfigurationNode root = null;

    /* methods */

    public boolean load() {
        /*
        [pseudocode]
        # this code loads translation specified by the user

        set lang = the user's configured language
        set langToPathFun = function of <String input, Path output>, where input is 'langInput', and output is: "%dataFolder%/translations/%langInput%.yml"

        loop while file at path %langToPathFun.apply(lang)% does not exist
            if internal translation available for lang
                save internal translation resource to data folder
                break loop
            else
                # avoiding a possible stack overflow in this loop if the file doesnt save for some reason
                assert lang != en_us

                # the user specified a language without providing a translation for it
                log [severe] language %lang% specified, but no translation file found. defaulting to en_us

                # let's just default to en_us
                set lang = en_us

                # we continue the loop so that it can generate the file again but this time for en_us
                continue loop

        load translation config from file
         */
        Log.inf("Loading translations.");

        var lang = LevelledMobs.getInstance()
            .getConfigHandler()
            .getSettingsCfg()
            .getRoot()
            .node("lang")
            .getString(InbuiltLang.EN_US.toString());

        final Function<String, Path> langToPathFun = (langInput) -> Path.of(
            LevelledMobs.getInstance().getDataFolder() + File.separator + "translations" +
                File.separator + langInput + ".yml"
        );

        while(!langToPathFun.apply(lang).toFile().exists()) {
            final @Nullable var inbuilt = InbuiltLang.of(lang);

            if(inbuilt == null) {
                // avoiding a possible stack overflow in this loop if the file doesnt save for some reason
                if(!lang.equalsIgnoreCase(InbuiltLang.EN_US.toString())) {
                    return false;
                }

                // the user specified a language without providing a translation for it
                Log.sev("Lang '" + lang + "' is not an inbuilt lang, and no custom translation"
                    + " file was found for it. Falling back to 'en_US' until you fix it.");

                // let's just default to en_us
                lang = InbuiltLang.EN_US.toString();

                // we continue the loop so that it can generate the file again but this time for en_us
                // noinspection UnnecessaryContinue
                continue;
            } else {
                // make sure the lang is using the correct format, ab_CD.
                // this is done by just grabbing it straight from the InbuiltLang constant.
                lang = inbuilt.toString();

                Log.inf("Saving default translation for lang '" + lang + "'.");

                LevelledMobs.getInstance().saveResource(
                    "translations" + File.separator + lang + ".yml", false);

                break;
            }
        }

        // note: should not need to check if loader is already set, as the file name is dynamic.
        // this is unlike other config files (see: Config#load())

        loader = YamlConfigurationLoader.builder().path(Path.of(
            LevelledMobs.getInstance().getDataFolder().getAbsolutePath() +
                File.separator + "translations" + File.separator + getLang() + ".yml"
        )).build();

        try {
            root = getLoader().load();
        } catch(ConfigurateException ex) {
            Log.sev(
                "Unable to load translation '" + getLang() + "'. This is usually a " +
                    "user-caused error caused from YAML syntax errors inside the file, such as an " +
                    "incorrect indent or stray symbol. We recommend that you use a YAML parser " +
                    "website - such as the one linked here - to help locate where these errors " +
                    "are appearing. --> https://www.yaml-online-parser.appspot.com/ <-- A stack " +
                    "trace will be printed below for debugging purposes."
            );
            ex.printStackTrace();
            return false;
        }

        return update();
    }

    private boolean update() {
        var currentFileVersion = getCurrentFileVersion();

        if(currentFileVersion == 0) {
            Log.sev("Unable to detect file version of translation '" + getLang() + "'. "
                + "Was it modified by the user?");
            return false;
        }

        if(currentFileVersion > getLatestFileVersion()) {
            Log.war("Translation '" + getLang() + "' is somehow newer than the latest compatible "
                + "file version. How did we get here?");
            return true;
        }

        if(currentFileVersion < getUpdaterCutoffFileVersion()) {
            final var heading = "Translation '" + getLang() + "' is too old for LevelledMobs to "
                + "update. ";

            // make a different recommendation based upon whether the translation is inbuilt
            if(InbuiltLang.of(getLang()) == null) {
                // not an inbuilt translation
                Log.sev(heading + "As this seems to be a 'custom' translation, we recommend " +
                    "you to store a backup of the translation file in a separate location, then " +
                    "remove the file from the 'plugins/LevelledMobs/translations' directory, and " +
                    "switch to an inbuilt translation such as 'en_US' for the time being. Then, " +
                    "customize the new translation file as you wish.");
            } else {
                // an inbuilt translation
                Log.sev(heading + "As this translation is an 'inbuilt' translation, you can " +
                    "simply remove the file and allow LevelledMobs to generate the latest one " +
                    "for you automatically. If you have made any edits to this translation file, " +
                    "remember to back it up and transfer the edits to the newly generated file.");
            }
            return false;
        }

        //noinspection ConstantConditions
        while(currentFileVersion < getLatestFileVersion()) {
            Log.inf("Upgrading translation '" + getLang() + "' from file version '" +
                currentFileVersion + "' to '" + (currentFileVersion + 1) + "'.");

            switch(currentFileVersion) {
                case Integer.MIN_VALUE -> {
                    // Example migration code
                    currentFileVersion++;
                    try {
                        getRoot().node("metadata", "version", "current").set(currentFileVersion);
                        getLoader().save(getRoot());
                    } catch(ConfigurateException ex) {
                        Log.sev("Unable to write updates to file of lang '" + getLang() + "'.");
                        return false;
                    }
                }
                default -> {
                    Log.sev("Attempted to update from file version '" + currentFileVersion +
                        "' of translation '" + getLang() + "', but no updater logic is present " +
                        "for that file version. Please inform LevelledMobs maintainers.");
                    return false;
                }
            }
        }

        return true;
    }

    public int getCurrentFileVersion() {
        return getRoot().node("metadata", "version", "current").getInt(0);
    }

    /* var getters and setters */

    public int getLatestFileVersion() { return latestFileVersion; }

    public int getUpdaterCutoffFileVersion() { return updaterCutoffFileVersion; }

    public String getLang() { return lang; }

    public YamlConfigurationLoader getLoader() { return loader; }

    public CommentedConfigurationNode getRoot() { return root; }

}
