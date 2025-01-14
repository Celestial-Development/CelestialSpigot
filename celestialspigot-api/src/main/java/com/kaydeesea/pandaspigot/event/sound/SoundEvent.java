package com.kaydeesea.pandaspigot.event.sound;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a sound is about to be played.
 */
public class SoundEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    /**
     * -- GETTER --
     * <p>
     *
     * -- SETTER --
     *  Set the location the sound will be played at.
     *
     */
    @Setter
    @Getter
    private Location location;
    /**
     * -- GETTER --
     *
     * @return The sound that will be played.
     * <p>
     * -- SETTER --
     *  Set the sound that will be played.
     *
     * @param sound The new sound.

     */
    @Setter
    @Getter
    private String sound;
    /**
     * -- GETTER --
     *
     *
     * -- SETTER --
     *  Set the sound's volume.
     *
     @return The sound's volume.
      * @param volume The sound's new volume.
     */
    @Setter
    @Getter
    private float volume;
    /**
     * -- GETTER --
     *
     *
     * -- SETTER --
     *  Set the sound's pitch.
     *
     @return The sound's pitch.
      * @param pitch The sound's new pitch.
     */
    @Setter
    @Getter
    private float pitch;
    private boolean cancelled;
    
    public SoundEvent(Location location, String sound, float volume, float pitch) {
        this.location = location;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
