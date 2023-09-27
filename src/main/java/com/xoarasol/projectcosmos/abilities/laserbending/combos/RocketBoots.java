package com.xoarasol.projectcosmos.abilities.laserbending.combos;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.LaserAbility;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class RocketBoots extends LaserAbility implements ComboAbility, AddonAbility {
    int timer = 0;
    public RocketBoots(Player player) {
        super(player);
        start();
    }

    @Override
    public void progress() {
        timer++;
        if(timer > 5){
            bPlayer.addCooldown(this,4000);
            remove();
            return;
        }
        player.setFallDistance(0);
        if(ThreadLocalRandom.current().nextInt(0,2)==0){
            player.setVelocity(new Vector(0,.1,0));
        }

        ParticleEffect.FIREWORKS_SPARK.display(GeneralMethods.getRightSide(this.player.getLocation().add(0,-.5,0), 0.55).add(this.player.getVelocity().clone()), 2, 0, 0, 0, 0.04f);
        ParticleEffect.FIREWORKS_SPARK.display(GeneralMethods.getLeftSide(this.player.getLocation().add(0,-.5,0), 0.55).add(this.player.getVelocity().clone()), 2, 0, 0, 0, 0.04f);

        (new ColoredParticle(Color.fromRGB(255, 0, 140), 3.5F)).display(GeneralMethods.getRightSide(this.player.getLocation().add(0,-.5,0), 0.55).add(this.player.getVelocity().clone()), 2, 0, 0, 0);
        (new ColoredParticle(Color.fromRGB(255, 0, 140), 3.5F)).display(GeneralMethods.getLeftSide(this.player.getLocation().add(0,-.5,0), 0.55).add(this.player.getVelocity().clone()), 2, 0, 0, 0);

    }

    @Override
    public boolean isSneakAbility() {
        return false;
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
    public long getCooldown() {
        return 3000;
    }

    @Override
    public String getDescription() {
        return "Laserbenders can use their energy to negate fall damage and reduce their knockback!";
    }

    @Override
    public String getInstructions() {
        return "- Boost (Tap-Shift) > LaserFission (Tap-Shift) -";
    }

    @Override
    public String getName() {
        return "RocketBoots";
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
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
        BendingPlayer bplayer = BendingPlayer.getBendingPlayer(player);
        if(bplayer.canBendIgnoreBinds(this)){
            return new RocketBoots(player);
        } else{
            return null;
        }
    }

    @Override
    public ArrayList<ComboManager.AbilityInformation> getCombination() {
        ArrayList<ComboManager.AbilityInformation> combo = new ArrayList<>();
        combo.add(new ComboManager.AbilityInformation("Boost", ClickType.SHIFT_DOWN));
        combo.add(new ComboManager.AbilityInformation("Boost", ClickType.SHIFT_UP));
        combo.add(new ComboManager.AbilityInformation("LaserFission", ClickType.SHIFT_DOWN));
        return combo;
    }
}
