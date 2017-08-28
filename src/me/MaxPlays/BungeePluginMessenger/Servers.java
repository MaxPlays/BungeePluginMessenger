package me.MaxPlays.BungeePluginMessenger;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by MaxPlays on 28.08.2017.
 */
public class Servers {

    public static HashMap<String, Integer> servers = new HashMap<>();
    public static HashMap<String, String> motds = new HashMap<>();

    public static void startUpdatingTask(){
        BungeeCord.getInstance().getScheduler().schedule(BungeePluginMessenger.instance, new Runnable() {
            @Override
            public void run() {
                for(Map.Entry<String, ServerInfo> e: BungeeCord.getInstance().getServers().entrySet()){
                    e.getValue().ping(new Callback<ServerPing>() {
                        @Override
                        public void done(ServerPing serverPing, Throwable throwable) {
                            if(servers.containsKey(e.getKey()))
                                servers.remove(e.getKey());
                            if(motds.containsKey(e.getKey()))
                                motds.remove(e.getKey());
                            if(throwable == null) {
                                servers.put(e.getKey(), serverPing.getPlayers().getOnline());
                                motds.put(e.getKey(), serverPing.getDescription());
                            } else {
                                servers.put(e.getKey(), -1);
                                motds.put(e.getKey(), "");
                            }
                        }
                    });
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

}
