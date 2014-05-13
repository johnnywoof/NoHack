package me.johnnywoof.check;

import me.johnnywoof.event.ViolationChangedEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Violation {

	private int fly, vs, hs, impossible, sb, ns, gm, crit, fr, fspeed, abl, fp, timer, spam, fc, as, v, nf, is, gl, fb, nkb;
	private long lastnotification = 0;
	
	public boolean resetLevel(CheckType ct, Player p){
		
		ViolationChangedEvent vce = new ViolationChangedEvent(0, this.getLevel(ct), ct, p);
		
		Bukkit.getPluginManager().callEvent(vce);
		
		if(vce.isCancelled()){
			return true;
		}
		
		switch(ct){
		case AIMBOT:
			this.abl = vce.getNewLevel();
			break;
		case ATTACK_REACH:
			this.fr = vce.getNewLevel();
			break;
		case ATTACK_SPEED:
			this.fspeed = vce.getNewLevel();
			break;
		case CRITICAL:
			this.crit = vce.getNewLevel();
			break;
		case FASTPLACE:
			this.fp = vce.getNewLevel();
			break;
		case FAST_THROW:
			this.fp = vce.getNewLevel();
			break;
		case FLY:
			this.fly = vce.getNewLevel();
			break;
		case GOD_MODE:
			this.gm = vce.getNewLevel();
			break;
		case HORIZONTAL_SPEED:
			this.hs = vce.getNewLevel();
			break;
		case IMPOSSIBLE:
			this.impossible = vce.getNewLevel();
			break;
		case NOSWING:
			this.ns = vce.getNewLevel();
			break;
		case SPEED_BREAK:
			this.sb = vce.getNewLevel();
			break;
		case VERTICAL_SPEED:
			this.vs = vce.getNewLevel();
			break;
		case TIMER:
			this.timer = vce.getNewLevel();
			break;
		case SPAM:
			this.spam = vce.getNewLevel();
			break;
		case FASTCLICK:
			this.fc = vce.getNewLevel();
			break;
		case AUTOSOUP:
			this.as = vce.getNewLevel();
			break;
		case VISIBLE:
			this.v = vce.getNewLevel();
			break;
		case NOFALL:
			this.nf = vce.getNewLevel();
			break;
		case FAST_INTERACT:
			this.is = vce.getNewLevel();
			break;
		case GLIDE:
			this.gl = vce.getNewLevel();
			break;
		case FULLBRIGHT:
			this.fb = vce.getNewLevel();
			break;
		case NOKNOCKBACK:
			this.nkb = vce.getNewLevel();
			break;
		default:
			break;
	}
		
		return false;
		
	}
	
	public int getLevel(CheckType ct){
		
		switch(ct){
			case AIMBOT:
				return this.abl;
			case ATTACK_REACH:
				return this.fr;
			case ATTACK_SPEED:
				return this.fspeed;
			case CRITICAL:
				return this.crit;
			case FASTPLACE:
				return this.fp;
			case FAST_THROW:
				return this.fp;
			case FLY:
				return this.fly;
			case GOD_MODE:
				return this.gm;
			case HORIZONTAL_SPEED:
				return this.hs;
			case IMPOSSIBLE:
				return this.impossible;
			case NOSWING:
				return this.ns;
			case SPEED_BREAK:
				return this.sb;
			case VERTICAL_SPEED:
				return this.vs;
			case TIMER:
				return this.timer;
			case SPAM:
				return this.spam;
			case FASTCLICK:
				return this.fc;
			case AUTOSOUP:
				return this.as;
			case VISIBLE:
				return this.v;
			case NOFALL:
				return this.nf;
			case FAST_INTERACT:
				return this.is;
			case GLIDE:
				return this.gl;
			case FULLBRIGHT:
				return this.fb;
			case NOKNOCKBACK:
				return this.nkb;
			default:
				break;
		}
		
		return 0;
		
	}
	
	public boolean raiseLevel(CheckType ct, Player p){
		
		ViolationChangedEvent vce = new ViolationChangedEvent(this.getLevel(ct) + 1, this.getLevel(ct), ct, p);
		
		Bukkit.getPluginManager().callEvent(vce);
		
		if(vce.isCancelled()){
			return true;
		}
		
		switch(ct){
			case AIMBOT:
				this.abl = vce.getNewLevel();
				break;
			case ATTACK_REACH:
				this.fr = vce.getNewLevel();
				break;
			case ATTACK_SPEED:
				this.fspeed = vce.getNewLevel();
				break;
			case CRITICAL:
				this.crit = vce.getNewLevel();
				break;
			case FASTPLACE:
				this.fp = vce.getNewLevel();
				break;
			case FAST_THROW:
				this.fp = vce.getNewLevel();
				break;
			case FLY:
				this.fly = vce.getNewLevel();
				break;
			case GOD_MODE:
				this.gm = vce.getNewLevel();
				break;
			case HORIZONTAL_SPEED:
				this.hs = vce.getNewLevel();
				break;
			case IMPOSSIBLE:
				this.impossible = vce.getNewLevel();
				break;
			case NOSWING:
				this.ns = vce.getNewLevel();
				break;
			case SPEED_BREAK:
				this.sb = vce.getNewLevel();
				break;
			case VERTICAL_SPEED:
				this.vs = vce.getNewLevel();
				break;
			case TIMER:
				this.timer = vce.getNewLevel();
				break;
			case SPAM:
				this.spam = vce.getNewLevel();
				break;
			case FASTCLICK:
				this.fc = vce.getNewLevel();
				break;
			case AUTOSOUP:
				this.as = vce.getNewLevel();
				break;
			case VISIBLE:
				this.v = vce.getNewLevel();
				break;
			case NOFALL:
				this.nf = vce.getNewLevel();
				break;
			case FAST_INTERACT:
				this.is = vce.getNewLevel();
				break;
			case GLIDE:
				this.gl = vce.getNewLevel();
				break;
			case FULLBRIGHT:
				this.fb = vce.getNewLevel();
				break;
			case NOKNOCKBACK:
				this.nkb = vce.getNewLevel();
				break;
			default:
				break;
		}
		
		return false;
		
	}
	
	public void updateNotify(){
		this.lastnotification = System.currentTimeMillis();
	}
	
	public boolean shouldNotify(){
		
		return (System.currentTimeMillis() - this.lastnotification) >= 4000;
		
	}
	
}
