package me.lokka30.levelledmobs.bukkit.listener.action;

import me.lokka30.levelledmobs.bukkit.logic.context.Context;
import me.lokka30.levelledmobs.bukkit.logic.function.process.Process;
import me.lokka30.levelledmobs.bukkit.logic.function.process.action.Action;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class ExitAllAction extends Action {

    public ExitAllAction(
        @NotNull Process process,
        @NotNull CommentedConfigurationNode node
    ) {
        super(process, node);
    }

    @Override
    public void run(Context context) {
        getProcess().getFunction().setShouldExitAll(true);
        getProcess().getFunction().setShouldExit(true);
        getProcess().setShouldExit(true);
    }
}
