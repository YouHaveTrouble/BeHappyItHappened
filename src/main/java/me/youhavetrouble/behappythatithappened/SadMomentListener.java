package me.youhavetrouble.behappythatithappened;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class SadMomentListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSadMoment(EntityDeathEvent event) {

        if (event.getEntityType().equals(EntityType.PLAYER)) return;
        String inMemoryOf = event.getEntity().getCustomName();
        if (inMemoryOf == null) return;

        event.getDrops().forEach(memento -> {
            ItemMeta respect = memento.getItemMeta();
            if (respect.hasDisplayName()) return;
            respect.setDisplayName(inMemoryOf);
            memento.setItemMeta(respect);
        });

    }

}
