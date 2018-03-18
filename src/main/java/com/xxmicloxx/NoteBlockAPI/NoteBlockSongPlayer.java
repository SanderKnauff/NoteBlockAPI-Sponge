package com.xxmicloxx.NoteBlockAPI;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class NoteBlockSongPlayer extends SongPlayer {
    private Location<World> noteBlock;
    private int distance = 16;

    public NoteBlockSongPlayer(Song song) {
        super(song);
    }

    public NoteBlockSongPlayer(Song song, SoundCategory soundCategory) {
        super(song, soundCategory);
    }

    public Location<World> getNoteBlock() {
        return noteBlock;
    }

    @Override
    public void playTick(Player player, int tick) {
        if (!noteBlock.getBlockType().equals(BlockTypes.NOTEBLOCK)) {
            return;
        }
        if (!player.getWorld().equals(noteBlock.getExtent())) {
            // not in same world
            return;
        }
        byte playerVolume = NoteBlockPlayerMain.getPlayerVolume(player);

        for (Layer l : song.getLayerHashMap().values()) {
            Note note = l.getNote(tick);
            if (note == null) {
                continue;
            }

            player.playSound(Instrument.getInstrument(note.getInstrument()),
                    soundCategory,
                    noteBlock.getPosition(),
                    ((l.getVolume() * (int) volume * (int) playerVolume) / 1000000f) * ((1f / 16f) * distance),
                    NotePitch.getPitch(note.getKey() - 33)
            );

            if (isPlayerInRange(player)) {
                if (!this.playerList.contains(player.getUniqueId())) {
                    playerList.add(player.getUniqueId());
                    PlayerRangeStateChangeEvent event = new PlayerRangeStateChangeEvent(this, true, Cause.builder().append(player).build(EventContext.empty()));
                    Sponge.getEventManager().post(event);
                }
            } else {
                if (this.playerList.contains(player.getUniqueId())) {
                    playerList.remove(player.getUniqueId());
                    PlayerRangeStateChangeEvent event = new PlayerRangeStateChangeEvent(this, false, Cause.builder().append(player).build(EventContext.empty()));
                    Sponge.getEventManager().post(event);
                }
            }
        }
    }

    /**
     * Sets distance in blocks where would be player able to hear sound.
     *
     * @param distance (Default 16 blocks)
     */
    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isPlayerInRange(Player p) {
        if (p.getLocation().getPosition().distance(noteBlock.getPosition()) > distance) {
            return false;
        } else {
            return true;
        }
    }
}
