/*
 * Copyright (c) 2020-2021  lokka30. Use of this source code is governed by the GNU AGPL v3.0 license that can be found in the LICENSE.md file.
 */

package me.lokka30.levelledmobs.commands.levelledmobs.subcommands;

import me.lokka30.levelledmobs.LevelledMobs;
import me.lokka30.levelledmobs.commands.CommandHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lokka30
 * @since v4.0.0
 * This is a subcommand of the '/levelledmobs' command.
 * This subcommand prints basic information about the installed version of LM.
 * @see me.lokka30.levelledmobs.commands.levelledmobs.LevelledMobsCommand
 * @see CommandHandler
 */
public class InfoSubcommand implements CommandHandler.Subcommand {

    final HashSet<String> labels = new HashSet<>(Set.of("INFO", "ABOUT"));
    @Override
    public @NotNull HashSet<String> getLabels() {
        return labels;
    }

    @Override
    public void run(@NotNull LevelledMobs main, @NotNull CommandSender sender, @NotNull String baseCommandLabel, @NotNull String subCommandLabel, @NotNull String[] args) {
        /*
        TODO
            lokka30: Complete method body
         */
        sender.sendMessage("Info.work in progress");
    }

    @Override
    public @NotNull List<String> getSuggestions(@NotNull LevelledMobs main, @NotNull CommandSender sender, @NotNull String baseCommandLabel, @NotNull String subCommandLabel, @NotNull String[] args) {
        /*
        TODO
            lokka30: Complete method body
         */

        return new ArrayList<>();
    }
}
