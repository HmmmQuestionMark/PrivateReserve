package me.hqm.privatereserve.dungeon.drop;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class Loot {
    private static final Lootr EQUIPMENT = new Lootr("/equipment");

    static {
        ItemStack BLOOD_AXE = ItemStack.of(Material.DIAMOND_AXE);
        BLOOD_AXE.setData(DataComponentTypes.ITEM_NAME, Component.text("Blood Axe", NamedTextColor.RED));
        BLOOD_AXE.setData(DataComponentTypes.LORE, ItemLore.lore().addLine(Component.text("An axe made from zombie blood.", NamedTextColor.RED)));
        BLOOD_AXE.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.SHARPNESS, 2).add(Enchantment.UNBREAKING, 4).build());
        EQUIPMENT.branch("weapons").add(BLOOD_AXE);
    }

    private Loot() {
    }

    public static Lootr getEquipment() {
        return EQUIPMENT;
    }
}
