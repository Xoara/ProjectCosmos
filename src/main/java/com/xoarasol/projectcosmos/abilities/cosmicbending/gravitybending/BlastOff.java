package com.xoarasol.projectcosmos.abilities.cosmicbending.gravitybending;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.api.GravityAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BlastOff extends GravityAbility implements AddonAbility {
    //Plants a 'bomb' on sneak it explodes, dealing no damage, but knocking entities away from it

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.KNOCKBACK)
    private double force;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.DURATION)
    private long duration;

    private Location origin;
    private int currPoint;

    public BlastOff(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, BlastOff.class)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.BlastOff.Cooldown");
            this.force = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.BlastOff.Knockback");
            this.range = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.BlastOff.AffectRadius");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.BlastOff.Duration");

            this.origin = player.getLocation().add(0, -0.1, 0);

            start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBinds(this)) {
            remove();
            return;
        }
        if (System.currentTimeMillis() > duration + getStartTime()) {
            detonate();
            return;
        }
        ParticleEffect.SQUID_INK.display(origin, 1, 0.5,0.5, 0.5);
        ParticleEffect.END_ROD.display(origin, 3, 0.3, 0.3, 0.3);
        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            new ColoredParticle(Color.fromRGB(66, 0, 188), 2).display(origin, 2, 0.5, 0, 0.5);
            new ColoredParticle(Color.fromRGB(45, 0, 130), 2).display(origin, 2, 0.5, 0, 0.5);
            new ColoredParticle(Color.fromRGB(13, 0, 56), 2).display(origin, 2, 0.5, 0, 0.5);
        } else {
            new ColoredParticle(Color.fromRGB(109, 133, 255), 2).display(origin, 2, 0.5, 0, 0.5);
            new ColoredParticle(Color.fromRGB(80, 78, 196), 2).display(origin, 2, 0.5, 0, 0.5);
            new ColoredParticle(Color.fromRGB(72, 49, 175), 2).display(origin, 2, 0.5, 0, 0.5);

        }
    }

    public void detonate() {
        ParticleEffect.FLASH.display(origin, 4, 1, 1, 1);
        ParticleEffect.END_ROD.display(origin, 20, 0.5, 0.5, 0.5, 0.5);
        ParticleEffect.SMOKE_LARGE.display(origin, 20, 1, 1, 1, 0.5);
        ParticleEffect.END_ROD.display(origin, 20, 0.5, 0.5, 0.5, 0.5);
        ParticleEffect.SMOKE_LARGE.display(origin, 20, 1, 1, 1, 0.5);
        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            new ColoredParticle(Color.fromRGB(66, 0, 188), 1.05F).display(origin, 5, 1, 1, 1);
            new ColoredParticle(Color.fromRGB(45, 0, 130), 1.05F).display(origin, 5, 1, 1, 1);
            new ColoredParticle(Color.fromRGB(13, 0, 56), 1.05F).display(origin, 5, 1, 1, 1);
        } else {
            new ColoredParticle(Color.fromRGB(109, 133, 255), 1.05F).display(origin, 5, 1, 1, 1);
            new ColoredParticle(Color.fromRGB(80, 78, 196), 1.05F).display(origin, 5, 1, 1, 1);
            new ColoredParticle(Color.fromRGB(72, 49, 175), 1.05F).display(origin, 5, 1, 1, 1);

        }
        origin.getWorld().playSound(origin, Sound.ENTITY_GENERIC_EXPLODE, 3, 0);
        origin.getWorld().playSound(origin, Sound.ITEM_TRIDENT_RETURN, 3, 0);

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(origin, range)) {
            Vector knock = PCMethods.createDirectionalVector(origin, entity.getLocation());
            GeneralMethods.setVelocity(this, entity, knock.normalize().multiply(force));

        }
        remove();
        return;
    }


    public static void activate (Player player) {
        if (CoreAbility.hasAbility(player, BlastOff.class)) {
            BlastOff bOff = CoreAbility.getAbility(player, BlastOff.class);
            bOff.detonate();;
        }
    }

    @Override
    public void remove() {
        this.bPlayer.addCooldown(this);
        super.remove();
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.BlastOff.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "BlastOff";
    }

    @Override
    public Location getLocation() {
        return this.origin;
    }

    @Override
    public String getDescription() {
        return "This ability allows you to leave pockets of cosmic energy around, and detonate them at will, sending nearby entities flying!";
    }

    @Override
    public String getInstructions() {
        return "- Tap-Shift > Left-Click! -";
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
