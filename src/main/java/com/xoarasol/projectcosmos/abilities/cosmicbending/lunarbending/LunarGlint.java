package com.xoarasol.projectcosmos.abilities.cosmicbending.lunarbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.api.LunarAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LunarGlint extends LunarAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.RADIUS)
    private double radius;
    @Attribute("Pull")
    private double pull;
    private double impactRadius;
    private double knockback;
    private double damage;
    private long onMissCooldown;


    private Location origin;
    private Location location;
    private Vector direction;

    private LivingEntity target;
    private boolean latched;
    private long time;
    private int an;
    private int pstage;

    public LunarGlint(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, LunarGlint.class)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Lunar.LunarGlint.Cooldown");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.LunarGlint.Range");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.LunarGlint.ProjectileSpeed") * (ProjectKorra.time_step / 1000F);
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Lunar.LunarGlint.Duration");
            this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.LunarGlint.CollisionRadius");
            this.pull = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.LunarGlint.Pull");
            this.impactRadius = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Lunar.LunarGlint.ImpactRadius");
            this.knockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.LunarGlint.Knockback");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.LunarGlint.Damage");
            this.onMissCooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Lunar.LunarGlint.OnMissCooldown");

            this.origin = GeneralMethods.getMainHandLocation(this.player);
            this.location = origin.clone();
            this.direction = origin.getDirection();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.66f);

            start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBinds(this)) {
            this.bPlayer.addCooldown(this);
            remove();
            return;
        }
        if (!latched) {
            for (int i = 0; i <= 2; i++) {
                if (GeneralMethods.isSolid(location.getBlock()) || isWater(location.getBlock()) || this.location.distance(origin) > range) {
                    this.bPlayer.addCooldown(this, onMissCooldown);
                    remove();
                    return;
                }
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, radius)) {
                    if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                        target = (LivingEntity) entity;
                        latched = true;
                        time = System.currentTimeMillis();
                        break;
                    }
                }

                new ColoredParticle(Color.fromRGB(191, 207, 255), 2).display(location, 1, 0.1, 0.1, 0.1);
                new ColoredParticle(Color.fromRGB(188, 205, 255), 2).display(location, 1, 0.1, 0.1, 0.1);
                new ColoredParticle(Color.fromRGB(165, 171, 206), 2).display(location, 1, 0.1, 0.1, 0.1);
                Spirals();
                Spirals2();

                //new sound

                location.getWorld().playSound(location, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1, 2);
                location.add(direction.clone().normalize().multiply(speed / 2));

            }
        } else {
            if (System.currentTimeMillis() > time + duration) {
                this.bPlayer.addCooldown(this);
                remove();
                return;
            }
            grid();
            grid2();

            if (this.player.isSneaking() && this.bPlayer.getBoundAbilityName().equalsIgnoreCase("LunarGlint")) {
                Vector vec = PCMethods.createDirectionalVector(player.getEyeLocation(), target.getEyeLocation());
                GeneralMethods.setVelocity(this, player, vec.normalize().multiply(pull));

                BlockData bData = Bukkit.createBlockData(Material.END_STONE);
                ParticleEffect.BLOCK_DUST.display(player.getLocation(), 2, 0, 0, 0, bData);
                ParticleEffect.BLOCK_DUST.display(player.getLocation(), 2, 0.3, 0.3, 0.3, bData);
                new ColoredParticle(Color.fromRGB(192, 207, 255), 2).display(player.getLocation(), 2, 0.3, 0.3, 0.3);
                new ColoredParticle(Color.fromRGB(188, 205, 255), 2).display(player.getLocation(), 2, 0.3, 0.3, 0.3);
                new ColoredParticle(Color.fromRGB(165, 171, 206), 2).display(player.getLocation(), 2, 0.3, 0.3, 0.3);

                if (player.getLocation().distance(target.getLocation()) < 1.5) {
                    impact();

                }
            }
        }
    }

    private void grid() {
        Location centre = target.getLocation();
        double increment = (2 * Math.PI) / 36;
        double angle = pstage * increment;
        double x = centre.getX() + (1.0 * Math.cos(angle));
        double z = centre.getZ() + (1.0 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 1.2, z);

        new ColoredParticle(Color.fromRGB(192, 207, 255), 1).display(loc, 3, 0, 0, 0);

        double x2 = centre.getX() + (1 * -Math.cos(angle));
        double z2 = centre.getZ() + (1 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 1, z2);

        new ColoredParticle(Color.fromRGB(145, 157, 193), 1).display(loc2, 3, 0, 0, 0);

        pstage++;
    }

    private void grid2() {
        Location centre = target.getLocation();
        double increment = (2 * Math.PI) / 36;
        double angle = pstage * increment;
        double x = centre.getX() + (1 * Math.cos(angle));
        double z = centre.getZ() + (1 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 0.8, z);

        new ColoredParticle(Color.fromRGB(103, 111, 137),  1).display(loc, 3, 0, 0, 0);

        double x2 = centre.getX() + (1 * -Math.cos(angle));
        double z2 = centre.getZ() + (1 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 0.6, z2);

        new ColoredParticle(Color.fromRGB(145, 157, 193), 1).display(loc2, 3, 0, 0, 0);

        pstage++;
    }

    private void Spirals() {
        this.an += 20;
        if (this.an > 360)
            this.an = 0;
        for (int i = 0; i < 2; i++) {
            for (double d = -1.0D; d <= 0.0D;

                 d += 1.0D) {
                Location l = this.location.clone();
                double r = d * -1.0D / 1.0D;
                if (r > 0.6D)
                    r = 0.6D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, (this.an + 180 * i) + d, r);
                Location pl = l.clone().add(ov.clone());


                switch (i) {
                    case 0:

                        new ColoredParticle(Color.fromRGB(145, 157, 193), 1).display(pl, 5, 0.1, 0.1, 0.1);
                        break;
                    case 1:

                        new ColoredParticle(Color.fromRGB(145, 157, 193), 1).display(pl, 5, 0.1, 0.1, 0.1);
                        break;
                }
            }
        }
    }

    private void Spirals2() {
        this.an += 20;
        if (this.an > 360)
            this.an = 0;
        for (int i = 0; i < 2; i++) {
            for (double d = -2.0D; d <= 0.0D;

                 d += 2.0D) {
                Location l = this.location.clone();
                double r = d * -1.0D / 1.0D;
                if (r > 0.8D)
                    r = 0.8D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, (this.an + 180 * i) + d, r);
                Location pl = l.clone().add(ov.clone());


                switch (i) {
                    case 0:

                        new ColoredParticle(Color.fromRGB(103, 111, 137), 1).display(pl, 5, 0.1, 0.1, 0.1);
                        break;
                    case 1:

                        new ColoredParticle(Color.fromRGB(103, 111, 137), 1).display(pl, 5, 0.1, 0.1, 0.1);
                        break;
                }
            }
        }
    }
    private void impact() {
        ParticleEffect.EXPLOSION_HUGE.display(player.getLocation(), 3, 0, 0, 0);
        ParticleEffect.FLASH.display(player.getLocation(), 3, 0.1, 0.1, 0.1);

        BlockData bData = Bukkit.createBlockData(Material.IRON_BLOCK);
        ParticleEffect.BLOCK_DUST.display(player.getLocation(), 15, impactRadius / 2, impactRadius / 2, impactRadius / 2, bData);
        ParticleEffect.SMOKE_LARGE.display(player.getLocation(), 15, impactRadius / 2, impactRadius / 2, impactRadius / 2, 0.07);
        ParticleEffect.CLOUD.display(player.getLocation(), 15, impactRadius / 2, impactRadius / 2, impactRadius / 2, 0.07);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1, 2);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1.25f);

        Vector playerKnock = PCMethods.createDirectionalVector(target.getLocation(), player.getLocation());

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), impactRadius)) {
            if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                DamageHandler.damageEntity(entity, player, damage, this);

                Vector vec = PCMethods.createDirectionalVector(player.getLocation(), entity.getLocation());
                GeneralMethods.setVelocity(this, entity, vec.normalize().multiply(knockback));
            }
        }
        GeneralMethods.setVelocity(this, player, playerKnock.normalize().multiply(knockback));
        this.bPlayer.addCooldown(this);
        remove();
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public boolean isHarmlessAbility() {
        return true;
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.Lunar.LunarGlint.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "LunarGlint";
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String getDescription() {
        return "Space Hybrids harness the power of Diana, The Goddess Of The Moons. With this energy they can latch onto entities and throw themselves towards them by manipulating their gravitational pull.";
    }

    @Override
    public String getInstructions() {
        return "Fire: *LeftClick* \n" +
                "Strike: *Hold Shift* to throw yourself at your enemy!";
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
