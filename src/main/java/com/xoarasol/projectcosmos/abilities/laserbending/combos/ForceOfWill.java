package com.xoarasol.projectcosmos.abilities.laserbending.combos;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.ForceFieldAbility;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class ForceOfWill extends ForceFieldAbility implements ComboAbility, AddonAbility {

    Location[] locations;
    Vector direction;

    private int distance;
    private int timer;
    private int duration;
    private int cooldown;
    private double damage;
    private long time;

    public ForceOfWill(Player player) {
        super(player);
        updateStreams();

        time = System.currentTimeMillis();
        distance = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Combos.ForceOfWill.Range");
        duration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Combos.ForceOfWill.Duration");
        timer = 0;
        damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.ForceOfWill.Damage");
        cooldown = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Combos.ForceOfWill.Cooldown");

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 3.5f, 0f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 3.5f, 0f);
        start();
    }

    private void updateStreams(){
        locations = new Location[3];
        locations[0] = player.getLocation();
        locations[1] = GeneralMethods.getLeftSide(player.getLocation(),1);
        locations[2] = GeneralMethods.getRightSide(player.getLocation(),1);
        this.direction = player.getLocation().getDirection();
    }

    @Override
    public void progress() {
        if(!player.isSneaking()){
            remove();
        }
        if (System.currentTimeMillis() > time + duration) {
            bPlayer.addCooldown(this);
            remove();
            return;
        }
        for(int j = 0; j < 3; j++){
            for(int i = 0; i < distance/2; i++){
                locations[j] = locations[j].add(direction);
                if(j == 0){

                    (new ColoredParticle(Color.fromRGB(116, 173, 211), 2.0F)).display(locations[j], 2, 0.2D, 0.2D, 0.2D);


                } else {
                    ParticleEffect.SPELL_INSTANT.display(locations[j], 1, 0.4, 0.4, 0.4, 0.04);
                    player.getWorld().playSound(locations[j], Sound.BLOCK_BEACON_POWER_SELECT, 0.1f, 0.75f);

                }
                for(Entity entity : locations[j].getWorld().getNearbyEntities(locations[j],1,1,1)){
                    if(entity instanceof LivingEntity){
                        if(entity.getUniqueId() == player.getUniqueId()){
                            continue;
                        }
                        DamageHandler.damageEntity(entity, damage ,this);

                        //ParticleEffect.BLOCK_CRACK.display(block.getLocation(),10,1,1,1,block.getType().data);
                    }
                }
            }
        }
        if(distance < 100){
            distance++;
        }
        timer++;
        if(timer > 20*7){
            remove();
        }
        updateStreams();
    }

    @Override
    public void remove() {
        bPlayer.addCooldown(this, cooldown);
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

    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.Combos.ForceOfWill.Enabled");
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public String getName() {
        return "ForceOfWill";
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }


    @Override
    public Object createNewComboInstance(Player player) {
        if(!BendingPlayer.getBendingPlayer(player).isOnCooldown(CoreAbility.getAbility(ForceOfWill.class))){
            if(!CoreAbility.hasAbility(player,ForceOfWill.class)){
                return new ForceOfWill(player);
            }
        }
        return null;
    }

    @Override
    public String getDescription() {
        return "Advanced ForceFieldbenders are able to channel the energy from within, and can attack enemies by sending a beam of force at them!";
    }

    @Override
    public String getInstructions() {
        return "- LaserFission (Left-Click) > VirtusVeil (Left-Click 2x) > Barrier (Hold Shift) -";
    }

    @Override
    public ArrayList<ComboManager.AbilityInformation> getCombination() {
        ArrayList<ComboManager.AbilityInformation> combo = new ArrayList<>();
        combo.add(new ComboManager.AbilityInformation("LaserFission", ClickType.LEFT_CLICK));
        combo.add(new ComboManager.AbilityInformation("VirtusVeil", ClickType.LEFT_CLICK));
        combo.add(new ComboManager.AbilityInformation("VirtusVeil", ClickType.LEFT_CLICK));
        combo.add(new ComboManager.AbilityInformation("Barrier", ClickType.SHIFT_DOWN));
        return combo;
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
