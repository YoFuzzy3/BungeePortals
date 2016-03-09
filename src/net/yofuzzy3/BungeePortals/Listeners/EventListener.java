package net.yofuzzy3.BungeePortals.Listeners;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.yofuzzy3.BungeePortals.BungeePortals;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventListener implements Listener {

    private BungeePortals plugin;
    private Map<String, Boolean> statusData = new HashMap<>();

    public EventListener(BungeePortals plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) throws IOException {
        Player player = event.getPlayer();
        String playerName = player.getName();
        if (!statusData.containsKey(playerName)) {
            statusData.put(playerName, false);
        }
        Block block = player.getWorld().getBlockAt(player.getLocation());
        String data = block.getWorld().getName() + "#" + String.valueOf(block.getX()) + "#" + String.valueOf(block.getY()) + "#" + String.valueOf(block.getZ());
        if (plugin.portalData.containsKey(data)) {
            if (!statusData.get(playerName)) {
                statusData.put(playerName, true);
                String destination = plugin.portalData.get(data);
                if (player.hasPermission("BungeePortals.portal." + destination) || player.hasPermission("BungeePortals.portal.*")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(baos);
                    dos.writeUTF("Connect");
                    dos.writeUTF(destination);
                    player.sendPluginMessage(plugin, "BungeeCord", baos.toByteArray());
                    baos.close();
                    dos.close();
                } else {
                    player.sendMessage(plugin.configFile.getString("NoPortalPermissionMessage").replace("{destination}", destination).replaceAll("(&([a-f0-9l-or]))", "\u00A7$2"));
                }
            }
        } else {
            if (statusData.get(playerName)) {
                statusData.put(playerName, false);
            }
        }
    }

}
