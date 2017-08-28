package me.MaxPlays.BungeePluginMessenger.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.MaxPlays.BungeePluginMessenger.Servers;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;
import java.util.UUID;

/**
 * Created by MaxPlays on 28.08.2017.
 */
public class ChannelListener implements Listener {

    @EventHandler
    public void onMessage(PluginMessageEvent e){
        if(e.getTag().equalsIgnoreCase("BungeePluginMsg")){
            ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
            String channel = in.readUTF();
            ServerInfo server = BungeeCord.getInstance().getPlayer(e.getReceiver().toString()).getServer().getInfo();
            if(channel.equalsIgnoreCase("getServers")){

                JsonArray array = new JsonArray();
                for(Map.Entry<String, Integer> entry: Servers.servers.entrySet()){
                    JsonObject o = new JsonObject();
                    o.addProperty("name", entry.getKey());
                    o.addProperty("online", entry.getValue() >= 0);
                    o.addProperty("count", entry.getValue());
                    array.add(o);
                }

                send(channel, array.toString(), server);

            }else if(channel.equalsIgnoreCase("getPlayers")){

                JsonArray array = new JsonArray();
                for(ProxiedPlayer p: BungeeCord.getInstance().getPlayers()){
                    JsonObject o = new JsonObject();
                    o.addProperty("name", p.getName());
                    o.addProperty("uuid", p.getUniqueId().toString());
                    o.addProperty("server", p.getServer().getInfo().getName());
                    array.add(o);
                }

                send(channel, array.toString(), server);

            }else if(channel.equalsIgnoreCase("getPlayerCount")){

                send(channel, String.valueOf(BungeeCord.getInstance().getPlayers().size()), server);

            }else if(channel.equalsIgnoreCase("getPlayer")){

                JsonObject o = new JsonObject();
                String name = in.readUTF();
                ProxiedPlayer p = BungeeCord.getInstance().getPlayer(name);

                o.addProperty("name", name);
                o.addProperty("online", p != null);
                if(p != null){
                    o.addProperty("server", p.getServer().getInfo().getName());
                    o.addProperty("uuid", p.getUniqueId().toString());
                    o.addProperty("ping", p.getPing());
                    o.addProperty("ip", p.getAddress().getHostString());
                }

                send(channel, o.toString(), server);

            }else if(channel.equalsIgnoreCase("getServer")){

                ServerInfo info = BungeeCord.getInstance().getServerInfo(in.readUTF());

                JsonObject o = new JsonObject();
                if(info != null){
                    o.addProperty("name", info.getName());
                    o.addProperty("online", Servers.servers.get(info.getName()) >= 0);
                    if(Servers.servers.get(info.getName()) >= 0) {
                        o.addProperty("motd", Servers.motds.get(info.getName()));
                        StringBuilder sb = new StringBuilder();
                        int i = 0;
                        for(ProxiedPlayer p: info.getPlayers()){
                            sb.append(p.getName());
                            if(i < (info.getPlayers().size() - 1))
                                sb.append(", ");
                            i++;
                        }
                        o.addProperty("players", sb.toString());
                        o.addProperty("count", info.getPlayers().size());
                    }
                }
                send(channel, o.toString(), server);
            }else if(channel.equalsIgnoreCase("kickPlayer")){
                ProxiedPlayer p = BungeeCord.getInstance().getPlayer(in.readUTF());
                if(p != null)
                    p.disconnect(TextComponent.fromLegacyText(in.readUTF()));
            }else if(channel.equalsIgnoreCase("sendMessage")){
                ProxiedPlayer p = BungeeCord.getInstance().getPlayer(in.readUTF());
                if(p != null)
                    p.sendMessage(TextComponent.fromLegacyText(in.readUTF()));
            }else if(channel.equalsIgnoreCase("broadcast")){
                String permission = in.readUTF();
                BaseComponent[] bc = TextComponent.fromLegacyText(in.readUTF());
                for(ProxiedPlayer p: BungeeCord.getInstance().getPlayers()){
                    if(p.hasPermission(permission))
                        p.sendMessage(bc);
                }
            }else if(channel.equalsIgnoreCase("broadcastMessage")){
                BaseComponent[] bc = TextComponent.fromLegacyText(in.readUTF());
                BungeeCord.getInstance().broadcast(bc);
            }else if(channel.equalsIgnoreCase("kickPlayerUUID")){
                ProxiedPlayer p = BungeeCord.getInstance().getPlayer(UUID.fromString(in.readUTF()));
                if(p != null)
                    p.disconnect(TextComponent.fromLegacyText(in.readUTF()));
            }
        }
    }

    public static void send(String channel, String message, ServerInfo info){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(channel);
        out.writeUTF(message);
        info.sendData("BungeePluginMsg", out.toByteArray());
    }

}
