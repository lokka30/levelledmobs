package io.github.arcaneplugins.levelledmobs.bukkit.listener.impl

import io.github.arcaneplugins.levelledmobs.bukkit.listener.ListenerWrapper

class TestListener : ListenerWrapper(false) {
    /*
    @EventHandler
    public void onEntityInteract(final PlayerInteractEntityEvent event) {

        // This code unlevels a mob on right-click

        if(!event.getPlayer().isOp()) return;
        if(event.getHand() != EquipmentSlot.HAND) return;
        if(!(event.getRightClicked() instanceof LivingEntity lent)) return;
        if(!EntityDataUtil.isLevelled(lent, false)) return;

        InternalEntityDataUtil.unlevelMob(lent);
        lent.getWorld().strikeLightningEffect(lent.getLocation());
        lent.getWorld().spawnParticle(Particle.CLOUD, lent.getLocation(), 20);
        event.getPlayer().sendMessage(ChatColor.GREEN + "Mob has been unlevelled.");
    }*/
}