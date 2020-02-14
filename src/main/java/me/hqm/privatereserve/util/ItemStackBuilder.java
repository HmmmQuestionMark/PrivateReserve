package me.hqm.privatereserve.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;

public class ItemStackBuilder {

    // -- DATA -- //

    private ItemStack item;
    private ItemMeta meta;

    // -- CONSTRUCTORS -- //

    public ItemStackBuilder(Material type) {
        item = new ItemStack(type);
        meta = item.getItemMeta();
    }

    public ItemStackBuilder(MaterialData data) {
        item = new ItemStack(data.getItemType());
        item.setData(data);
        meta = item.getItemMeta();
    }

    // -- SETTERS -- //

    public ItemStackBuilder type(Material type) {
        item.setType(type);
        return this;
    }

    public ItemStackBuilder data(MaterialData data) {
        item.setData(data);
        return this;
    }

    public ItemStackBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemStackBuilder durability(short durability) {
        item.setDurability(durability);
        return this;
    }

    public ItemStackBuilder unbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemStackBuilder enchant(Enchantment enchant, int level) {
        item.addEnchantment(enchant, level);
        return this;
    }

    public ItemStackBuilder unsafeEnchant(Enchantment enchant, int level) {
        item.addUnsafeEnchantment(enchant, level);
        return this;
    }

    public ItemStackBuilder itemFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    public ItemStackBuilder displayName(String displayName) {
        meta.setDisplayName(displayName);
        return this;
    }

    public ItemStackBuilder lore(String... lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    // -- BUILD -- //

    public ItemStack build() {
        ItemStack item = this.item.clone();
        item.setItemMeta(meta);
        return item;
    }
}
