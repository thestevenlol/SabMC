package me.jackfoley.sab;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import me.jackfoley.sab.util.CFUtils;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ListIterator;

@CommandAlias("chatfilter|cf")
public class ChatFilter extends BaseCommand implements Listener {

    // todo make whitelist & blacklist requests

    @Subcommand("exclusion review")
    @CommandPermission("sab.admin")
    public void reviewExclusion(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 45, Color.chat("&dExclusion Requests"));
        CFUtils.getExclusionRequests(requests -> {
            if (requests.isEmpty()) {
                player.sendMessage(Color.chat(Messages.prefix + "&cThere is no exclusion requests to view."));
                return;
            }
            for (int i = 0; i < 45; i++) {
                ItemStack item = new ItemStack(Material.PAPER);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(Color.chat("&d") + requests.get(i));
                item.setItemMeta(meta);
                inventory.setItem(i, item);
            }
            Bukkit.getScheduler().runTask(Main.getPlugin(), () -> player.openInventory(inventory));
        });
    }

    @Subcommand("blacklist review")
    @CommandPermission("sab.admin")
    public void reviewBlacklist(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 45, Color.chat("&dBlacklist Requests"));
        CFUtils.getBlacklistRequests(requests -> {
            if (requests.isEmpty()) {
                player.sendMessage(Color.chat(Messages.prefix + "&cThere is no blacklist requests to view."));
                return;
            }
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = new ItemStack(Material.PAPER);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(Color.chat("&d") + requests.get(i));
                item.setItemMeta(meta);
                inventory.setItem(i, item);
                Bukkit.getScheduler().runTask(Main.getPlugin(), () -> player.openInventory(inventory));
            }
        });
    }


    private Inventory confirm(String type) {
        Inventory confirm = Bukkit.createInventory(null, 9, Color.chat("&dConfirm " + type + "?"));
        ItemStack yes = new ItemStack(Material.LIME_WOOL);
        ItemStack no = new ItemStack(Material.RED_WOOL);
        ItemMeta yesMeta = yes.getItemMeta();
        ItemMeta noMeta = no.getItemMeta();
        yesMeta.setDisplayName(Color.chat("&aConfirm"));
        noMeta.setDisplayName(Color.chat("&cDeny"));
        yes.setItemMeta(yesMeta);
        no.setItemMeta(noMeta);
        confirm.setItem(3, yes);
        confirm.setItem(5, no);
        return confirm;
    }

    @EventHandler
    public void inventoryShitIDK(InventoryClickEvent e) {
        if (e.getView().getTitle().equals(Color.chat("&dExclusion Requests")) || e.getView().getTitle().equals(Color.chat("&dBlacklist Requests"))) {
            e.setCancelled(true);
            if (e.getView().getTitle().equals(Color.chat("&dExclusion Requests"))) {
                if (e.getCurrentItem() == null) return;
                if (e.getCurrentItem().getType().equals(Material.AIR)) return;
                if (e.getCurrentItem().getType().equals(Material.PAPER)) {
                    String word = Color.strip(e.getCurrentItem().getItemMeta().getDisplayName());
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().openInventory(confirm("Exclusion"));
                    Main.getCfMap().remove(e.getWhoClicked().getUniqueId());
                    Main.getCfMap().put(e.getWhoClicked().getUniqueId(), word);
                }
                return;
            }

            if (e.getView().getTitle().equals(Color.chat("&dBlacklist Requests"))) {
                if (e.getCurrentItem() == null) return;
                if (e.getCurrentItem().getType().equals(Material.AIR)) return;
                if (e.getCurrentItem().getType().equals(Material.PAPER)) {
                    String word = Color.strip(e.getCurrentItem().getItemMeta().getDisplayName());
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().openInventory(confirm("Blacklist"));
                    Main.getCfMap().remove(e.getWhoClicked().getUniqueId());
                    Main.getCfMap().put(e.getWhoClicked().getUniqueId(), word);;
                }
            }
        }
    }

    @EventHandler
    public void confirmIt(InventoryClickEvent e) {
        if (e.getView().getTitle().equals(Color.chat("&dConfirm Exclusion?"))) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType().equals(Material.AIR)) return;
            if (e.getCurrentItem().getType().equals(Material.LIME_WOOL)) {
                String word = Main.getCfMap().get(e.getWhoClicked().getUniqueId());
                e.getWhoClicked().closeInventory();
                Bukkit.dispatchCommand(e.getWhoClicked(), "cf blacklist add " + word);
                CFUtils.remove("exclusion", word);
                Main.getCfMap().remove(e.getWhoClicked().getUniqueId());
            } else if (e.getCurrentItem().getType().equals(Material.RED_WOOL)) {
                String word = Main.getCfMap().get(e.getWhoClicked().getUniqueId());
                e.getWhoClicked().closeInventory();
                Main.getCfMap().remove(e.getWhoClicked().getUniqueId());
                CFUtils.remove("exclusion", word);
            }
        } else if (e.getView().getTitle().equals(Color.chat("&dConfirm Blacklist?"))) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType().equals(Material.AIR)) return;
            if (e.getCurrentItem().getType().equals(Material.RED_WOOL)) {
                String word = Main.getCfMap().get(e.getWhoClicked().getUniqueId());
                e.getWhoClicked().closeInventory();
                Main.getCfMap().remove(e.getWhoClicked().getUniqueId());
                CFUtils.remove("blacklist", word);
            } else if (e.getCurrentItem().getType().equals(Material.LIME_WOOL)) {
                String word = Main.getCfMap().get(e.getWhoClicked().getUniqueId());
                e.getWhoClicked().closeInventory();
                Bukkit.dispatchCommand(e.getWhoClicked(), "cf blacklist add " + word);
                CFUtils.remove("blacklist", word);
                Main.getCfMap().remove(e.getWhoClicked().getUniqueId());
            }
        }
    }

    @Subcommand("exclusion request")
    @Description("Request for a word to be excluded from the chat filter.")
    public void requestWhitelist(Player player) {
        List<String> exclusions = Main.getPlugin().getConfig().getStringList("bad-word-exclusions");
        new AnvilGUI.Builder().onComplete((p, text) -> {
            if (exclusions.contains(text)) return AnvilGUI.Response.text("That word is already excluded!");

            CFUtils.exclusionRequestExists(text, exists -> {
                if (!exists) {
                    Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
                        try (PreparedStatement st = Main.getSql().createStatement("INSERT INTO sab_requests (UUID,TYPE,WORD) VALUES (?,?,?)")) {
                            st.setString(1, p.getUniqueId().toString());
                            st.setString(2, "exclusion");
                            st.setString(3, text);
                            st.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    player.sendMessage(Color.chat(Messages.prefix + "&7Word sent for review, thank you!"));
                    return;
                }
                player.sendMessage(Color.chat(Messages.prefix + "&cThat word is already being requested for exclusion."));
            });

            return AnvilGUI.Response.close();
        })
                .title("Word Exclusion")
                .plugin(Main.getPlugin())
                .itemLeft(new ItemStack(Material.PAPER))
                .text("Enter here")
                .open(player);
    }


    @Subcommand("blacklist request")
    @Description("Request for a word to be blacklisted.")
    public void requestBlacklist(Player player) {
        List<String> blacklisted = Main.getPlugin().getConfig().getStringList("bad-words");
        new AnvilGUI.Builder().onComplete((p, text) -> {
            if (blacklisted.contains(text)) return AnvilGUI.Response.text("That word is already blacklisted!");

            CFUtils.blacklistRequestExists(text, exists -> {
                if (!exists) {
                    Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
                        try (PreparedStatement st = Main.getSql().createStatement("INSERT INTO sab_requests (UUID,TYPE,WORD) VALUES (?,?,?)")) {
                            st.setString(1, p.getUniqueId().toString());
                            st.setString(2, "blacklist");
                            st.setString(3, text);
                            st.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    player.sendMessage(Color.chat(Messages.prefix + "&7Word sent for review, thank you!"));
                    return;
                }
                player.sendMessage(Color.chat(Messages.prefix + "&cThat word is already being requested for blacklisting."));
            });

            return AnvilGUI.Response.close();
        })
                .title("Word Blacklist")
                .plugin(Main.getPlugin())
                .itemLeft(new ItemStack(Material.PAPER))
                .text("Enter here")
                .open(player);
    }


    @Subcommand("blacklist add")
    @CommandPermission("sab.admin")
    public void addBlacklist(Player player, String[] args) {
        String word = args[0];
        List<String> list = Main.getPlugin().getConfig().getStringList("bad-words");
        for (String s : list) {
            if (word.equalsIgnoreCase(s)) {
                player.sendMessage(Color.chat(Messages.prefix + "&cThe word " + Color.strip(word) + " is already in the blacklist."));
                return;
            }
        }
        ListIterator<String> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            listIterator.set(listIterator.next().toLowerCase());
        }
        list.add(word);
        Main.getPlugin().getConfig().set("bad-words", list);
        Main.getPlugin().saveConfig();
        player.sendMessage(Color.chat(Messages.prefix + "&7The word " + Color.strip(word) + " has been added to the blacklist."));
    }

    @Subcommand("blacklist remove")
    @CommandPermission("sab.admin")
    public void removeBlacklist(Player player, String[] args) {
        String word = args[0];
        List<String> list = Main.getPlugin().getConfig().getStringList("bad-words");
        boolean exists = list.stream().anyMatch(word::equalsIgnoreCase);
        if (!exists) {
            player.sendMessage(Color.chat(Messages.prefix + "&cThe word " + Color.strip(word) + " is not in the blacklist."));
            return;
        }
        ListIterator<String> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            listIterator.set(listIterator.next().toLowerCase());
        }
        list.remove(word);
        Main.getPlugin().getConfig().set("bad-words", list);
        Main.getPlugin().saveConfig();
        player.sendMessage(Color.chat(Messages.prefix + "&7The word " + Color.strip(word) + " has been removed from the blacklist."));
    }

    @Subcommand("exclusion add")
    @CommandPermission("sab.admin")
    public void addExclusion(Player player, String[] args) {
        String word = args[0];
        List<String> list = Main.getPlugin().getConfig().getStringList("bad-word-exclusions");
        for (String s : list) {
            if (word.equalsIgnoreCase(s)) {
                player.sendMessage(Color.chat(Messages.prefix + "&cThe word " + Color.strip(word) + " is already in the exclusions list."));
                return;
            }
        }
        ListIterator<String> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            listIterator.set(listIterator.next().toLowerCase());
        }
        list.add(word);
        Main.getPlugin().getConfig().set("bad-word-exclusions", list);
        Main.getPlugin().saveConfig();
        player.sendMessage(Color.chat(Messages.prefix + "&7The word " + Color.strip(word) + " has been added to the exclusions list."));
    }

    @Subcommand("exclusion remove")
    @CommandPermission("sab.admin")
    public void removeExclusion(Player player, String[] args) {
        String word = args[0];
        List<String> list = Main.getPlugin().getConfig().getStringList("bad-word-exclusions");
        boolean exists = list.stream().anyMatch(word::equalsIgnoreCase);
        if (!exists) {
            player.sendMessage(Color.chat(Messages.prefix + "&cThe word " + Color.strip(word) + " is not in the exclusions list."));
            return;
        }
        ListIterator<String> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            listIterator.set(listIterator.next().toLowerCase());
        }
        list.remove(word);
        Main.getPlugin().getConfig().set("bad-word-exclusions", list);
        Main.getPlugin().saveConfig();
        player.sendMessage(Color.chat(Messages.prefix + "&7The word " + Color.strip(word) + " has been removed from the exclusions list."));
    }

}
