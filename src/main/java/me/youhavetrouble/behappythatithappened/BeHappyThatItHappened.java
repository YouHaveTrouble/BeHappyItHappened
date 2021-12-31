package me.youhavetrouble.behappythatithappened;

import org.bukkit.plugin.java.JavaPlugin;

public final class BeHappyThatItHappened extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new SadMomentListener(), this);
    }

}
