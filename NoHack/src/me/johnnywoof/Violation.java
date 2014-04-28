package me.johnnywoof;

public class Violation {

	private int fly, vs, hs, impossible, sb, ns, gm, crit, fr, fspeed, abl, fp, timer, spam, fc, as, v, nf, is, gl, fb;
	private long lastnotification = 0;
	
	public void resetLevel(CheckType ct){
		
		switch(ct){
		case AIMBOT:
			this.abl = 0;
			break;
		case ATTACK_REACH:
			this.fr = 0;
			break;
		case ATTACK_SPEED:
			this.fspeed = 0;
			break;
		case CRITICAL:
			this.crit = 0;
			break;
		case FASTPLACE:
			this.fp = 0;
			break;
		case FAST_THROW:
			this.fp = 0;
			break;
		case FLY:
			this.fly = 0;
			break;
		case GOD_MODE:
			this.gm = 0;
			break;
		case HORIZONTAL_SPEED:
			this.hs = 0;
			break;
		case IMPOSSIBLE:
			this.impossible = 0;
			break;
		case NOSWING:
			this.ns = 0;
			break;
		case SPEED_BREAK:
			this.sb = 0;
			break;
		case VERTICAL_SPEED:
			this.vs = 0;
			break;
		case TIMER:
			this.timer = 0;
			break;
		case SPAM:
			this.spam = 0;
			break;
		case FASTCLICK:
			this.fc = 0;
			break;
		case AUTOSOUP:
			this.as = 0;
			break;
		case VISIBLE:
			this.v = 0;
			break;
		case NOFALL:
			this.nf = 0;
			break;
		case FAST_INTERACT:
			this.is = 0;
			break;
		case GLIDE:
			this.gl = 0;
			break;
		case FULLBRIGHT:
			this.fb = 0;
			break;
		default:
			break;
	}
		
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
			default:
				break;
		}
		
		return 0;
		
	}
	
	public void raiseLevel(CheckType ct){
		
		switch(ct){
			case AIMBOT:
				this.abl++;
				break;
			case ATTACK_REACH:
				this.fr++;
				break;
			case ATTACK_SPEED:
				this.fspeed++;
				break;
			case CRITICAL:
				this.crit++;
				break;
			case FASTPLACE:
				this.fp++;
				break;
			case FAST_THROW:
				this.fp++;
				break;
			case FLY:
				this.fly++;
				break;
			case GOD_MODE:
				this.gm++;
				break;
			case HORIZONTAL_SPEED:
				this.hs++;
				break;
			case IMPOSSIBLE:
				this.impossible++;
				break;
			case NOSWING:
				this.ns++;
				break;
			case SPEED_BREAK:
				this.sb++;
				break;
			case VERTICAL_SPEED:
				this.vs++;
				break;
			case TIMER:
				this.timer++;
				break;
			case SPAM:
				this.spam++;
				break;
			case FASTCLICK:
				this.fc++;
				break;
			case AUTOSOUP:
				this.as++;
				break;
			case VISIBLE:
				this.v++;
				break;
			case NOFALL:
				this.nf++;
				break;
			case FAST_INTERACT:
				this.is++;
				break;
			case GLIDE:
				this.gl++;
				break;
			case FULLBRIGHT:
				this.fb++;
				break;
			default:
				break;
		}
		
	}
	
	public void updateNotify(){
		this.lastnotification = System.currentTimeMillis();
	}
	
	public boolean shouldNotify(){
		
		return (System.currentTimeMillis() - this.lastnotification) >= 4000;
		
	}
	
}
