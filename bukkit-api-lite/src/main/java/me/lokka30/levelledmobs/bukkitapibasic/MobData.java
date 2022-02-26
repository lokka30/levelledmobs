package me.lokka30.levelledmobs.bukkitapibasic;

import me.lokka30.levelledmobs.bukkitapibasic.util.NamespacedKeys;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public final class MobData {

    private MobData() { throw new IllegalStateException("Instantiaton of utility-type class"); }

    public static Optional<Integer> getLevel(final LivingEntity entity) {
        return Optional.ofNullable(getPDC(entity).get(NamespacedKeys.LEVEL_KEY, PersistentDataType.INTEGER));
    }

    public static Optional<Integer> getMinLevel(final LivingEntity entity) {
        return Optional.ofNullable(getPDC(entity).get(NamespacedKeys.MIN_LEVEL_KEY, PersistentDataType.INTEGER));
    }

    public static Optional<Integer> getMaxLevel(final LivingEntity entity) {
        return Optional.ofNullable(getPDC(entity).get(NamespacedKeys.MAX_LEVEL_KEY, PersistentDataType.INTEGER));
    }

    // NOTE: Non-standard spawn reasons can be specified here! Do not expect all values to exist in the
    // SpawnReason enum!
    public static Optional<String> getSpawnReason(final LivingEntity entity) {
        return Optional.ofNullable(getPDC(entity).get(NamespacedKeys.SPAWN_REASON_KEY, PersistentDataType.STRING));
    }

    public static Optional<String> getWasBabyMob(final LivingEntity entity) {
        return Optional.ofNullable(getPDC(entity).get(NamespacedKeys.WAS_BABY_MOB_KEY, PersistentDataType.STRING));
    }

    public static Optional<String> getOverriddenEntityName(final LivingEntity entity) {
        return Optional.ofNullable(getPDC(entity).get(NamespacedKeys.OVERRIDDEN_ENTITY_NAME_KEY, PersistentDataType.STRING));
    }

    @Deprecated
    public static void setOverriddenEntityName(final LivingEntity entity, final String val) {
        getPDC(entity).set(NamespacedKeys.OVERRIDDEN_ENTITY_NAME_KEY, PersistentDataType.STRING, val);
    }

    public static Optional<String> getPlayerLevellingId(final LivingEntity entity) {
        return Optional.ofNullable(getPDC(entity).get(NamespacedKeys.PLAYER_LEVELLING_ID_KEY, PersistentDataType.STRING));
    }

    public static Optional<Double> getChanceRuleAllowed(final LivingEntity entity) {
        return Optional.ofNullable(getPDC(entity).get(NamespacedKeys.CHANCE_RULE_ALLOWED_KEY, PersistentDataType.DOUBLE));
    }

    public static Optional<Double> getChanceRuleDenied(final LivingEntity entity) {
        return Optional.ofNullable(getPDC(entity).get(NamespacedKeys.CHANCE_RULE_DENIED_KEY, PersistentDataType.DOUBLE));
    }

    public static Optional<String> getNametagFormat(final LivingEntity entity) {
        return Optional.ofNullable(getPDC(entity).get(NamespacedKeys.NAMETAG_FORMAT_KEY, PersistentDataType.STRING));
    }

    public static Optional<String> getMajorPluginVersionKey(final LivingEntity entity) {
        return Optional.ofNullable(getPDC(entity).get(NamespacedKeys.MAJOR_PLUGIN_VERSION_KEY, PersistentDataType.STRING));
    }

    private static PersistentDataContainer getPDC(final LivingEntity entity) {
        return entity.getPersistentDataContainer();
    }
}
