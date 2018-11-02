package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity{
	
	private static final float RUN_SPEED = 80;
	private static final float TURN_SPEED = 30;
	private static final float GRAVITY = -80;
	private final float JUMP_POWER = 40;
	private final float MAX_HEIGHT_CHANGE = 0.9f;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	
	private float hitboxSize = 8;

	
	private boolean isInAir = false;
	
	private int healthPoints = 5;
	
	
	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		// TODO Auto-generated constructor stub
	}

	public void move(Terrain terrain) {
		checkInputs();
		
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float rotDistance = currentTurnSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY()))) + (float) (rotDistance * Math.sin(Math.toRadians(super.getRotY() + 90)));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY()))) + (float) (rotDistance * Math.cos(Math.toRadians(super.getRotY() + 90)));
		
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();	
		
		float lastPosY = super.getPosition().y;
		
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x + dx, super.getPosition().z + dz);
		

		
		float lastTerrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(lastPosY + MAX_HEIGHT_CHANGE <= terrainHeight) {
			dx = 0;
			dz = 0;
			terrainHeight = lastTerrainHeight;
		}
		
		super.increasePosition(dx, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), dz);
		
		if(super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			super.getPosition().y = terrainHeight;
			isInAir = false;
		}
		
		
		
		
	}
	
	private void jump() {
		if(!isInAir) {
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
		
	}
	
	private void checkInputs() {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			this.currentSpeed = RUN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			this.currentSpeed = -RUN_SPEED;
		}else {
			this.currentSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			this.currentTurnSpeed = TURN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			this.currentTurnSpeed = -TURN_SPEED;
		}else {
			this.currentTurnSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
	}
	
	public float getHitBoxSize() {
		return hitboxSize;
	}
	
	public void pifPaf() {
		
		
	}
	
	public void hitMe() {
		
		if(healthPoints > 0) {
			this.healthPoints --;
		}
		
		//this.setPosition(new Vector3f(100, 100, 100));
	}
	
	public int howAreYou() {
		return healthPoints;
	}
}
