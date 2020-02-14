package me.hqm.privatereserve.dungeon.drop;

import me.hqm.privatereserve.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

public class Loot {
    private static final Lootr EQUIPMENT = new Lootr("/equipment");

    static {
        EQUIPMENT.branch("weapons").add(new ItemStackBuilder(Material.DIAMOND_AXE)
                .displayName(ChatColor.RED + "Blood Axe")
                .itemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .lore(ChatColor.RED + "An axe made from zombie blood.")
                .enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.DAMAGE_ALL, 2)
                .build());
    }

    private Loot() {
    }

    public static Lootr getEquipment() {
        return EQUIPMENT;
    }
}
