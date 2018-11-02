package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;

public class Camera {
	
	private float distanceFromPlayer = 0;
	private float angleAroundPlayer = 0;
	
	private Vector3f position = new Vector3f(0,0,0);
	private float pitch = 0;
	private float yaw = 0;
	private float sensitivity = 0.25f;
	private float speed = 0.5f;
	private float roll;
	
	private Player player;
	private Entity fireball;
	private Gun gun;
	
	public Camera(Player player, Gun gun, Entity fireball) {
		this.player = player;
		this.gun = gun;
		this.fireball = fireball;
	}
	
	public void move() {
	
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
		
		int mouseX = Mouse.getX();
		int mouseY = Mouse.getY();
		
		int screenHeight = DisplayManager.getScreenHeight();
		int screenWidth = DisplayManager.getScreenWidth();
		
		int difX = (int) (((screenWidth / 2) - mouseX)*sensitivity);
		int difY = (int) (((screenHeight / 2) - mouseY)*sensitivity);
		player.increaseRotation(0, difX, 0);

		pitch += difY;
		
		if(pitch > 50) {
			pitch = 50;
		}
		if(pitch < -90) {
			pitch = -90;
		}
		
		Mouse.setCursorPosition(screenWidth / 2, screenHeight / 2);
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	private void calculateCameraPosition(float horizDistance, float verticDistance) {
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta))*speed);
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta))*speed);
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticDistance  + 10f;
		
		float alfa = gun.getRotY();
		alfa = alfa % 180 - 90;
		alfa = Math.abs(alfa);
		gun.setPosition(new Vector3f(this.getPosition().x,this.getPosition().y - 2,this.getPosition().z));
		fireball.setPosition(new Vector3f(this.getPosition().x,this.getPosition().y - 2,this.getPosition().z));
		
		
		gun.setRotY(player.getRotY());
		fireball.setRotY(player.getRotY());
		
		gun.setRotX(pitch);
		fireball.setRotX(pitch);
	}
	
	
	private float calculateHorizontalDistance() {
		return (float)( distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance() {
		return (float)( distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
}
