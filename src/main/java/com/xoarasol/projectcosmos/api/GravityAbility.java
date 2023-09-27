package com.xoarasol.projectcosmos.api;

import com.xoarasol.projectcosmos.PCElement;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.SubAbility;
import org.bukkit.entity.Player;

public abstract class GravityAbility extends CosmicAbility implements SubAbility {

    public GravityAbility(Player player) {
        super(player);
    }

    @Override
    public Class<? extends Ability> getParentAbility() {
        return CosmicAbility.class;
    }

    @Override
    public Element getElement() {
        return PCElement.GRAVITY;
    }
}
