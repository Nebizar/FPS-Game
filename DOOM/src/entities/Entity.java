package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Entity {

	private TexturedModel model;
	private Vector3f position;
	private float rotX, rotY, rotZ, extraRotX, extraRotY, extraRotZ;
	private float scale;
	private boolean hidden = false;
	
	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super();
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		extraRotX = 0;
	}
	
	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;	
	}
	
	public void increaseRotation(float dx, float dy, float dz) {
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
		
		normalizeAngles();
	}
	
	public TexturedModel getModel() {
		return model;
	}


	public void setModel(TexturedModel model) {
		this.model = model;
	}


	public Vector3f getPosition() {
		return position;
	}


	public void setPosition(Vector3f position) {
		this.position = position;
	}


	public float getRotX() {
		return rotX + extraRotX;
	}


	public void setRotX(float rotX) {
		this.rotX = rotX;
		normalizeAngles();
	}


	public float getRotY() {
		return rotY + extraRotY;
	}


	public void setRotY(float rotY) {
		this.rotY = rotY;
		normalizeAngles();
	}


	public float getRotZ() {
		return rotZ + extraRotZ;
	}


	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
		normalizeAngles();
	}


	public float getScale() {
		return scale;
	}


	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public void setScaleX(float scale) {
		
	}
	
	public void setExtraRotX(float rot) {
		this.extraRotX = rot;
	}
	
	public void setExtraRotY(float rot) {
		this.extraRotY = rot;
	}
	
	public void setExtraRotZ(float rot) {
		this.extraRotZ = rot;
	}
	
	public void hide() {
		hidden = true;
	}
	
	public void show() {
		hidden = false;
	}
	
	public boolean hidden() {
		return hidden;
	}
	
	public void hideDef() {
		
	}
	public void showDef() {
		
	}
	
	
	public void normalizeAngles()
	{
		this.rotX = this.rotX % 360;
		this.rotY = this.rotY % 360;
		this.rotZ = this.rotZ % 360;
		
		if(this.rotX < 0) {
			this.rotX = 360 + this.rotX;
		}
		if(this.rotY < 0) {
			this.rotY = 360 + this.rotY;
			
		}
		if(this.rotZ < 0) {
			this.rotZ = 360 + this.rotZ;
		}
	}
}
