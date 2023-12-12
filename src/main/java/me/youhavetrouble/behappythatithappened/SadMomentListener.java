package me.youhavetrouble.behappythatithappened;

import io.papermc.paper.event.entity.TameableDeathMessageEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class SadMomentListener implements Listener {

    private final ItemMeta nametagMeta = new ItemStack(Material.NAME_TAG).getItemMeta();
    private final NamespacedKey inMemoryOfKey;
    private final BeHappyThatItHappened plugin;

    protected SadMomentListener(BeHappyThatItHappened plugin) {
        this.plugin = plugin;
        inMemoryOfKey = new NamespacedKey(plugin, "in-memory-of");
        nametagMeta.getPersistentDataContainer().set(inMemoryOfKey, PersistentDataType.STRING, "");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSadMoment(EntityDeathEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) return;
        if (!(event.getEntity() instanceof Mob)) return;
        if (event.getEntity() instanceof Tameable) {
            Tameable pet = (Tameable) event.getEntity();
            if (pet.isTamed()) return;
        }
        Component inMemoryOf = event.getEntity().customName();
        if (inMemoryOf == null) return;

        if (plugin.renameDrops) {
            event.getDrops().forEach(memento -> {
                ItemMeta respect = memento.getItemMeta();
                if (respect.hasDisplayName()) return;
                respect.displayName(inMemoryOf);
                memento.setItemMeta(respect);
            });
        }

        if (plugin.renamedMobsDropTag) {
            event.getDrops().add(getMemento(inMemoryOf, null));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSevereLossMoment(TameableDeathMessageEvent event) {
        if (!plugin.renamedMobsDropTag) return;
        if (!event.getEntity().isTamed()) return;
        ItemStack memento = getMemento(event.getEntity().customName(), event.deathMessage());
        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), memento);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLossMoment(PlayerDeathEvent event) {
        if (!plugin.playersDropTag) return;
        Player player = event.getEntity();
        ItemStack memento = getMemento(player.displayName(), event.deathMessage());
        event.getDrops().add(memento);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDesecration(PrepareAnvilEvent event) {
        ItemStack left = event.getInventory().getFirstItem();
        if (left == null) return;
        if (!Material.NAME_TAG.equals(left.getType())) return;
        ItemMeta meta = left.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(inMemoryOfKey, PersistentDataType.STRING)) return;
        event.setResult(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDesecration(PlayerInteractEntityEvent event) {
        ItemStack held = event.getPlayer().getInventory().getItemInMainHand();
        if (!Material.NAME_TAG.equals(held.getType())) return;
        ItemMeta meta = held.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(inMemoryOfKey, PersistentDataType.STRING)) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDesecration(EntityDamageEvent event) {
        if (!(event.getEntityType().equals(EntityType.DROPPED_ITEM))) return;
        if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) return;
        ItemStack item = ((org.bukkit.entity.Item) event.getEntity()).getItemStack();
        if (!Material.NAME_TAG.equals(item.getType())) return;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(inMemoryOfKey, PersistentDataType.STRING)) return;
        event.setCancelled(true);
    }

    private ItemStack getMemento(Component name, Component message) {
        ItemStack nametag = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = nametagMeta;
        meta.displayName(Component.text("In memory of ").append(name)
                .decoration(TextDecoration.ITALIC, false)
                .color(NamedTextColor.WHITE));
        if (message != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(message
                    .decoration(TextDecoration.ITALIC, false)
                    .color(NamedTextColor.GRAY)
            );
            meta.lore(lore);
        } else {
            meta.lore(null);
        }
        nametag .setItemMeta(meta);
        return nametag;
    }

}
