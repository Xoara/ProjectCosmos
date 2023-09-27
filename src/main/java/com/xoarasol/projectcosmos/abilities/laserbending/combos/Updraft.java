package com.xoarasol.projectcosmos.abilities.laserbending.combos;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.LaserAbility;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class Updraft extends LaserAbility implements ComboAbility, AddonAbility {
    double addedY;
    Location origin;
    private long cooldown;
    private int fireTicks;
    private double damage;
    private int knockup;

    private int blueCounter;
    private boolean hasReached = false;
    Random rand = new Random();

    public Updraft(Player player) {
        super(player);
        addedY = 0;
        origin = player.getLocation().add(0,.7,0);

        this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Combos.Updraft.Cooldown");
        this.fireTicks = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Combos.Updraft.FireTicks");
        this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.Updraft.Damage");
        this.knockup = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Combos.Updraft.KnockUp");

        this.blueCounter = rand.nextInt(49) / 5;

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 5, 1.86f);
        start();
    }

    @Override
    public void progress() {
        addedY+=0.1;
        if(addedY > 3){
            remove();
        }
        if(addedY < .25){
            drawRing(1);
            return;
        }
        if(addedY < .50){
            drawRing(2);
            return;
        }
        if(addedY < .75){
            drawRing(3);
            return;
        }
        if(addedY < 1){
            drawRing(4);
            return;
        }
        if(addedY < 1.25){
            drawRing(5);
            return;
        }
        if(addedY < 1.50){
            drawRing(6);
            return;
        }
        if(addedY > 1.50){
            drawRing(7);
            return;
        }
    }

    @Override
    public void remove() {
        bPlayer.addCooldown(this,getCooldown());
        super.remove();
    }

    private void drawRing(int r){
        double x;
        double y =  origin.getY() + addedY;
        double z;
        Location loc = origin;
        for (double i = 0.0; i < 360.0; i+=10) { //0.1
            double angle = i * Math.PI / 180;
            x = (loc.getX() + r * Math.cos(angle));
            z = (loc.getZ() + r * Math.sin(angle));

            if (this.hasReached) {
                this.blueCounter -= 5;
                if (this.blueCounter <= 0) {
                    this.blueCounter = 0;
                    this.hasReached = false;
                }
            } else {
                this.blueCounter += 5;
                if (this.blueCounter >= 255) {
                    this.blueCounter = 255;
                    this.hasReached = true;
                }
            }
            new ColoredParticle(Color.fromRGB(255, 0, this.blueCounter), 2f).display(new Location(loc.getWorld(),x,y,z), 3, 0, 0, 0);
            this.player.setFallDistance(0.0F);
            for(Entity en : loc.getWorld().getNearbyEntities(new Location(loc.getWorld(),x,y,z),.5,.5,.5)){
                if(en instanceof LivingEntity){
                    if(en.getUniqueId() != player.getUniqueId()){
                        en.setFireTicks(fireTicks);
                        DamageHandler.damageEntity(en, damage, this);
                        en.setFallDistance(0.0F);
                        GeneralMethods.setVelocity(this, en, new Vector(0,knockup,0));
                    }
                }
            }
        }
    }

    @Override
    public boolean isSneakAbility() {
        return false;
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
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public String getName() {
        return "Updraft";
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public String getDescription() {
        return "This is an intricate Laser-Combo as is used as crowd control against enemies. " +
                "With this ability you can send out deadly uppercutting rays of pure laser, knocking enemies in the air!";
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return "Lubdan & XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }

    @Override
    public Object createNewComboInstance(Player player) {
        if(!BendingPlayer.getBendingPlayer(player).isOnCooldown(CoreAbility.getAbility(Updraft.class))){
            if(!CoreAbility.hasAbility(player,Updraft.class)){
                return new Updraft(player);
            }
        }
        return null;
    }

    @Override
    public String getInstructions() {
        return "- LaserFission (Left-Click 2x) > Boost (Tap-Shift) -";
    }

    @Override
    public ArrayList<ComboManager.AbilityInformation> getCombination() {
        ArrayList<ComboManager.AbilityInformation> combo = new ArrayList<>();
        combo.add(new ComboManager.AbilityInformation("LaserFission", ClickType.LEFT_CLICK));
        combo.add(new ComboManager.AbilityInformation("LaserFission", ClickType.LEFT_CLICK));
        combo.add(new ComboManager.AbilityInformation("Boost", ClickType.SHIFT_DOWN));
        return combo;
    }
}
