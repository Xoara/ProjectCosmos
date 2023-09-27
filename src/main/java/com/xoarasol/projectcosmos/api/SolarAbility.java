package com.xoarasol.projectcosmos.api;

import com.xoarasol.projectcosmos.PCElement;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.SubAbility;
import org.bukkit.entity.Player;

public abstract class SolarAbility extends CosmicAbility implements SubAbility {
    public SolarAbility(Player player) {
        super(player);
    }

    @Override
    public Class<? extends Ability> getParentAbility() {
        return CosmicAbility.class;
    }

    @Override
    public Element getElement() {
        return PCElement.SOLAR;
    }
}

