package me.youhavetrouble.behappythatithappened;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class BeHappyThatItHappened extends JavaPlugin {

    protected boolean renameDrops = true;
    protected boolean renamedMobsDropTag = false;
    protected boolean playersDropTag = false;

    @Override
    public void onEnable() {
        reloadSadness();
        getServer().getPluginManager().registerEvents(new SadMomentListener(this), this);
        PluginCommand command = getCommand("bhihreload");
        if (command == null) {
            getLogger().warning("You messed with the plugin.yml, didn't you?");
            return;
        }
        command.setExecutor(new ReloadCommand(this));
    }

    protected void reloadSadness() {
        saveDefaultConfig();
        renameDrops = getConfig().getBoolean("named-entity.rename-drops", true);
        renamedMobsDropTag = getConfig().getBoolean("named-entity.drop-nametag", false);
        playersDropTag = getConfig().getBoolean("player.drop-nametag", false);
    }

}
