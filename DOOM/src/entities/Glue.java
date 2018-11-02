package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Glue extends Entity{
	
	Entity mother;
	Vector3f pose;
	private boolean free = false;

	public Glue(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, Entity mother, Vector3f pose) {
		super(model, position, rotX, rotY, rotZ, scale);
		// TODO Auto-generated constructor stub
		
		this.pose = pose;
		this.mother = mother;
	}
	
	
	public void move() {
		Vector3f motV = mother.getPosition();
		
		if(!free) {
			this.setPosition(new Vector3f(motV.x + pose.x, motV.y + pose.y, motV.z + pose.z));
		}
		this.setRotX(mother.getRotX());
		this.setRotY(mother.getRotY());
		this.setRotZ(mother.getRotZ());
	}
	
	public void setFree() {
		free = true;
	}
	
	public void setHold() {
		free = false;
	}

}
