package com.xoarasol.projectcosmos.abilities.laserbending.combos;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.LaserAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class PhotonRing extends LaserAbility implements AddonAbility, ComboAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.RADIUS)
    private double radius;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute("RadianceDuration")
    private int radianceDuration;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.RANGE)
    private double range;

    private Location origin;
    private Location location;
    private Vector direction;

    private boolean isProjectile;
    private int currPoint = 0;

    private long time;

    public PhotonRing(Player player) {
        super(player);

        if (!CoreAbility.hasAbility(this.player, PhotonRing.class) && this.bPlayer.canBendIgnoreBinds(this)) {

            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Combos.PhotonRing.Cooldown");
            this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.PhotonRing.Radius");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Combos.PhotonRing.Duration");
            this.radianceDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Combos.PhotonRing.RadianceDuration");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.PhotonRing.Speed");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.PhotonRing.Range");

            this.origin = this.player.getEyeLocation();
            this.location = this.origin.clone();
            this.direction = this.player.getEyeLocation().getDirection();

            this.isProjectile = true;

            this.speed *= (ProjectKorra.time_step / 1000F);

            start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            this.remove();
            return;
        }
        if (this.isProjectile) {
            if (this.location.distance(this.origin) > this.range) {
                remove();
                return;
            }
            if (GeneralMethods.isSolid(this.location.getBlock()) || isWater(this.location.getBlock())) {
                this.isProjectile = false;
                this.time = System.currentTimeMillis();
                this.location = this.location.add(0, 0.3, 0);
                createRotatingCircle();
                return;
            }
            new ColoredParticle(Color.fromRGB(207, 203, 103), 2).display(this.location, 1, 0, 0, 0);
            new ColoredParticle(Color.fromRGB(207, 203, 103), 1).display(this.location, 3, 0.2, 0.2, 0.2);
            ParticleEffect.END_ROD.display(this.location, 1, 0.1,0.1,0.1);
            this.location.add(this.direction.normalize().multiply(this.speed));

        } else {
            if (System.currentTimeMillis() > this.time + this.duration) {
                remove();
                return;
            }
            createRotatingCircle();

            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, this.radius)) {
                if (entity instanceof LivingEntity) {
                    if (!isRadiant((LivingEntity) entity)) {
                        setRadiance((LivingEntity) entity, this.radianceDuration);
                    }
                }
            }
        }
    }

    private void createRotatingCircle() {
        Location loc;
        for (int i = 0; i < 10; ++i) {
            currPoint += 360 / 180;

            if (currPoint > 360) {
                currPoint = 0;
            }

            double angle = currPoint * 3.141592653589793D / 180.0D;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            loc = this.location.clone().add(x, 0, z);

            double particleX, particleY, particleZ;
            particleX = loc.clone().add(0, 0.6, 0).getX() - loc.getX();
            particleY = loc.clone().add(0, 0.6, 0).getY() - loc.getY();
            particleZ = loc.clone().add(0, 0.6, 0).getZ() - loc.getZ();

            if (currPoint % 10 == 0) {
                ParticleEffect.FIREWORKS_SPARK.display(loc, 2, particleX, particleY, particleZ, 0.1);
                loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_POWER_SELECT, 1, 0.55f);
            }

            new ColoredParticle(Color.fromRGB(207, 203, 103), 2).display(loc, 1, 0, 0, 0);
        }
    }

    @Override
    public void remove() {
        this.bPlayer.addCooldown(this);
        location.getWorld().playSound(location, Sound.BLOCK_BEACON_POWER_SELECT, 4, 0.55f);
        location.getWorld().playSound(location, Sound.BLOCK_BEACON_POWER_SELECT, 4, 0f);
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
        return  ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.Combos.PhotonRing.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "PhotonRing";
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public String getAuthor() {
        return "Xoara & KWilson272";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }


    @Override
    public String getDescription() {
        return "For a short time, radiationbenders are able to create a small region which will irradiate all entities within it.";
    }

    @Override
    public String getInstructions() {
        return "Usage: ElectronLance (Sneak-Down) > Contaminate (Sneak-Up) > Contaminate (Left-Click)";
    }

    @Override
    public Object createNewComboInstance(Player player) {
        return new PhotonRing(player);
    }

    @Override
    public ArrayList<AbilityInformation> getCombination() {
        final ArrayList<AbilityInformation> photonRing = new ArrayList<>();
        photonRing.add(new AbilityInformation("ElectronLance", ClickType.SHIFT_DOWN));
        photonRing.add(new AbilityInformation("Contaminate", ClickType.SHIFT_UP));
        photonRing.add(new AbilityInformation("Contaminate", ClickType.LEFT_CLICK));
        return photonRing;
    }

    @Override
    public void load() {
    }

    @Override
    public void stop() {
    }
}
