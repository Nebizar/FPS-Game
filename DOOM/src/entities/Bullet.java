package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Bullet extends Glue{

	private Glue explosion;
	public Bullet(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale,
			Entity mother, Vector3f pose, Glue explosion) {
		super(model, position, rotX, rotY, rotZ, scale, mother, pose);
		// TODO Auto-generated constructor stub
		this.explosion = explosion;
	}
	
	
	public void explode() {
		super.hide();
		explosion.setPosition(this.getPosition());
		explosion.show();
	}
	
	public void hide() {
		super.hide();
		explosion.hide();
	}
	public void free() {
		super.setFree();
		explosion.setFree();
	}
	
	public void hold() {
		super.setHold();
		explosion.setHold();
	}
	
	public void hideBum() {
		explosion.hide();
	}
	
	public void setExplosionSize(float size) {
		explosion.setScale(size);
	}
}
