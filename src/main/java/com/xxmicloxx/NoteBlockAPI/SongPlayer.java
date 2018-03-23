package com.xxmicloxx.NoteBlockAPI;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class SongPlayer {

    private final Lock lock = new ReentrantLock();

    protected Song song;
    protected boolean playing = false;
    protected short tick = -1;
    protected List<UUID> playerList = new ArrayList<>();
    protected boolean loop;
    protected boolean autoDestroy = false;
    protected boolean destroyed = false;
    protected Thread playerThread;
    protected byte fadeTarget = 100;
    protected byte volume = 100;
    protected byte fadeStart = volume;
    protected int fadeDuration = 60;
    protected int fadeDone = 0;
    protected FadeType fadeType = FadeType.FADE_LINEAR;
    protected SoundCategory soundCategory;

    public SongPlayer(Song song) {
        this(song, SoundCategories.BLOCK);
    }

    public SongPlayer(Song song, SoundCategory soundCategory) {
        this.song = song;
        this.soundCategory = soundCategory;
        createThread();
    }

    public FadeType getFadeType() {
        return fadeType;
    }

    public void setFadeType(FadeType fadeType) {
        this.fadeType = fadeType;
    }

    public byte getFadeTarget() {
        return fadeTarget;
    }

    public void setFadeTarget(byte fadeTarget) {
        this.fadeTarget = fadeTarget;
    }

    public byte getFadeStart() {
        return fadeStart;
    }

    public void setFadeStart(byte fadeStart) {
        this.fadeStart = fadeStart;
    }

    public int getFadeDuration() {
        return fadeDuration;
    }

    public void setFadeDuration(int fadeDuration) {
        this.fadeDuration = fadeDuration;
    }

    public int getFadeDone() {
        return fadeDone;
    }

    public void setFadeDone(int fadeDone) {
        this.fadeDone = fadeDone;
    }

    protected void calculateFade() {
        if (fadeDone == fadeDuration) {
            return; // no fade today
        }
        double targetVolume = Interpolator.interpLinear(new double[]{0, fadeStart, fadeDuration, fadeTarget}, fadeDone);
        setVolume((byte) targetVolume);
        fadeDone++;
    }

    protected void createThread() {
        playerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!destroyed) {
                    long startTime = System.currentTimeMillis();
                    synchronized (SongPlayer.this) {
                        if (playing) {
                            calculateFade();
                            tick++;
                            if (tick > song.getLength()) {
                                if (loop) {
                                    tick = 0;
                                    continue;
                                }
                                playing = false;
                                tick = -1;
                                SongEndEvent event = new SongEndEvent(SongPlayer.this, Cause.builder().append(this).build(EventContext.empty()));
                                Sponge.getEventManager().post(event);
                                if (autoDestroy) {
                                    destroy();
                                    return;
                                }
                            }
                            for (UUID uuid : playerList) {
                                Sponge.getServer().getPlayer(uuid).ifPresent(player -> {
                                    playTick(player, tick);
                                });
                            }
                        }
                    }
                    long duration = System.currentTimeMillis() - startTime;
                    float delayMillis = song.getDelay() * 50;
                    if (duration < delayMillis) {
                        try {
                            Thread.sleep((long) (delayMillis - duration));
                        } catch (InterruptedException e) {
                            // do nothing
                        }
                    }
                }
            }
        });
        playerThread.setPriority(Thread.MAX_PRIORITY);
        playerThread.start();
    }

    public List<UUID> getPlayerList() {
        return Collections.unmodifiableList(playerList);
    }

    public void addPlayer(Player p) {
        synchronized (this) {
            if (!playerList.contains(p.getUniqueId())) {
                playerList.add(p.getUniqueId());
                ArrayList<SongPlayer> songs = NoteBlockPlayerMain.plugin.playingSongs
                        .get(p.getUniqueId());
                if (songs == null) {
                    songs = new ArrayList<SongPlayer>();
                }
                songs.add(this);
                NoteBlockPlayerMain.plugin.playingSongs.put(p.getUniqueId(), songs);
            }
        }
    }

    public void setLoop(boolean loop) {
        lock.lock();
        try {
            this.loop = loop;
        } finally {
            lock.unlock();
        }
    }

    public boolean isLoop() {
        lock.lock();
        try {
            return loop;
        } finally {
            lock.unlock();
        }
    }

    public boolean getAutoDestroy() {
        synchronized (this) {
            return autoDestroy;
        }
    }

    public void setAutoDestroy(boolean value) {
        synchronized (this) {
            autoDestroy = value;
        }
    }

    public abstract void playTick(Player p, int tick);

    public void destroy() {
        synchronized (this) {
            SongDestroyingEvent event = new SongDestroyingEvent(this, Cause.builder().append(this).build(EventContext.empty()));
            Sponge.getEventManager().post(event);
            //Bukkit.getScheduler().cancelTask(threadId);
            if (event.isCancelled()) {
                return;
            }
            destroyed = true;
            playing = false;
            setTick((short) -1);
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
        if (!playing) {
            SongStoppedEvent event = new SongStoppedEvent(this, Cause.builder().append(this).build(EventContext.empty()));
            Sponge.getEventManager().post(event);
        }
    }

    public short getTick() {
        return tick;
    }

    public void setTick(short tick) {
        this.tick = tick;
    }

    public void removePlayer(Player p) {
        synchronized (this) {
            playerList.remove(p.getUniqueId());
            if (NoteBlockPlayerMain.plugin.playingSongs.get(p.getUniqueId()) == null) {
                return;
            }
            ArrayList<SongPlayer> songs = new ArrayList<SongPlayer>(
                    NoteBlockPlayerMain.plugin.playingSongs.get(p.getUniqueId()));
            songs.remove(this);
            NoteBlockPlayerMain.plugin.playingSongs.put(p.getUniqueId(), songs);
            if (playerList.isEmpty() && autoDestroy) {
                SongEndEvent event = new SongEndEvent(this, Cause.builder().append(this).build(EventContext.empty()));
                Sponge.getEventManager().post(event);
                destroy();
            }
        }
    }

    public byte getVolume() {
        return volume;
    }

    public void setVolume(byte volume) {
        this.volume = volume;
    }

    public Song getSong() {
        return song;
    }
}
