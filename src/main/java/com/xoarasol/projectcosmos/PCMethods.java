package com.xoarasol.projectcosmos;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class PCMethods {

    public static boolean generalBendCheck(CoreAbility ability, Player player) {
        if (ability == null) {
            System.out.println(ability.toString() + "failed for being null");
            return false;
        }
        if (!player.isOnline() || player.isDead()) {
            System.out.println(ability.toString() + "failed the general alive/online check");
            return false;
        }
        BendingPlayer bplayer = BendingPlayer.getBendingPlayer(player);
        if (bplayer.isParalyzed() || bplayer.isChiBlocked() || bplayer.isBloodbent() || bplayer.isControlledByMetalClips()) {
            System.out.println(ability.toString() + "Failed the can't bend check");
            return false;
        }
        return true;
    }

    public static List<Location> drawSpherePoints(Location loc, Integer r, Integer h, Boolean hollow, Boolean sphere, int plus_y) {
        List<Location> circleblocks = new ArrayList<Location>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx + r; x++)
            for (int z = cz - r; z <= cz + r; z++)
                for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }

        return circleblocks;
    }

    /**
     * Will create a directional vector, pointing from loc1, to loc2.
     */

    public static Vector createDirectionalVector(Location loc1, Location loc2) {
        Double x,y,z;

        x = loc2.getX() - loc1.getX();
        y = loc2.getY() - loc1.getY();
        z = loc2.getZ() - loc1.getZ();

        return new Vector(x,y,z);
    }
}

