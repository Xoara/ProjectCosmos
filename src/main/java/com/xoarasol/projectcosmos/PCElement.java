package com.xoarasol.projectcosmos;


import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.ElementType;
import com.projectkorra.projectkorra.Element.SubElement;
import com.projectkorra.projectkorra.ProjectKorra;
import net.md_5.bungee.api.ChatColor;

public class PCElement {

    public static final Element COSMIC;
    public static final SubElement SOLAR;
    public static final SubElement LUNAR;
    public static final SubElement GRAVITY;
    public static final SubElement DARK_COSMIC;

    public PCElement() {
    }

    static {
        COSMIC = new Element("Cosmic", ElementType.BENDING, ProjectKorra.plugin) {

            @Override
            public ChatColor getColor() {
                return ChatColor.of("#7700ff");
            }
        };

        SOLAR = new SubElement("Solar", PCElement.COSMIC, ElementType.BENDING, ProjectKorra.plugin){

            @Override
            public ChatColor getColor() {
                return ChatColor.of("#ffd907");
            }
        };

        LUNAR = new SubElement("Lunar", PCElement.COSMIC, ElementType.BENDING, ProjectKorra.plugin){

            @Override
            public ChatColor getColor() {
                return ChatColor.of("#7c77ff");
            }
        };

        DARK_COSMIC = new SubElement("DarkCosmic", PCElement.COSMIC, ElementType.BENDING, ProjectKorra.plugin){

            @Override
            public ChatColor getColor() {
                return ChatColor.of("#280168");
            }
        };

        GRAVITY = new SubElement("Gravity", PCElement.COSMIC, ElementType.BENDING, ProjectKorra.plugin){

            @Override
            public ChatColor getColor() {
                return ChatColor.of("#7e67c9");
            }
        };
    }
}
