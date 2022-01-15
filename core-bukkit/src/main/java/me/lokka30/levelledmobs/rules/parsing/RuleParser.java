/*
 * This file is Copyright (c) 2020-2022 lokka30.
 * This file is/was present in the LevelledMobs resource.
 * Repository: <https://github.com/lokka30/LevelledMobs>
 * Use of this source code is governed by the GNU GPL v3.0
 * license that can be found in the LICENSE.md file.
 */

package me.lokka30.levelledmobs.rules.parsing;

import de.leonhard.storage.Yaml;
import de.leonhard.storage.sections.FlatFileSection;
import me.lokka30.levelledmobs.LevelledMobs;
import me.lokka30.levelledmobs.file.FileHandler;
import me.lokka30.levelledmobs.rules.Group;
import me.lokka30.levelledmobs.rules.Rule;
import me.lokka30.levelledmobs.rules.RuleListener;
import me.lokka30.levelledmobs.rules.action.RuleAction;
import me.lokka30.levelledmobs.rules.action.RuleActionType;
import me.lokka30.levelledmobs.rules.action.type.ExecuteAction;
import me.lokka30.levelledmobs.rules.condition.RuleCondition;
import me.lokka30.levelledmobs.rules.condition.RuleConditionType;
import me.lokka30.levelledmobs.rules.condition.type.EntityTypeCondition;
import me.lokka30.levelledmobs.rules.condition.type.IsLevelledCondition;
import me.lokka30.levelledmobs.rules.condition.type.LightLevelFromBlockCondition;
import me.lokka30.levelledmobs.rules.condition.type.LightLevelFromSkyCondition;
import me.lokka30.levelledmobs.rules.option.RuleOption;
import me.lokka30.levelledmobs.rules.option.RuleOptionType;
import me.lokka30.levelledmobs.util.Utils;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;

/**
 * @author lokka30
 * @since v4.0.0
 * This class parses rules from the Rules configuration
 * into Rule objects that are accessed by the plugin.
 * It also parses other components of the Rules system,
 * such as groups and presets.
 */
public class RuleParser {

    private final HashSet<Group<EntityType>> mobGroups = new HashSet<>();
    public @NotNull HashSet<Group<EntityType>> getMobGroups() { return mobGroups; }

    private final HashSet<Group<Biome>> biomeGroups = new HashSet<>();
    public @NotNull HashSet<Group<Biome>> getBiomeGroups() { return biomeGroups; }

    private final HashSet<RuleListener> ruleListeners = new HashSet<>();
    public @NotNull HashSet<RuleListener> getRuleListeners() { return ruleListeners; }

    private final HashSet<Rule> presets = new HashSet<>();
    public @NotNull HashSet<Rule> getPresets() { return presets; }

    public void parse() {
        clearCache();

        addRuleGroups();
        addRulePresets();
        addRuleListeners();
    }

    void clearCache() {
        // listeners
        getRuleListeners().clear();

        // presets
        getPresets().clear();

        // groups
        getMobGroups().clear();
        getBiomeGroups().clear();
    }

    void addRuleGroups() {
        addMobRuleGroups();
        addBiomeRuleGroups();
    }

    void addMobRuleGroups() {
        final Yaml data = LevelledMobs.getInstance().getFileHandler().getGroupsFile().getData();
        for(
                String mobGroupName : data.getSection("mob-groups").singleLayerKeySet()
        ) {
            EnumSet<EntityType> entityTypes = EnumSet.noneOf(EntityType.class);

            for(
                    String entityTypeStr : data.getStringList("mob-groups." + mobGroupName)
            ) {
                EntityType entityType;
                try {
                    entityType = EntityType.valueOf(entityTypeStr.toUpperCase(Locale.ROOT));
                } catch(IllegalArgumentException ex) {
                    Utils.LOGGER.error("Invalid entity type specified '&b" + entityTypeStr + "&7' in the mob " +
                            "group named '&b" + mobGroupName + "&7'! Fix this ASAP.");
                    continue;
                }

                if(entityTypes.contains(entityType)) {
                    Utils.LOGGER.error("Entity type '&b" + entityTypeStr.toUpperCase(Locale.ROOT) + "&7' has been listed " +
                            "listed more than once in the mob group named '&b" + mobGroupName + "&7'! " +
                            "Fix this ASAP.");
                    continue;
                }

                entityTypes.add(entityType);
            }

            getMobGroups().add(new Group<>(
                    mobGroupName,
                    entityTypes
            ));
        }
    }

    void addBiomeRuleGroups() {
        final Yaml data = LevelledMobs.getInstance().getFileHandler().getGroupsFile().getData();
        for(
                String biomeGroupName : data.getSection("biome-groups").singleLayerKeySet()
        ) {
            EnumSet<Biome> biomes = EnumSet.noneOf(Biome.class);

            for(
                    String biomeStr : data.getStringList("biome-groups." + biomeGroupName)
            ) {
                Biome biome;
                try {
                    biome = Biome.valueOf(biomeStr.toUpperCase(Locale.ROOT));
                } catch(IllegalArgumentException ex) {
                    Utils.LOGGER.error("Invalid biome specified '&b" + biomeStr + "&7' in the biome " +
                            "group named '&b" + biomeGroupName + "&7'! Fix this ASAP.");
                    continue;
                }

                if(biomes.contains(biome)) {
                    Utils.LOGGER.error("Biome '&b" + biomeStr.toUpperCase(Locale.ROOT) + "&7' has been listed " +
                            "listed more than once in the biome group named '&b" + biomeGroupName + "&7'! " +
                            "Fix this ASAP.");
                    continue;
                }

                biomes.add(biome);
            }

            getBiomeGroups().add(new Group<>(
                    biomeGroupName,
                    biomes
            ));
        }
    }

    void addRulePresets() {
        LevelledMobs.getInstance().getFileHandler().getPresetsFile().getData()
                .getSection("presets").singleLayerKeySet()
                .forEach(presetId -> {
                    Optional<Rule> preset = parseRule(true, presetId, "presets." + presetId);
                    if(preset.isPresent()) {
                        Utils.LOGGER.error("Unable to register preset '&b" + presetId + "&7' due to a parsing error. Fix this ASAP.");
                    } else {
                        presets.add(preset.get());
                    }
                });
    }

    @NotNull
    Optional<Rule> parseRule(
            boolean isPreset,
            @NotNull final String identifier,
            @NotNull final String path
    ) {
        final FileHandler fh = LevelledMobs.getInstance().getFileHandler();
        final Yaml data = isPreset ? fh.getPresetsFile().getData() : fh.getListenersFile().getData();
        final String ruleOrPreset = isPreset ? "preset" : "rule";

        final Optional<String> description = Optional.ofNullable(data.getString(path + ".description"));

        final HashSet<Rule> presetsInRule = new HashSet<>();
        if(!isPreset) {
            data.getStringList(path + ".use-presets").forEach(presetId -> {
                final Optional<Rule> presetInRule = presets.stream()
                        .filter(preset -> preset.identifier().equals(presetId))
                        .findFirst();
                if(presetInRule.isPresent()) {
                    Utils.LOGGER.error("Rule '&b" + identifier + "&7' wants to use preset '&b" + presetId + "&7', but that exact preset is not configured. Fix this ASAP.");
                } else {
                    presetsInRule.add(presetInRule.get());
                }
            });
        }

        final HashSet<RuleCondition> conditions = new HashSet<>();
        for(String ruleConditionTypeStr : data.getSection(path + ".conditions").singleLayerKeySet()) {
            final Optional<RuleConditionType> ruleConditionType = RuleConditionType.fromId(ruleConditionTypeStr);

            if(ruleConditionType.isPresent()) {
                Utils.LOGGER.error("The " + ruleOrPreset + " '&b" + identifier + "&7' has an invalid condition" +
                        " specified, named '&b" + ruleConditionTypeStr + "&7'. Fix this ASAP.");
            } else {
                conditions.add(processRuleCondition(
                        ruleConditionType.get(),
                        data.getSection(path + ".conditions." + ruleConditionTypeStr)
                ));
            }
        }

        final HashSet<RuleAction> actions = new HashSet<>();
        for(String ruleActionTypeStr : data.getSection(path + ".actions").singleLayerKeySet()) {
            final Optional<RuleActionType> ruleActionType = RuleActionType.fromId(ruleActionTypeStr);

            if(ruleActionType.isPresent()) {
                Utils.LOGGER.error("The " + ruleOrPreset + " '&b" + identifier + "&7' has an invalid action" +
                        " specified, named '&b" + ruleActionTypeStr + "&7'. Fix this ASAP.");
            } else {
                actions.add(processRuleAction(
                        ruleActionType.get(),
                        data.getSection(path + ".actions." + ruleActionTypeStr)
                ));
            }
        }

        final HashSet<RuleOption> options = new HashSet<>();
        for(String ruleOptionTypeStr : data.getSection(path + ".options").singleLayerKeySet()) {
            final Optional<RuleOptionType> ruleOptionType = RuleOptionType.fromId(ruleOptionTypeStr);

            if(ruleOptionType.isPresent()) {
                Utils.LOGGER.error("The " + ruleOrPreset + " '&b" + identifier + "&7' has an invalid option" +
                        " specified, named '&b" + ruleOptionTypeStr + "&7'. Fix this ASAP.");
            } else {
                options.add(processRuleOption(
                        ruleOptionType.get(),
                        data.getSection(path + ".options." + ruleOptionTypeStr)
                ));
            }
        }

        return Optional.of(new Rule(
                isPreset,
                identifier,
                description,
                conditions,
                actions,
                options,
                presetsInRule
        ));
    }

    public boolean hasPreset(String presetId) {
        return presets.stream().anyMatch(preset -> preset.identifier().equals(presetId));
    }

    @NotNull
    RuleCondition processRuleCondition(
            final @NotNull RuleConditionType type,
            final @NotNull FlatFileSection section
    ) {
        switch(type) {
            case ENTITY_TYPE: return EntityTypeCondition.of(section);
            case IS_LEVELLED: return IsLevelledCondition.of(section);
            case LIGHT_LEVEL_FROM_BLOCK: return LightLevelFromBlockCondition.of(section);
            case LIGHT_LEVEL_FROM_SKY: return LightLevelFromSkyCondition.of(section);
            default: throw new IllegalStateException(
                    "Rule condition '&b" + type + "&7' does not have in-built processing logic!" +
                            " If this is meant to be a valid rule condition, and it is not a typo, please inform LevelledMobs" +
                            " developers. Otherwise, fix this ASAP."
            );
        }
    }

    @NotNull
    RuleAction processRuleAction(
            final @NotNull RuleActionType type,
            final @NotNull FlatFileSection section
    ) {
        switch(type) {
            case EXECUTE: return new ExecuteAction(); //TODO
            default: throw new IllegalStateException(
                    "Rule action '&b" + type + "&7' does not have in-built processing logic!" +
                            " If this is meant to be a valid rule action, and it is not a typo, please inform LevelledMobs" +
                            " developers. Otherwise, fix this ASAP."
            );
        }
    }

    @NotNull
    RuleOption processRuleOption(
            final @NotNull RuleOptionType type,
            final @NotNull FlatFileSection section
    ) {
        switch(type) {
            case TO_DO: throw new UnsupportedOperationException("Options not yet implemented!"); //TODO
            default: throw new IllegalStateException(
                    "Rule option '&b" + type + "&7' does not have in-built processing logic!" +
                            " If this is meant to be a valid rule option, and it is not a typo, please inform LevelledMobs" +
                            " developers. Otherwise, fix this ASAP."
                );
        }
    }

    void addRuleListeners() {
        //TODO
    }
}
