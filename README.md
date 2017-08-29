# BungeePluginMessenger
Utility that simplifies the exchange of data between BungeeCord and Bukkit

## Installation ##
1. Download the latest version of the plugin [here](https://github.com/MaxPlays/BungeePluginMessenger/releases/latest)
2. Stop your BungeeCord server
3. Drag and drop it into the folder "plugins" in the root of your BungeeCord server
4. Start your BungeeCord server

## Usage ##
This is a very simple tool that allows Bukkit plugins to get informations about other servers/players that are connected to the same BungeeCord network. It also allows Bukkit plugins to give certain instructions to the BungeeCord server that would otherwise require you to connect your plugins to a SQL server. See the documentation below for further instructions.

### Sending instructions ###
Note: This assumes that you have already followed the instructions in the installation part of this file. First, we have to tell our Bukkit plugins that we want to use the BukkitPluginMessenger. We accomplish that by inserting the following line into the onEnable method of the plugin:
```java
public void onEnable(){
  Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeePluginMsg");
}
```
We can now start sending instructions to the BungeeCord server.

#### Kick a player ####
```java
 ByteArrayDataOutput out = ByteStreams.newDataOutput();
 out.writeUTF("kickPlayer")
 out.writeUTF("PLAYERNAME");
 out.writeUTF("MESSAGE");
 
 Bukkit.getServer().sendPluginMessage(this /* (Instance of your main class) */, "BungeePluginMsg", out.toByteArray());
```

#### Kick a UUID ####
```java
 ByteArrayDataOutput out = ByteStreams.newDataOutput();
 out.writeUTF("kickPlayerUUID")
 out.writeUTF("UUID");
 out.writeUTF("MESSAGE");
 
 Bukkit.getServer().sendPluginMessage(this /* (Instance of your main class) */, "BungeePluginMsg", out.toByteArray());
```

#### Send a message ####
```java
  ByteArrayDataOutput out = ByteStreams.newDataOutput();
  out.writeUTF("sendMessage");
  out.writeUTF("PLAYERNAME");
  out.writeUTF("MESSAGE");
  
  Bukkit.getServer().sendPluginMessage(this /* (Instance of your main class) */, "BungeePluginMsg", out.toByteArray());
```

#### Broadcast to players with BungeeCord permission ####
```java
  ByteArrayDataOutput out = ByteStreams.newDataOutput();
  out.writeUTF("broadcast");
  out.writeUTF("PERMISSION");
  out.writeUTF("MESSAGE");
  
  Bukkit.getServer().sendPluginMessage(this /* (Instance of your main class) */, "BungeePluginMsg", out.toByteArray());
```

#### Broadcast to everyone ####
```java
  ByteArrayDataOutput out = ByteStreams.newDataOutput();
  out.writeUTF("broadcastMessage");
  out.writeUTF("MESSAGE");
  
  Bukkit.getServer().sendPluginMessage(this /* (Instance of your main class) */, "BungeePluginMsg", out.toByteArray());
```

### Querying data ###
To query data from the BungeeCord server, you will additionally need a listener that listens to the responses from the BungeeCord server. This listener must also be registered in the onEnable method. See the example below.
```java
  public class foo extends JavaPlugin implements PluginMessageListener {
    
    public void onEnable(){
      getServer().getMessenger().registerIncomingPluginChannel(this, "BungeePluginMsg", this /* Or an instance of a different class that implements the interface PluginMessageListener */);
    }
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] data) {
      if(s.equalsIgnoreCase("BungeePluginMsg")){
            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
            if(in.readUTF().equalsIgnoreCase("SUBCHANNEL")){
                String response = in.readUTF();
            }
        }
    } 
  }
```
Now we are ready to query data from our BungeeCord server.

#### Get a list of all servers ####
**Query**
```java
  ByteArrayDataOutput out = ByteStreams.newDataOutput();
  out.writeUTF("getServers");
  
  Bukkit.getServer().sendPluginMessage(this /* (Instance of your main class) */, "BungeePluginMsg", out.toByteArray());
```
**Example Response (JSON Array)**
```json
  [  
   {  
      "name":"Server1",
      "online":true,
      "count":10
   },
   {  
      "name":"Server2",
      "online":false,
      "count":-1
   }
]
```

#### Get a list of all players ####

**Query**
```java
  ByteArrayDataOutput out = ByteStreams.newDataOutput();
  out.writeUTF("getPlayers");
  
  Bukkit.getServer().sendPluginMessage(this /* (Instance of your main class) */, "BungeePluginMsg", out.toByteArray());
```
**Example Response (JSON Array)**
```json
[  
   {  
      "name":"Player1",
      "uuid":"UUID1",
      "server":"Server1"
   },
   {  
      "name":"Player2",
      "uuid":"UUID2",
      "server":"Server2"
   }
]
```

#### Get the number of online players ####

**Query**
```java
  ByteArrayDataOutput out = ByteStreams.newDataOutput();
  out.writeUTF("getPlayerCount");
  
  Bukkit.getServer().sendPluginMessage(this /* (Instance of your main class) */, "BungeePluginMsg", out.toByteArray());
```
**Example Response (Plain text)**
```
10
```

#### Get details about a player ####

**Query**
```java
  ByteArrayDataOutput out = ByteStreams.newDataOutput();
  out.writeUTF("getPlayer");
  out.writeUTF("PLAYERNAME");
  
  Bukkit.getServer().sendPluginMessage(this /* (Instance of your main class) */, "BungeePluginMsg", out.toByteArray());
```
**Example Response (JSON Object)**
```json
{  
   "name":"Player1",
   "online":true,
   "server":"Server1",
   "uuid":"UUID1",
   "ping":10,
   "ip":"127.0.0.1"
}
```
```json
{  
   "name":"Player2",
   "online":false
}
```

#### Get details about a server ####

**Query**
```java
  ByteArrayDataOutput out = ByteStreams.newDataOutput();
  out.writeUTF("getServer");
  out.writeUTF("SERVERNAME");
  
  Bukkit.getServer().sendPluginMessage(this /* (Instance of your main class) */, "BungeePluginMsg", out.toByteArray());
```
**Example Response (JSON Object)**
```json
{  
   "name":"Server1",
   "online":true,
   "motd":"Motd1",
   "players":"Player1, Player2, Player3",
   "count":3
}
```
```json
{  
   "name":"Server2",
   "online":false
}
```
If the server does not exist
```json
{}
```
