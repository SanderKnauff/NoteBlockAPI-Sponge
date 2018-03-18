package com.xxmicloxx.NoteBlockAPI;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class SongEndEvent extends AbstractEvent {

    private final Cause cause;
    private final SongPlayer song;

    public SongEndEvent(SongPlayer song, Cause cause) {
        this.song = song;
        this.cause = cause;
    }

    public SongPlayer getSongPlayer() {
        return song;
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
