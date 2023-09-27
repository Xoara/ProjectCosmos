package com.xoarasol.projectcosmos.abilities.laserbending.combos;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.RadiationAbility;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;

public class Decontaminate extends RadiationAbility implements ComboAbility, AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;

    public Decontaminate(Player player) {
        super(player);

        if (isRadiant(player)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Combos.Decontaminate.Cooldown");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Combos.Decontaminate.Duration");

            removeRadiance(player);
            this.bPlayer.addCooldown(this);
            start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            remove();
            return;
        }
        if (System.currentTimeMillis() > this.getStartTime() + this.duration) {
            remove();
            return;
        }
        for (PotionEffect potion : this.player.getActivePotionEffects()) {
            if (!isPositiveEffect(potion.getType())) {
                this.player.removePotionEffect(potion.getType());
            }
        }
        ParticleEffect.FIREWORKS_SPARK.display(this.player.getLocation(), 2, 0.5, 0.5, 0.5, 0.1);
        ParticleEffect.SNEEZE.display(this.player.getLocation(), 2, 0.5, 0.5, 0.5, 0.1);
    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public boolean isIgniteAbility() {
        return false;
    }

    @Override
    public boolean isExplosiveAbility() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.Combos.Decontaminate.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "Decontaminate";
    }

    @Override
    public Location getLocation() {
        return this.player != null ? this.player.getLocation() : null;
    }


    @Override
    public String getDescription() {
        return "By clearing themselves of radiant energy, Radiationbenders are able to heal themselves from negative potion effects.";
    }

    @Override
    public String getInstructions() {
        return "- Contaminate (Left-Click) > Contaminate (Left-Click) > Contaminate (Tap-Sneak) -";
    }

    @Override
    public Object createNewComboInstance(Player player) {
        return new Decontaminate(player);
    }

    @Override
    public ArrayList<AbilityInformation> getCombination() {
        final ArrayList<AbilityInformation> decontaminate = new ArrayList<>();
        decontaminate.add(new AbilityInformation("Contaminate", ClickType.LEFT_CLICK));
        decontaminate.add(new AbilityInformation("Contaminate", ClickType.LEFT_CLICK));
        decontaminate.add(new AbilityInformation("Contaminate", ClickType.SHIFT_DOWN));
        decontaminate.add(new AbilityInformation("Contaminate", ClickType.SHIFT_UP));
        return decontaminate;
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return "KWilson272 & XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }
}
