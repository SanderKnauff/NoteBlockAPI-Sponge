package com.xxmicloxx.NoteBlockAPI;

import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.entity.living.player.Player;

public class RadioSongPlayer extends SongPlayer {

    public RadioSongPlayer(Song song) {
        super(song);
    }

    public RadioSongPlayer(Song song, SoundCategory soundCategory) {
        super(song, soundCategory);
    }

    @Override
    public void playTick(Player player, int tick) {
        byte playerVolume = NoteBlockPlayerMain.getPlayerVolume(player);

        for (Layer l : song.getLayerHashMap().values()) {
            Note note = l.getNote(tick);
            if (note == null) {
                continue;
            }
            player.playSound(Instrument.getInstrument(note.getInstrument()),
                    soundCategory,
                    player.getLocation().getPosition().add(0, 1, 0),
                    (l.getVolume() * (int) volume * (int) playerVolume) / 1000000f / 2f,
                    NotePitch.getPitch(note.getKey() - 33)
            );
        }
    }
}
