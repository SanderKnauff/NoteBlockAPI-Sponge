package com.xxmicloxx.NoteBlockAPI;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

import java.util.*;

@Plugin(id = "noteblockapi", name = "@name@", version = "@version@", description = "@description@")
public class NoteBlockPlayerMain {

    public static NoteBlockPlayerMain plugin;

    public Map<UUID, ArrayList<SongPlayer>> playingSongs = Collections.synchronizedMap(new HashMap<UUID, ArrayList<SongPlayer>>());
    public Map<UUID, Byte> playerVolume = Collections.synchronizedMap(new HashMap<UUID, Byte>());

    private boolean disabling = false;

    public static boolean isReceivingSong(Player p) {
        return ((plugin.playingSongs.get(p.getUniqueId()) != null) && (!plugin.playingSongs.get(p.getUniqueId()).isEmpty()));
    }

    public static void stopPlaying(Player p) {
        if (plugin.playingSongs.get(p.getUniqueId()) == null) {
            return;
        }
        for (SongPlayer s : plugin.playingSongs.get(p.getUniqueId())) {
            s.removePlayer(p);
        }
    }

    public static void setPlayerVolume(Player p, byte volume) {
        plugin.playerVolume.put(p.getUniqueId(), volume);
    }

    public static byte getPlayerVolume(Player p) {
        Byte b = plugin.playerVolume.get(p.getUniqueId());
        if (b == null) {
            b = 100;
            plugin.playerVolume.put(p.getUniqueId(), b);
        }
        return b;
    }

    @Listener
    public void onGameServerStartedEvent(GameStartedServerEvent gameStartedServerEvent) {
        plugin = this;
    }

    @Listener
    public void onGameServerStoppingEvent(GameStoppingServerEvent gameStoppingServerEvent) {
        disabling = true;
        Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
    }

    public void doSync(Runnable r) {
        Sponge.getScheduler().createTaskBuilder().execute(r).submit(this);
    }

    public void doAsync(Runnable r) {
        Sponge.getScheduler().createTaskBuilder().execute(r).async().submit(this);
    }

    protected boolean isDisabling() {
        return disabling;
    }

}
