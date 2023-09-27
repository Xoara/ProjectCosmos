package com.xoarasol.projectcosmos.api;

import com.xoarasol.projectcosmos.PCElement;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.ElementalAbility;
import org.bukkit.entity.Player;

public abstract class CosmicAbility extends ElementalAbility {

    public CosmicAbility(Player player) {
        super(player);
    }

    @Override
    public Element getElement() {
        return PCElement.COSMIC;
    }

}
