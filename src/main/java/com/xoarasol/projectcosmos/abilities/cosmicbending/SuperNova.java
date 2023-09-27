package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class SuperNova extends CosmicAbility implements AddonAbility {

    private long cooldown;
    private long chargeTime;
    private double range;
    private double speed;
    private double radius;
    private boolean damageBlocks;
    private boolean revertBlocks;
    private long revertTime;
    private double damage;

    private boolean charged;
    private boolean launched;
    private Vector direction;
    private Location stream;
    private Location origin;
    private double an;
    private TempBlock tempBlock;
    private int p;
    private int p1;
    private int point;
    private double yaw;

    private ArrayList<UUID> affected = new ArrayList<UUID>();
    private Location loca;

    public SuperNova(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, SuperNova.class)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.SuperNova.Cooldown");
            this.chargeTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.SuperNova.ChargeTime");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.SuperNova.Range");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.SuperNova.Damage");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.SuperNova.Speed") * (ProjectKorra.time_step / 1000F);
            this.damageBlocks = ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.SuperNova.DamageBlocks.Enabled");
            this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.SuperNova.DamageBlocks.Radius");
            this.revertBlocks = ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.SuperNova.DamageBlocks.RevertBlocks");
            this.revertTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.SuperNova.DamageBlocks.RevertTime");

            stream = GeneralMethods.getTargetedLocation(player, 1);
            origin = GeneralMethods.getTargetedLocation(player, 1);
            direction = GeneralMethods.getTargetedLocation(player, 1).getDirection();

            start();
        }
    }

    static Material[] unbreakables = {Material.BEDROCK, Material.BARRIER, Material.END_PORTAL_FRAME, Material.END_PORTAL, Material.ENDER_CHEST, Material.CHEST, Material.TRAPPED_CHEST};

    public static boolean isUnbreakable(Block block) {
        if (block.getState() instanceof InventoryHolder) {
            return true;
        }
        if (Arrays.asList(unbreakables).contains(block.getType())) {
            return true;
        }
        return false;
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreCooldowns(this)) {
            remove();
            return;
        }
        if (System.currentTimeMillis() > getStartTime() + chargeTime) {
            charged = true;
        }
        if (stream.getBlock().getType() == Material.OBSIDIAN || stream.getBlock().getType() == Material.OBSIDIAN) {
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        if (origin.distance(stream) >= range) {
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        if ((player.isSneaking()) && (!launched)) {
            stream = GeneralMethods.getTargetedLocation(player, 1);
            origin = GeneralMethods.getTargetedLocation(player, 1);
            direction = GeneralMethods.getTargetedLocation(player, 1).getDirection();
            playEffect();
        } else {
            if (!charged) {
                remove();
                return;
            }
            if (!launched) {
                bPlayer.addCooldown(this);
                launched = true;
            }
            shootSphere();
        }
    }

    private void explode(Location loc) {
        loc.getWorld().playSound(loc, Sound.ENTITY_SHULKER_BULLET_HIT, 0.3F, 0.4F);
    }

    private void createExplosion(Location loc, double radius, Entity entity) {
        if (damageBlocks && !isUnbreakable(loc.getBlock()))
            if (!isTransparent(loc.getBlock()) || entity instanceof LivingEntity) {
                for (Location l : GeneralMethods.getCircle(loc, (int) radius, 0, false, true, 0)) {
                    if (!isUnbreakable(l.getBlock())) {
                        tempBlock = new TempBlock(l.getBlock(), Material.AIR);
                        explode(stream);
                        if (revertBlocks)
                            tempBlock.setRevertTime(revertTime);
                    }
                }
            }
    }

    @SuppressWarnings("deprecation")
    private void shootSphere() {
        direction = player.getEyeLocation().getDirection().normalize();
        stream = stream.add(direction.normalize().multiply(speed));
        ParticleEffect.END_ROD.display(stream, 1, .1F, .1F, .1F, 0.02F);
        ParticleEffect.EXPLOSION_LARGE.display(stream, 2, 1, 1, 1, 0.04);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(81, 8, 31), 2.1F)).display(stream, 1, 2, 2, 2);
            (new ColoredParticle(Color.fromRGB(57, 17, 119), 2.5F)).display(stream, 1, 1.5, 1.5, 1.5);
            (new ColoredParticle(Color.fromRGB(26, 56, 0), 2.5F)).display(stream, 1, 2, 2, 2);
            (new ColoredParticle(Color.fromRGB(114, 75, 11), 2.5F)).display(stream, 1, 1.5, 1.5, 1.5);
            DarkSpirals();
        } else {
            (new ColoredParticle(Color.fromRGB(255, 28, 111), 2.1F)).display(stream, 1, 2, 2, 2);
            (new ColoredParticle(Color.fromRGB(74, 22, 153), 2.5F)).display(stream, 1, 1.5, 1.5, 1.5);
            (new ColoredParticle(Color.fromRGB(132, 95, 255), 2.5F)).display(stream, 1, 2, 2, 2);
            (new ColoredParticle(Color.fromRGB(255, 159, 65), 2.5F)).display(stream, 1, 1.5, 1.5, 1.5);
            LightSpirals();
        }

        Location fakeLoc = stream.clone();
        fakeLoc.setPitch(0);
        fakeLoc.setYaw((float) (yaw += 40));
        Vector direction = fakeLoc.getDirection();
        for (double j = -180; j <= 180; j += 35) {
            Location tempLoc = fakeLoc.clone();
            Vector newDir = direction.clone().multiply(1 * Math.cos(Math.toRadians(j)));
            tempLoc.add(newDir);
            tempLoc.setY(tempLoc.getY() + 0 + (1 * Math.sin(Math.toRadians(j))));
            //sphere
            ParticleEffect.SMOKE_LARGE.display(tempLoc, 2, 0, 0, 0, 0.2);
            tempLoc.getWorld().playSound(tempLoc, Sound.AMBIENT_CRIMSON_FOREST_MOOD, 0.75f, 1.85f);
            tempLoc.getWorld().playSound(tempLoc, Sound.ENTITY_GENERIC_EXPLODE, 0.75f, 0.75F);
        }


        p++;
        for (int i = -180; i < 180; i += 40) {
            double angle = (i * Math.PI / 180);
            double x = 1 * Math.cos(angle + p);
            double z = 1 * Math.sin(angle + p);
            Location loc = stream.clone();
            loc.add(x, 0, z);
            ParticleEffect.ELECTRIC_SPARK.display(loc, 2, 0, 0, 0, 0.04);
        }

        p++;
        Location location = stream.clone();
        for (int i = -180; i < 180; i += 30) {
            double angle = (i * Math.PI / 180);
            double xRotation = 3.141592653589793D / 3 * 2.1;
            Vector v = new Vector(Math.cos(angle + p), Math.sin(angle + p), 0.0D).multiply(1);
            Vector v1 = v.clone();
            rotateAroundAxisX(v, xRotation);
            rotateAroundAxisY(v, -((location.getYaw() * Math.PI / 180) - 1.575));
            rotateAroundAxisX(v1, -xRotation);
            rotateAroundAxisY(v1, -((location.getYaw() * Math.PI / 180) - 1.575));


        }
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(stream, radius)) {
            if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                DamageHandler.damageEntity(entity, damage, this);;

            }
        }
        createExplosion(stream, radius, null);
    }
    private void LightSpirals() {
        this.loca = this.stream.add(this.direction.normalize().multiply(0.4D));
        this.an += 20.0D;
        if (this.an > 360.0D)
            this.an = 0;
        for (int i = 0; i < 3; i++) {
            for (double d = -3.0D; d <= 0.0D;

                 d += 3.0D) {
                Location l = this.loca.clone();
                double r = d * -5.0D / 5.0D;
                if (r > 3.0D)
                    r = 3.0D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, this.an + (90 * i) + d, r);
                Location pl = l.clone().add(ov.clone());
                switch (i) {
                    case 0:
                        new ColoredParticle(Color.fromRGB(112, 205, 198), 9.3f).display(pl, 2, 0, 0, 0);
                        break;
                    case 1:
                        new ColoredParticle(Color.fromRGB(112, 205, 198), 9.3f).display(pl, 2, 0, 0, 0);
                        break;
                    case 2:
                        new ColoredParticle(Color.fromRGB(112, 205, 198), 9.3f).display(pl, 2, 0, 0, 0);
                        break;
                    case 3:
                        new ColoredParticle(Color.fromRGB(112, 205, 198), 9.3f).display(pl, 2, 0, 0, 0);
                        break;
                }
            }
        }
    }

    private void DarkSpirals() {
        this.loca = this.stream.add(this.direction.normalize().multiply(0.4D));
        this.an += 20.0D;
        if (this.an > 360.0D)
            this.an = 0;
        for (int i = 0; i < 3; i++) {
            for (double d = -3.0D; d <= 0.0D;

                 d += 3.0D) {
                Location l = this.loca.clone();
                double r = d * -5.0D / 5.0D;
                if (r > 3.0D)
                    r = 3.0D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, this.an + (90 * i) + d, r);
                Location pl = l.clone().add(ov.clone());
                switch (i) {
                    case 0:
                        new ColoredParticle(Color.fromRGB(80, 78, 196), 9.3f).display(pl, 2, 0, 0, 0);
                        break;
                    case 1:
                        new ColoredParticle(Color.fromRGB(80, 78, 196), 9.3f).display(pl, 2, 0, 0, 0);
                        break;
                    case 2:
                        new ColoredParticle(Color.fromRGB(80, 78, 196), 9.3f).display(pl, 2, 0, 0, 0);
                        break;
                    case 3:
                        new ColoredParticle(Color.fromRGB(80, 78, 196), 9.3f).display(pl, 2, 0, 0, 0);
                        break;
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void playEffect() {
        if (!charged) {
            Location loc = GeneralMethods.getTargetedLocation(player, 5);
            ParticleEffect.ELECTRIC_SPARK.display(loc, 2, 0, 0, 0, 0.005);
            loc.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.4f, 0.90F);

            //sphere

            if (System.currentTimeMillis() > getStartTime() + chargeTime - 2000L) {
                Location centre = GeneralMethods.getTargetedLocation(player, 5);
                double increment = (/* speed */ 2 * Math.PI) / 36;
                double angle = p * increment;
                double x1 = centre.getX() + (/* radius */ 1 * -Math.cos(angle));
                double z1 = centre.getZ() + (/* radius */ 1 * -Math.sin(angle));
                ParticleEffect.ELECTRIC_SPARK.display(new Location(centre.getWorld(), x1, centre.getY(), z1), 2, 0, 0, 0, 0.04);
            }
        } else {
            Location fakeLoc = GeneralMethods.getTargetedLocation(player, 5);
            //Disc

            ParticleEffect.ELECTRIC_SPARK.display(fakeLoc, 2, 0.222, 0.222, 0.222, 0.0111f);
            fakeLoc.getWorld().playSound(fakeLoc, Sound.BLOCK_BEACON_AMBIENT, 1f, 0.96F);

            fakeLoc.setPitch(0);
            fakeLoc.setYaw((float) (yaw += 40));
            Vector direction = fakeLoc.getDirection();
            for (double j = -180; j <= 180; j += 45) {
                Location tempLoc = fakeLoc.clone();
                Vector newDir = direction.clone().multiply(1 * Math.cos(Math.toRadians(j)));
                tempLoc.add(newDir);
                tempLoc.setY(tempLoc.getY() + 0 + (1 * Math.sin(Math.toRadians(j))));

                ParticleEffect.ELECTRIC_SPARK.display(tempLoc, 4, 0.1, 0.1, 0.1);
            }


            p++;
            for (int i = -180; i < 180; i += 40) {
                double angle = (i * Math.PI / 180);
                double x = 1 * Math.cos(angle + p);
                double z = 1 * Math.sin(angle + p);
                Location loc = GeneralMethods.getTargetedLocation(player, 5);
                loc.add(x, 0, z);

            }

            p++;
            Location location = GeneralMethods.getTargetedLocation(player, 5);
            for (int i = -180; i < 180; i += 45) {
                double angle = (i * Math.PI / 180);
                double xRotation = 3.141592653589793D / 3 * 2.1;
                Vector v = new Vector(Math.cos(angle + point), Math.sin(angle + point), 0.0D).multiply(1);
                Vector v1 = v.clone();

                rotateAroundAxisX(v, xRotation);
                rotateAroundAxisY(v, -((location.getYaw() * Math.PI / 180) - 1.575));
                rotateAroundAxisX(v1, -xRotation);
                rotateAroundAxisY(v1, -((location.getYaw() * Math.PI / 180) - 1.575));

            }
            point++;
        }
        if (p >= 360) {
            p = 0;
        }
        p++;
        if (p1 >= 360) {
            p1 = 0;
        }
        p1++;
        if (point >= 360) {
            point = 0;
        }
        point++;
    }

    private Vector rotateAroundAxisX(Vector v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    private Vector rotateAroundAxisY(Vector v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }


    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return stream;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.SuperNova.Enabled");
    }

    @Override
    public String getName() {
        return "SuperNova";
    }

    @Override
    public String getDescription() {
        return "This is an extremely advanced Cosmic ability and requires a lot of focus, concentration and inner balance. With this Ability, a grandmaster Cosmicbender is " +
                "able to create a super nova out of their bare hands, destroying and unmaking EVERYTHING in its path!";
    }

    @Override
    public String getInstructions() {
        return "- Hold-Shift + Left-Click > Release-Shift when it is charged! -";
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
        return true;
    }

    @Override
    public boolean isSneakAbility() {
        return true;
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
