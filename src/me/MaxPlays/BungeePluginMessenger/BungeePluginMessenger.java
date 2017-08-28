package me.MaxPlays.BungeePluginMessenger;

import me.MaxPlays.BungeePluginMessenger.listeners.ChannelListener;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Created by MaxPlays on 28.08.2017.
 */
public class BungeePluginMessenger extends Plugin{

    public static BungeePluginMessenger instance;

    public void onEnable(){
        instance = this;

        Servers.startUpdatingTask();

        BungeeCord.getInstance().getPluginManager().registerListener(this, new ChannelListener());
        BungeeCord.getInstance().registerChannel("BungeePluginMsg");
    }

}
