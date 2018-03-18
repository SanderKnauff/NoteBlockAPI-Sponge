package com.xxmicloxx.NoteBlockAPI;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class SongDestroyingEvent extends AbstractEvent implements Cancellable {

    private final SongPlayer song;
    private final Cause cause;
    private boolean cancelled = false;

    public SongDestroyingEvent(SongPlayer song, Cause cause) {
        this.song = song;
        this.cause = cause;
    }

    public SongPlayer getSongPlayer() {
        return song;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        cancelled = arg0;
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
