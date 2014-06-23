package me.johnnywoof.protocollib;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import me.johnnywoof.NoHack;
import me.johnnywoof.Setting;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class HackChecker {

	private ProtocolManager promanager;
	
	private NoHack nh;
	
	private HashMap<Integer, Integer> pendingAttacks = new HashMap<Integer, Integer>();
	
	public HackChecker(NoHack nh){
		
		this.nh = nh;
		
		this.promanager = ProtocolLibrary.getProtocolManager();
		
		nh.getServer().getScheduler().runTaskTimer(nh, new Runnable(){

			@Override
			public void run() {
				
				pendingAttacks.clear();
				
				Random r = new Random();
				
				for(Player p : Bukkit.getOnlinePlayers()){
					
					Location l = p.getLocation();
        			
					if(Setting.killmode == 1){
					
	        			l.setYaw((p.getEyeLocation().getYaw() * -1));
	        			l.setPitch((p.getEyeLocation().getPitch() * -1));
	        			
	        			BlockIterator it = new BlockIterator(l, 0, 2);
	        			
	        			while(it.hasNext()){
	        				
	        				Block b = it.next();
	        				
	        				//Spice it up with randomness
	        				l.setX(b.getX() + r.nextDouble());
	        				l.setY(b.getY() + r.nextDouble());
	        				l.setZ(b.getZ() + r.nextDouble());
	        				
	        			}
	        			
	        			it = null;
        			
					}else{
						
						if(l.getPitch() < 0){//Looking up, spawn it under
							
							l.setY(p.getLocation().getY() - 2);
							
						}else{//Looking down, spawn it above
							
							l.setY(p.getEyeLocation().getY() + 1);
							
						}
						
						//Spice it up with randomness
						l.setX(l.getX() + r.nextDouble());
        				l.setY(l.getY() + r.nextDouble());
        				l.setZ(l.getZ() + r.nextDouble());
						
					}
					
					pendingAttacks.put(p.getEntityId(), spawnNpc(false, p, l));
					
				}
				
				destroyEntities();
				
			}
			
		}, 100, 60);//3600
		
		this.promanager.addPacketListener(
                new PacketAdapter(nh, WrapperPlayClientUseEntity.TYPE) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                    	
                        if (event.getPacketType() == WrapperPlayClientUseEntity.TYPE) {
                        	WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());
                        	
                        	if(packet.getMouse() == EntityUseAction.ATTACK){
                        	
	                        	Iterator<Entry<Integer, Integer>> it = pendingAttacks.entrySet().iterator();
	                        	
	                        	while(it.hasNext()){
	                        		
	                        		Entry<Integer, Integer> en = it.next();
	                        		
	                        		if(en.getValue() == packet.getTargetID() && en.getKey() == event.getPlayer().getEntityId()){
	                        			
	                        			if(Setting.killban){
	                        				
	                        				 Bukkit.getBanList(BanList.Type.NAME).addBan(event.getPlayer().getName(), "AutoBanned - Using KillAura", null, "Server");
	                        				
	                        				 event.getPlayer().kickPlayer("AutoBanned - Using KillAura");
	                        				 
	                        			}else{
	                        				
	                        				for(Player o : Bukkit.getOnlinePlayers()){
	                        					
	                        					if(o.isOp() || o.hasPermission("nohack.notification.killaura")){
	                        						
	                        						o.sendMessage(ChatColor.RED + "[NoHack] " + ChatColor.LIGHT_PURPLE + "" + event.getPlayer().getName() + " could be using kill aura!");
	                        						
	                        					}
	                        					
	                        				}
	                        				
	                        			}
	                        			
	                        			try {
	            				        	promanager.sendServerPacket(event.getPlayer(), kill(en.getValue()).getHandle());
	            				        } catch (InvocationTargetException e) {
	            				            e.printStackTrace();
	            				        }
	                        			
	                        			it.remove();
	                        			
	                        		}
	                        		
	                        	}
	                        	
                        	}
                        	
                        }
                    }

                });
		
	}
	
	public void destroyEntities(){
		
		nh.getServer().getScheduler().runTaskLater(nh, new Runnable(){

			@Override
			public void run() {
				
				for(Entry<Integer, Integer> en : pendingAttacks.entrySet()){
					
					Player p = getPlayerByID(en.getKey());
					
					if(p != null){
						
						try {
				        	promanager.sendServerPacket(p, kill(en.getValue()).getHandle());
				        } catch (InvocationTargetException e) {
				            e.printStackTrace();
				        }
						
					}
					
				}
				
				pendingAttacks.clear();
				
			}
			
		}, 10);
		
	}
	
	public Player getPlayerByID(int id){
		
		for(Player p : Bukkit.getOnlinePlayers()){
			
			if(p.getEntityId() == id){
				
				return p;
				
			}
			
		}
		
		return null;
		
	}
	
	public WrapperPlayServerEntityDestroy kill(int entity) {
        WrapperPlayServerEntityDestroy wrapper = new WrapperPlayServerEntityDestroy();
        wrapper.setEntities(new int[]{entity});
        return wrapper;
    }
	
	private int spawnNpc(boolean invisible, Player p, Location loc){
		Random r = new Random();
		 WrapperPlayServerNamedEntitySpawn wrapper = new WrapperPlayServerNamedEntitySpawn();
	     wrapper.setEntityID(r.nextInt(20000));
	     wrapper.setPosition(loc.toVector());
	     wrapper.setPlayerUUID(UUID.randomUUID().toString());
	     OfflinePlayer[] ps = Bukkit.getOfflinePlayers();
         wrapper.setPlayerName(ps[new Random().nextInt(ps.length - 1)].getName());
         ps = null;
	     wrapper.setYaw((float) (r.nextInt(179) - 10));
	     wrapper.setPitch((float) (r.nextInt(180) - 90));
	     WrappedDataWatcher watcher = new WrappedDataWatcher();
	     watcher.setObject(0, invisible ? (Byte) (byte) 0x20 : (byte) 0);
	     watcher.setObject(6, (Float)(float) 0.5);
	     watcher.setObject(11, (Byte)(byte) 1);
	     
	     wrapper.setMetadata(watcher);
        
        try {
        	this.promanager.sendServerPacket(p, wrapper.getHandle());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        
        return wrapper.getEntityID();
        
	}
	
}
