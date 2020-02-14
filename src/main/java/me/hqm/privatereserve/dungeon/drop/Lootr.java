package me.hqm.privatereserve.dungeon.drop;

import com.google.common.base.Joiner;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Lootr {
    String name;
    List<ItemStack> items;
    Map<String, Lootr> branches;
    List<String> branchNames;

    public Lootr(String name) {
        name = clean(name);

        if (name.indexOf('/') > -1) {
            throw new RuntimeException("Specified name should not contain a \"/\" separator.");
        }

        this.name = name;
        this.items = new ArrayList<>();
        this.branches = new HashMap<>();
    }

    private String clean(String path) {
        return path.replace("^\\/", "").replace("\\/$", "");
    }

    private double randomInRange(int... bounds) {
        switch (bounds.length) {
            case 0:
                bounds = new int[]{0, 5};
                break;
            case 1:
                bounds = new int[]{bounds[0], bounds[0] + 5};
                break;
            default:
                bounds = new int[]{bounds[0], bounds[bounds.length - 1]};
        }

        bounds[0] = Integer.parseInt(String.valueOf(bounds[0]), 0);
        bounds[1] = Integer.parseInt(String.valueOf(bounds[1]), 0);

        return Math.floor(Math.random() * (bounds[1] - bounds[0] + 1)) + bounds[0];
    }

    public Lootr add(ItemStack item) {
        items.add(item);
        return this;
    }

    public Lootr add(ItemStack item, String path) {
        Lootr branch = branch(path);
        branch.items.add(item);
        return this;
    }

    public Lootr branch(String name) {
        return getBranch(name, true);
    }

    public Lootr getBranch(String name, boolean create) {
        Deque<String> path = new ArrayDeque<>(Arrays.asList(clean(name).split("/")));

        if (!branches.containsKey(path.getFirst()) && path.getFirst().equals(this.name) && create) {
            branchNames.add(path.getFirst());
            branches.put(path.getFirst(), new Lootr(path.getFirst()));
        }

        if (path.size() == 1) {
            return path.getFirst().equals(this.name) ? this : branches.get(path.getFirst());
        } else if (path.size() > 1) {
            String head = path.pollFirst();
            String newPath = Joiner.on("/").join(path);

            if (branches.containsKey(head)) {
                return branches.get(head).getBranch(newPath, create);
            }

            if (create) {
                branchNames.add(head);
                branches.put(head, new Lootr(head));
                return branches.get(head).getBranch(newPath, true);
            }
        }

        return this;
    }

    public List<ItemStack> allItems() {
        List<ItemStack> items = new ArrayList<>(this.items);

        for (Lootr branch : branches.values()) {
            items.addAll(branch.allItems());
        }

        return items;
    }

    public ItemStack randomPick(int allowedNesting, Double threshold) {
        List<ItemStack> picked = new ArrayList<>();

        if (threshold == null) {
            threshold = 1.0;
        }

        if (Math.random() < threshold && items.size() > 0) {
            picked.add(items.get(~~(int) (Math.random() * items.size())));
        }

        if (allowedNesting > 0) {
            for (int i = 0; i < branches.size(); i++) {
                double nestedChance = Math.random();
                if (nestedChance <= threshold) {
                    ItemStack other = branches.get(this.branchNames.get(i)).
                            randomPick(allowedNesting - 1, threshold - Math.random() / allowedNesting);
                    if (other != null) {
                        picked.add(other);
                    }
                }
            }
        }

        return items.size() > 0 ? picked.get(~~(int) (Math.random() * picked.size())) : null;
    }

    public ItemStack roll(String catalogPath, int allowedNesting, Double threshold) {
        Lootr branch = getBranch(catalogPath, false);
        return branch.randomPick(allowedNesting, threshold == null ? 1.0 : threshold);
    }

    public List<ItemStack> loot(DropQuery drops) {
        List<ItemStack> rewards = new ArrayList<>();

        for (DropQuery.Entry drop : drops.query) {
            ItemStack item = roll(drop.from, drop.stack, drop.luck);
            if (item != null) {
                rewards.add(item);
            }
        }

        return rewards;
    }
}
