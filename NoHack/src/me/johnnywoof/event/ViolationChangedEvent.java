package me.johnnywoof.event;

import me.johnnywoof.check.CheckType;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class ViolationChangedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Player p;
    private int ol, nl;
    private CheckType ct;
    private boolean cancelled;
 
    /**Constructor for violation change event
     * @param new level
     * @param old level
     * @param check type
     * @param The player*/
    public ViolationChangedEvent(int newlevel, int oldlevel, CheckType ct, Player p){
        this.p = p;
        this.ct = ct;
        this.ol = oldlevel;
        this.nl = newlevel;
    }
 
    /**Returns the current new violation level
     * @return The new violation level*/
    public int getNewLevel(){
    	return this.nl;
    }
    
    /**Returns the type of check the violation level changed for
     * @return The check type*/
    public CheckType getCheckType(){
    	return this.ct;
    }
    
    /**Returns the player responsible for raising the violation
     * @return The player*/
    public Player getPlayer(){
    	return this.p;
    }
    
    /**Sets the new violation level
     * @param the new level*/
    public void setNewLevel(int v){
    	this.nl = v;
    }
    
    /**Returns the previous level
     * @return the old violation level*/
    public int getOldLevel(){
    	return this.ol;
    }
 
    /**If canceled, the violation level will not increase and not notify admins
     * @return If canceled*/
    public boolean isCancelled() {
        return cancelled;
    }
 
    /**If canceled, the violation level will not increase and not notify admins
     * @param canceled state*/
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
 
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}