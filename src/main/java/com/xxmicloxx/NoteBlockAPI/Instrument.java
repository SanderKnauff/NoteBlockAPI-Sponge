package com.xxmicloxx.NoteBlockAPI;

import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;

public class Instrument {

    public static SoundType getInstrument(byte instrument) {

        switch (instrument) {
            case 0:
                return SoundTypes.BLOCK_NOTE_HARP;
            case 1:
                return SoundTypes.BLOCK_NOTE_BASS;
            case 2:
                return SoundTypes.BLOCK_NOTE_BASEDRUM;
            case 3:
                return SoundTypes.BLOCK_NOTE_SNARE;
            case 4:
                return SoundTypes.BLOCK_NOTE_HAT;
            case 5:
                return SoundTypes.BLOCK_NOTE_GUITAR;
            case 6:
                return SoundTypes.BLOCK_NOTE_FLUTE;
            case 7:
                return SoundTypes.BLOCK_NOTE_BELL;
            case 8:
                return SoundTypes.BLOCK_NOTE_CHIME;
            case 9:
                return SoundTypes.BLOCK_NOTE_XYLOPHONE;
        }
        return SoundTypes.BLOCK_NOTE_HARP;
    }
}
