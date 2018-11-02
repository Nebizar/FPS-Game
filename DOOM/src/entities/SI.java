package entities;

import org.lwjgl.util.vector.Vector3f;

public class SI {
	private Player player;
	private Entity badGuy;
	private Vector3f playerPos=new Vector3f(0,0,0);
	private Vector3f badGuyPos=new Vector3f(0,0,0);
	private double distance;
	public SI(Entity en,Player pl) {
		player=pl;
		badGuy=en;
	}
	public void think() {
		playerPos=player.getPosition();
		badGuyPos=badGuy.getPosition();
		distance=calculateDistance(playerPos.x,playerPos.z,badGuyPos.x,badGuyPos.z);
		float ratio=(playerPos.z-badGuyPos.z)/(playerPos.x-badGuyPos.x);
		if(distance<75){
			//shoot
			if(playerPos.x<badGuyPos.x) {
				if(playerPos.z<badGuyPos.z) {
					if(ratio<1/3) {
						badGuy.setRotY(270);
					}
					else if(ratio>3){
						badGuy.setRotY(180);
					}
					else {
						badGuy.setRotY(225);
					}
				}
				else {
					if(ratio<-1*1/3) {
						badGuy.setRotY(270);
					}
					else if(ratio>-1*3){
						badGuy.setRotY(0);
					}
					else {
						badGuy.setRotY(315);
					}
				}
			}
			else {
				if(playerPos.z<badGuyPos.z) {
					if(ratio<-1*1/3) {
						badGuy.setRotY(90);
					}
					else if(ratio>-1*3){
						badGuy.setRotY(180);
					}
					else {
						badGuy.setRotY(135);
					}
				}
				else {
					if(ratio<1/3) {
						badGuy.setRotY(90);
					}
					else if(ratio>3){
						badGuy.setRotY(0);
					}
					else {
						badGuy.setRotY(45);
					}
				}
			}
		}
		else{
			if(playerPos.x<badGuyPos.x) {
				if(playerPos.z<badGuyPos.z) {
					badGuy.increasePosition(-0.5f, 0, -0.5f);
					badGuy.setRotY(225);
				}
				else {
					badGuy.increasePosition(-0.5f, 0, 0.5f);
					badGuy.setRotY(315);
				}
			}
			else {
				if(playerPos.z<badGuyPos.z) {
					badGuy.increasePosition(0.5f, 0, -0.5f);
					badGuy.setRotY(135);
				}
				else {
					badGuy.increasePosition(0.5f, 0, 0.5f);
					badGuy.setRotY(45);
				}
			}
		}
	}
	private double calculateDistance(float x1, float y1, float x2, float y2) {
		         
		    float ac = Math.abs(y2 - y1);
		    float cb = Math.abs(x2 - x1);
		         
		    return Math.hypot(ac, cb);
	}
}