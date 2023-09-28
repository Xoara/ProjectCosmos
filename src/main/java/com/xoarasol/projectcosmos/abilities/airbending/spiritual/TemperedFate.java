package com.xoarasol.projectcosmos.abilities.airbending.spiritual;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.SpiritualAbility;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.xoarasol.projectcosmos.ProjectCosmos;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Random;

public class TemperedFate extends SpiritualAbility implements AddonAbility {

    public static Config config;
    private Location location;
    private double range;
    private double radius;
    private double height;
    private double damage;
    private double speed;
    private double h;
    private long knockback;
    private long cooldown;
    private int pstage;
    private int currPoint;
    private boolean on_ground = false;
    private Random rand;
    private double t = 0.7853981633974483D;

    public TemperedFate(Player player) {
        super(player);
        this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Air.Spiritual.TemperedFate.Range");
        this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Air.Spiritual.TemperedFate.ImpactRadius");
        this.height = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Air.Spiritual.TemperedFate.StartingHeight");
        this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Air.Spiritual.TemperedFate.Speed");
        this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Air.Spiritual.TemperedFate.Damage");
        this.knockback = ProjectCosmos.plugin.getConfig().getLong("Abilities.Air.Spiritual.TemperedFate.Knockback");
        this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Air.Spiritual.TemperedFate.Cooldown");
        this.h = 0.0D;
        this.location = GeneralMethods.getTargetedLocation(player, this.range, new Material[0]);
        if (location == null) {
            return;
        }
        if (getGround(this.location) == null)
            return;
        this.location.setY(getGround(this.location).getLocation().getY());

        if (bPlayer.isOnCooldown(this) || !bPlayer.canBend(this) || !player.isSneaking()) {
            return;
        }
        location.getWorld().playSound(location,  Sound.ITEM_TRIDENT_RIPTIDE_3, 5, 0F);
        location.getWorld().playSound(location,  Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 5, 0.7F);
        location.getWorld().playSound(location,  Sound.ENTITY_EVOKER_PREPARE_ATTACK, 5, 1.14F);
        start();
        this.bPlayer.addCooldown((Ability) this);
    }

    private Block getGround(Location loc) {
        Block gb = GeneralMethods.getTopBlock(loc, 2147483647);
        if (gb != null)
            return gb;
        return null;
    }

    public void progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            remove();
            return;
        }

        if (this.t >= 20) {
            remove();
            return;
        }
        if (getGround(location) == null) {
            remove();
            return;
        }

        createBeam(this.location);
    }

    private void createBeam(Location location) {
        if (!this.on_ground) {
            Location centre = new Location(location.getWorld(), location.getX(), location.getY() + this.height,
                    location.getZ());
            if (this.height >= 0.0D) {
                this.height -= 0.0914D * this.speed;
                this.h += 0.0514D / this.speed * 1.14D;
            } else {
                this.on_ground = true;
            }

            GeneralMethods.displayColoredParticle("00FFCB", centre, 10, 0.3140000104904175D, 0.3140000104904175D, 0.3140000104904175D);
            GeneralMethods.displayColoredParticle("00FFCB", centre, 10, 0.21400000154972076D, 0.21400000154972076D, 0.21400000154972076D);
            GeneralMethods.displayColoredParticle("00FFCB", centre, 10, 0.12399999797344208D, 0.12399999797344208D, 0.12399999797344208D);
            ParticleEffect.SPELL_INSTANT.display(centre, 5, 0.0D, this.height, 0.0D, 0.04);
            ParticleEffect.SPIT.display(centre, 5, 0.0D, this.height, 0.0D, 0.04);
            burst(100, 4, 10);

            //speed + speed //

            double increment = (4.4 + 4.4) * Math.PI / 36.0D;
            double angle = this.pstage * increment;
            double x = centre.getX() + this.radius * Math.cos(angle);
            double y = centre.getY() + 0.0D * Math.ceil(angle);
            double z = centre.getZ() + this.radius * Math.sin(angle);
            Location loc = new Location(centre.getWorld(), x, y + 0.5D, z);

            GeneralMethods.displayColoredParticle("00FFCB", loc, 10, 0.25, 0.25, 0.25);
            GeneralMethods.displayColoredParticle("00FFCB", loc, 10, 0.35, 0.35, 0.35);
            GeneralMethods.displayColoredParticle("00FFCB", loc, 10, 0.55, 0.5, 0.55);
            ParticleEffect.SPELL_INSTANT.display(loc, 10, 0.0D, 0.0D, 0.0D, 0.04);
            ParticleEffect.SPIT.display(loc, 10, 0.0D, 0.0D, 0.0D, 0.2D);

            this.pstage++;
        } else {
            impact(location);
            location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 2F, 1.4F);
            location.getWorld().playSound(location, Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1.0F, 1.5F);
        }
    }

    private void impact(Location location) {
        for (int d = 0; d < 360; d++) {
            ParticleEffect.EXPLOSION_LARGE.display(location, 1, 2.5, Math.ceil(0.5D), 2.5, 1.0D);
            ParticleEffect.SPELL_INSTANT.display(location, 4, this.radius - 1.0D, Math.ceil(0.5D), this.radius - 1.0D, 0.05f);

        }
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, this.radius)) {
            if (entity instanceof LivingEntity && entity.getUniqueId() != this.player.getUniqueId()) {
                if (this.damage > 0.0D)
                    DamageHandler.damageEntity(entity, this.damage, (Ability) this);
                GeneralMethods.setVelocity(this, entity, this.location.getDirection().normalize().multiply(knockback));
            }
        }
        remove();
        this.bPlayer.addCooldown((Ability) this);
    }

    private void burst(int points, float size, int speed) {
        for (int i = 0; i < speed; i++) {
            this.currPoint += 360 / points;
            if (this.currPoint > 360)
                this.currPoint = 0;
            double angle = this.currPoint * Math.PI / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);
            Location loc = new Location(location.getWorld(), location.getX(), location.getY() + this.height,
                    location.getZ()).add(x, 0, z);

            ParticleEffect.SPELL_INSTANT.display(loc, 5, 0.1, 0.1, 0.1, 0.04);
        }
    }


    public long getCooldown() {
        return this.cooldown;
    }

    public Location getLocation() {
        return (this.player != null) ? this.player.getLocation() : null;
    }

    public String getName() {
        return "TemperedFate";
    }

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

    public boolean isSneakAbility() {
        return true;
    }

    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Air.Spiritual.TemperedFate.Enabled");
    }

    public String getDescription() {
        return "This ability is rarely used amongst Spiritual Airbenders and is quite difficult to master. " +
                "This ability allows you to call upon and summon an arcing bolt of spiritual energy coming from the sky. " +
                "Players struck by the impact will be damaged and knocked back!";

    }

    public String getInstructions() {
        return "- Hold-Shift > Left-Click! -";
    }

    @Override
    public void load() {
    }

    @Override
    public void stop() {
    }

    @Override
    public String getAuthor() {
        return "_Hetag1216_ & XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }

}