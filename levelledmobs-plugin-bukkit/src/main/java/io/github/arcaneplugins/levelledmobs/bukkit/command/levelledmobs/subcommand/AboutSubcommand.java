package io.github.arcaneplugins.levelledmobs.bukkit.command.levelledmobs.subcommand;

import dev.jorel.commandapi.CommandAPICommand;
import io.github.arcaneplugins.levelledmobs.bukkit.LevelledMobs;
import io.github.arcaneplugins.levelledmobs.bukkit.config.translations.Message;
import org.bukkit.plugin.PluginDescriptionFile;

public final class AboutSubcommand {

    public static CommandAPICommand createInstance() {
        return new CommandAPICommand("about")
            .withPermission("levelledmobs.command.levelledmobs.about")
            .withShortDescription("View info about the installed version of the plugin.")
            .withFullDescription("View info about the installed version of the plugin.")
            .executes((sender, args) -> {
                final PluginDescriptionFile pdf = LevelledMobs.getInstance().getDescription();

                Message.COMMAND_LEVELLEDMOBS_MAIN.sendTo(sender,
                    "%version%", pdf.getVersion(),
                    "%maintainers%", Message.joinDelimited(pdf.getAuthors())
                );
            });
    }

}
