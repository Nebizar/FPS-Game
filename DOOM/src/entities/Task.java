package entities;

import org.lwjgl.util.vector.Vector3f;

public class Task {
	
	private float distance = 0;
	private float waitTime = 0;
	private float attackTime = 0;
	private float attackTimeGoal = 0;
	
	private float angle = 0;
	private float sumOfDistance = 0;
	private float sumOfRotation = 0;
	
	private boolean goToGoal = false;
	private boolean waitGoal = false;
	private boolean rotateGoal = false;
	private boolean attackGoal = false;
	private boolean aimGoal = false;
	
	private float previousX;
	private float previousY;
	private float previousRotationY;
	
	private float errorCount = 0;
	private boolean ready = false;
	
	private static final int angleAccuracy = 5;
	
	
	public Task(float posX, float posY, float rotY) {
		previousX = posX;
		previousY = posY;
		previousRotationY = rotY;
	}
	
	
	public void goTo(float distance) {
		clear();
		goToGoal = true;
		this.distance = distance;
		distance = 0;
	}
	
	public void sleep(int time) {
		clear();
		waitGoal = true;
		waitTime = time;
	}
	
	public void rotate(int aim) {
		attackGoal = false;
		rotateGoal = true;
		angle = aim;
		sumOfRotation = 0;
	}
	
	public void attack(int frameTime) {
		clear();
		attackGoal = true;
		attackTimeGoal = frameTime;	
	}
	
	public boolean ready(float posX, float posY, float posZ, float rotY, Vector3f playerPos) {
		
		if(goToGoal) {
			float dx = previousX - posX;
			float dy = previousY - posY;
			float made = (float) Math.sqrt( dx*dx + dy*dy );
			if(made == 0) {
				errorCount++;
				if(errorCount > 15) {
					errorCount = 0;
					return true;
				}
			}
			sumOfDistance += made;
			
			if( sumOfDistance > distance ) {
				return true;
			}
		}
		if(waitGoal){
			if(waitTime == 0) {
				return true;
			}
			waitTime --;
		}
		if(rotateGoal){
			sumOfRotation += Math.abs(previousRotationY - rotY);
			if(sumOfRotation > Math.abs(angle)) {
				return true;
			}
		}
		
		if(attackGoal) {
			if(attackTime >= attackTimeGoal) {
				return true;
			}
			attackTime ++;
		}
		
		if(aimGoal) {
			float b = playerPos.z - posZ;
			float a = playerPos.x - posX;
			float c = (float) Math.sqrt(a*a + b*b);
			
			float sina = a/c;
			float halfer = (float) Math.toDegrees(Math.asin(sina));
			
			if(a > 0 && b > 0) {
				halfer = halfer + 0;
			}else if(a > 0 && b < 0) {
				halfer = 180 - halfer ;
			}else if(a < 0 && b < 0) {
				halfer = 180 - halfer;
			}else{
				halfer = 360 + halfer;
			}
			
			float angle = - (rotY - halfer);
			
			if(angle < 0) {
				angle = 360 + angle;
			}
			
			
			if(angle < angleAccuracy || angle > 360 - angleAccuracy) {
				return true;
			}
			return false;
		}
		
		if(ready) {
			ready = false;
			clear();
			return true;
		}
		
		return false;
	}
	
	public void aimPlayer() {
		clear();
		aimGoal = true;
	}
	
	public int getRotationDir() {
		if(angle < 0) {
			return -1;
		}else {
			return 1;
		}
	}


	public boolean isGoToGoal() {
		return goToGoal;
	}


	public boolean isWaitGoal() {
		return waitGoal;
	}
	
	public boolean isAimGoal() {
		return aimGoal;
	}


	public boolean isRotateGoal() {
		return rotateGoal;
	}
	
	public boolean isAttackGoal() {
		return attackGoal;
	}
	
	
	public void setReady() {
		ready = true;
	}
	
	
	private void clear(){
		distance = 0;
		waitTime = 0;
		attackTime = 0;
		attackTimeGoal = 0;
		
		angle = 0;
		sumOfDistance = 0;
		sumOfRotation = 0;
		
		goToGoal = false;
		waitGoal = false;
		rotateGoal = false;
		attackGoal = false;
		aimGoal = false;
		errorCount = 0;
		ready = false;
	}
	
}
