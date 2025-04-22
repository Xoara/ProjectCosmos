package com.xoarasol.projectcosmos.abilities.cosmicbending.gravitybending;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.GravityAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.MovementHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class GravityBinding extends GravityAbility implements AddonAbility {

    private Location location;
    private Location origin;
    private Vector direction;
    private Random rand = new Random();
    public static Config config;

    private long cooldown;
    private double range;
    private double damage;
    private long stunDuration;
    private int an;
    private int leviDuration;

    private ArrayList<Entity> bound;

    public GravityBinding(Player player) {
        super(player);
        if (this.bPlayer.isOnCooldown((Ability)this))
            return;
        setFields();
        start();


        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_DEATH, 2, 0.75f);
        location.getWorld().playSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM, 1, 0.66F);

        this.bPlayer.addCooldown((Ability)this);
    }

    private void setFields() {
        this.cooldown = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.GravityBinding.Cooldown");
        this.range = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.GravityBinding.Range");
        this.stunDuration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.GravityBinding.StunDuration");
        this.damage = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.GravityBinding.Damage");
        this.leviDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.GravityBinding.LeviDuration");
        this.origin = this.player.getLocation().clone().add(0.0D, 1.0D, 0.0D);
        this.location = this.origin.clone();
        this.bound = new ArrayList<>();
        this.direction = this.player.getLocation().getDirection();
    }

    public void progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            remove();
            return;
        }
        if (this.origin.distance(this.location) > this.range) {
            remove();
            return;
        }

        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            remove();
            return;
        }

        if (GeneralMethods.isSolid(this.location.getBlock())) {
            remove();
            return;
        }
        this.location.add(this.direction.multiply(1));

        ParticleEffect.SQUID_INK.display(this.location, 3, 0.2, 0.2, 0.2);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.0F)).display(this.location, 3, 0.5, 0.5, 0.5);
            DarkSpirals();
        } else {
            (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.0F)).display(this.location, 3, 0.5, 0.5, 0.5);
            LightSpirals();
        }

        if (this.rand.nextInt(2) == 0)

            this.location.getWorld().playSound(this.location, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2, 0.6f);

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 1.2D)) {
            if (entity instanceof LivingEntity && entity.getUniqueId() != this.player.getUniqueId()) {
                DamageHandler.damageEntity(entity, this.damage, (Ability)this);
                LivingEntity le = (LivingEntity) entity;
                le.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, (leviDuration * 20 / 1000), 2 - 1));
                //stun((LivingEntity)entity);
                return;
            }
        }
    }
    private void DarkSpirals() {
        this.location = this.location.add(this.direction.normalize().multiply(0.4D));
        this.an += 20.0D;
        if (this.an > 360.0D)
            this.an = 0;
        for (int i = 0; i < 4; i++) {
            for (double d = -4.0D; d <= 0.0D;

                 d += 5.0D) {
                Location l = this.location.clone();
                double r = d * -1.0D / 5.0D;
                if (r > 1.1D)
                    r = 1.1D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, this.an + (90 * i) + d, r);
                Location pl = l.clone().add(ov.clone());
                switch (i) {
                    case 0:
                        new ColoredParticle(Color.fromRGB(66, 0, 188), 1.4f).display(pl, 2, 0, 0, 0);
                        break;
                    case 1:
                        new ColoredParticle(Color.fromRGB(45, 0, 130), 1.4f).display(pl, 2, 0, 0, 0);
                        break;
                    case 2:
                        new ColoredParticle(Color.fromRGB(13, 0, 56), 1.4f).display(pl, 2, 0, 0, 0);
                        break;
                    case 3:
                        new ColoredParticle(Color.fromRGB(0, 0, 0), 1.4f).display(pl, 2, 0, 0, 0);
                        break;
                }
            }
        }
    }

    private void LightSpirals() {
        this.location = this.location.add(this.direction.normalize().multiply(0.4D));
        this.an += 20.0D;
        if (this.an > 360.0D)
            this.an = 0;
        for (int i = 0; i < 4; i++) {
            for (double d = -4.0D; d <= 0.0D;

                 d += 5.0D) {
                Location l = this.location.clone();
                double r = d * -1.0D / 5.0D;
                if (r > 1.1D)
                    r = 1.1D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, this.an + (90 * i) + d, r);
                Location pl = l.clone().add(ov.clone());
                switch (i) {
                    case 0:
                        new ColoredParticle(Color.fromRGB(178, 191, 255), 1.4f).display(pl, 2, 0, 0, 0);
                        break;
                    case 1:
                        new ColoredParticle(Color.fromRGB(109, 133, 255), 1.4f).display(pl, 2, 0, 0, 0);
                        break;
                    case 2:
                        new ColoredParticle(Color.fromRGB(80, 78, 196), 1.4f).display(pl, 2, 0, 0, 0);
                        break;
                    case 3:
                        new ColoredParticle(Color.fromRGB(72, 49, 175), 1.4f).display(pl, 2, 0, 0, 0);
                        break;
                }
            }
        }
    }


    public void stun(LivingEntity entity) {
        double i = Math.random() * 100.0D;
        if (i <= 1000 && !this.bound.contains(entity)) {
            this.bound.add(entity);
            MovementHandler mh = new MovementHandler(entity, (CoreAbility)this);
            mh.stopWithDuration(1000 / 1000L * 20L, PCElement.COSMIC.getColor() + "* Held In Orbit *");
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_WITHER_HURT, 4, 0f);
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 4, 0f);


            return;
        }
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public Location getLocation() {
        return null;
    }

    public String getName() {
        return "GravityBinding";
    }

    public String getDescription() {
        return "CosmicBenders are able to shoot out a specific type of cosmic energy which influences gravity around" +
                "a target. It binds their target and stuns them, in a way ''holding them in orbit''. The Energy of this ability is so intense, " +
                "it can even force an Avatar out of the AvatarState.";
    }

    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.GravityBinding.Enabled");
    }

    public String getInstructions() {
        return "*Left Click*";
    }

    public String getAuthor() {
        return "XoaraSol";
    }

    public String getVersion() {
        return ProjectCosmos.getVersion();
    }

    public boolean isHarmlessAbility() {
        return false;
    }

    public boolean isSneakAbility() {
        return false;
    }

    public void load() {
    }

    public void stop() {

    }
    public boolean isExplosiveAbility() {
        return false;
    }

    public boolean isIgniteAbility() {
        return false;
    }
}
