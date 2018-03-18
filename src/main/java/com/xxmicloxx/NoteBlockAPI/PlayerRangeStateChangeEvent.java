package com.xxmicloxx.NoteBlockAPI;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class PlayerRangeStateChangeEvent extends AbstractEvent {

    private final Cause cause;
    private final SongPlayer song;
    private Boolean state;

    public PlayerRangeStateChangeEvent(SongPlayer song, Boolean state, Cause cause) {
        this.song = song;
        this.state = state;
        this.cause = cause;
    }

    public SongPlayer getSongPlayer() {
        return song;
    }

    public Boolean isInRange() {
        return state;
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
