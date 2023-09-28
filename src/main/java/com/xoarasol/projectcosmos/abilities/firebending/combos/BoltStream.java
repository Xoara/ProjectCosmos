package com.xoarasol.projectcosmos.abilities.firebending.combos;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.LightningAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.xoarasol.projectcosmos.ProjectCosmos;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class BoltStream extends LightningAbility implements ComboAbility {

    private Location location;
    private Location origin;
    private Vector direction;
    private Random rand = new Random();

    private long cooldown;
    private double range;
    private double damage;

    private boolean controllable;
    private int currPoint;
    private double power;
    private double speed;
    private int an;
    private int count = 0;
    private int lightningstrike;
    private double collisionRadius;

    public BoltStream(Player player) {
        super(player);

        if (this.bPlayer.isOnCooldown(this)) {
            return;
        }
        setFields();
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.66f);

        start();
        this.bPlayer.addCooldown(this);
    }

    private void setFields() {
        this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Fire.Combos.BoltStream.Cooldown");
        this.range = ProjectCosmos.plugin.getConfig().getInt("Abilities.Fire.Combos.BoltStream.Range");
        this.damage = ProjectCosmos.plugin.getConfig().getInt("Abilities.Fire.Combos.BoltStream.Damage");
        this.lightningstrike = ProjectCosmos.plugin.getConfig().getInt("Abilities.Fire.Combos.BoltStream.Lightning.Interval");
        this.collisionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Fire.Combos.BoltStream.Lightning.HitRadius");
        this.location = player.getEyeLocation();
        this.controllable = false;
        this.power = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Fire.Combos.BoltStream.Power");
        this.speed = 1;
        this.origin = this.player.getLocation().clone().add(0.0D, 1.0D, 0.0D);
        this.location = this.origin.clone();
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
        if (this.controllable) {
            Vector untouchVector = this.direction.clone().multiply(this.speed);
            Location destinationLocation = this.location.clone().add(this.player.getLocation().getDirection().multiply(this.speed));
            Vector desiredVector = destinationLocation.toVector().subtract(this.location.clone().toVector());
            Vector steeringVector = desiredVector.subtract(untouchVector).multiply(0.25D);
            this.direction = this.direction.add(steeringVector);
        }
        if (GeneralMethods.isSolid(this.location.getBlock())) {
        }

        //booms
        this.location.add(this.direction.clone().multiply(1));

        Spirals();
        location.getWorld().playSound(location, Sound.ENTITY_CREEPER_HURT, 2, 0);

        if (count % lightningstrike == 0) {
            ParticleEffect.FLASH.display(this.location, 1);
            player.getWorld().strikeLightningEffect(this.location);
        }
        count++;


        Sparks(60, 2.25F, 3);
        Sparks(60, 2.5F, 3);
        Sparks(60, 3.0F, 3);

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, collisionRadius)) {
            if (entity instanceof org.bukkit.entity.LivingEntity && entity.getUniqueId() != this.player.getUniqueId()) {
                DamageHandler.damageEntity(entity, this.damage, (Ability) this);
                entity.setVelocity(this.direction.multiply(this.power));
                return;
            }
        }
    }

    private void Spirals() {
        this.an += 20;
        if (this.an > 360)
            this.an = 0;
        for (int i = 0; i < 2; i++) {
            for (double d = -2.0D; d <= 0.0D;

                 d += 2.0D) {
                Location l = this.location.clone();
                double r = d * -1.0D / 5.0D;
                if (r > 2.0D)
                    r = 2.0D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, (this.an + 180 * i) + d, r);
                Location pl = l.clone().add(ov.clone());


                switch (i) {
                    case 0:

                        playLightningbendingParticle(pl, 0, 0, 0);
                        playLightningbendingParticle(pl, 0.1, 0.1, 0.1);
                        break;
                    case 1:

                        playLightningbendingParticle(pl, 0, 0, 0);
                        playLightningbendingParticle(pl, 0.1, 0.1, 0.1);
                        break;
                }
            }
        }
    }

    private void Sparks(int points, float size, int speed) {
        for (int i = 0; i < speed; i++) {
            this.currPoint += 360 / points;
            if (this.currPoint > 360)
                this.currPoint = 0;
            double angle = this.currPoint * Math.PI / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);
            Location loc = this.player.getLocation().add(x, 0.75D, z);
            playLightningbendingParticle(loc, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void remove() {
        super.remove();
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Fire.Combos.BoltStream.Enabled");
    }

    public String getName() {
        return "BoltStream";
    }

    public String getDescription() {
        return "With this ability combination, a master Lightningbender is able to summon multiple bolts of Lightning from the sky, following a line!";
    }

    public String getInstructions() {
        return "- ElectricDischarge (Hold-Shift) > Lightning (Left-Click 2x) -";
    }

    @Override
    public Object createNewComboInstance(Player player) {
        return new BoltStream(player);
    }

    @Override
    public ArrayList<ComboManager.AbilityInformation> getCombination() {
        ArrayList<ComboManager.AbilityInformation> gf = new ArrayList<>();
        gf.add(new ComboManager.AbilityInformation("ElectricDischarge", ClickType.SHIFT_DOWN));
        gf.add(new ComboManager.AbilityInformation("Lightning", ClickType.LEFT_CLICK));
        gf.add(new ComboManager.AbilityInformation("Lightning", ClickType.LEFT_CLICK));
        return gf;
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

}
