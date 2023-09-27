package com.xoarasol.projectcosmos.api;

import com.xoarasol.projectcosmos.PCElement;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.SubAbility;
import org.bukkit.entity.Player;

public abstract class RadiationAbility extends LaserAbility implements SubAbility {
    public RadiationAbility(Player player) {
        super(player);
    }

    @Override
    public Class<? extends Ability> getParentAbility() {
        return LaserAbility.class;
    }

    @Override
    public Element getElement() {
        return PCElement.RADIATION;
    }
}
