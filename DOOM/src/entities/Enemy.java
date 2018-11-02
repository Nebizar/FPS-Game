package entities;

import java.util.List;
import java.util.Random;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Enemy extends Entity{
	
	private float hitboxHeight = 5;
	private float hitboxWidth = 5;
	private float hitboxDepth = 5;
	private int explosionTime = 20;

	Task tasks = null;
	Random generator = new Random();
	
	private static final float RUN_SPEED = 50;
	private static final float TURN_SPEED = 30;
	private final float MAX_HEIGHT_CHANGE = 0.8f;
	
	private float currentSpeed = 0;
	
	private static final float bulletSpeed = 700;
	
	private float currentTurnSpeed = 0;
	
	private boolean attacking = false;
	
	private List<Glue> mine = null;
	
	private Bullet bullet;
	
	private int exploded = 0;
	
	private Glue damageModel = null;
	
	private int damageTime = 10;
	private int damageCounter = 0;
	
	private float attackDistance = 200;
	
	private int healthPoints = 5;
	
	private Player player;
	
	private boolean attackMode = false;
	
	private boolean dead = false;

	public Enemy(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, Player player) {
		super(model, position, rotX, rotY, rotZ, scale);	
		this.player = player;
		
		Random generator = new Random();
		attackDistance = generator.nextInt(200) + 10;
	}
	
	public void hit(Vector3f point) {
		
		if(point.x < this.getPosition().x + hitboxWidth && point.x > this.getPosition().x - hitboxWidth) {
			if(point.y < this.getPosition().y + hitboxHeight && point.y > this.getPosition().y - hitboxHeight) {
				if(point.z < this.getPosition().z + hitboxDepth && point.z > this.getPosition().z - hitboxDepth) {
					
					damageModel.show();
					damageCounter = damageTime;
					healthPoints --;
					
					if(healthPoints == 0) {
						if(!dead) {
							damageCounter = damageTime;
							damageModel.setScale(this.getScale() * 4);
						}
					}
				}
			}
		}

	}
	
	public void addGlued(List<Glue> glued) {
		mine = glued;
	}
	
	private void move() {
		if(tasks== null) {
			tasks = new Task(this.getPosition().x, this.getPosition().y, this.getRotY());
			
			switch(/*generator.nextInt(5)*/ 4) {
			case 0:
				tasks.goTo(generator.nextInt(850) + 5);
				break;
			case 1:
				tasks.sleep(generator.nextInt(100) + 5);
				break;
			case 2:
				tasks.rotate(generator.nextInt(360));
				break;
			case 3:
				tasks.attack(50);
				break;
			case 4:
				attackMode = true;
				tasks.aimPlayer();
				break;
			}
		}
		
		
		
		if(tasks.isGoToGoal()){
			this.currentSpeed = RUN_SPEED;
		}else {
			this.currentSpeed = 0;
		}
		
		if(tasks.isRotateGoal()){
			this.currentTurnSpeed = tasks.getRotationDir() * TURN_SPEED;
		}else {
			this.currentTurnSpeed = 0;
		}
		
		if(tasks.isAimGoal()){
			float angle = calculateAngleToPlayer(player.getPosition());
			
			if(angle > 180) {
				this.currentTurnSpeed = - tasks.getRotationDir() * TURN_SPEED;
			}else {
				this.currentTurnSpeed = tasks.getRotationDir() * TURN_SPEED;
			}
		}else if(!tasks.isRotateGoal()){
			this.currentTurnSpeed = 0;
		}
		
		if(tasks.isAttackGoal() && exploded <= 0){
			
			if(attacking == false) {
				for(Glue glue : mine) {
					glue.show();
				}
				bullet.hideBum();
				if(damageCounter == 0) {
					damageModel.hide();
				}
				attacking = true;
				this.bullet.free();
			}
			
			float distance = bulletSpeed * DisplayManager.getFrameTimeSeconds();
			
			float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
			float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
			float dy = (float) (distance * Math.tan(Math.toRadians(-super.getRotX() -2)));
			this.bullet.increasePosition(dx, dy, dz);
			
			//Check if bullet near player
			float playerHitbox = player.getHitBoxSize();
			float bulletToPlayer = calculateDistance(player.getPosition(), this.bullet.getPosition());
			
			if(bulletToPlayer < playerHitbox) {
				bullet.explode();
				player.hitMe();
			}
			
		}else if(attacking == true){
			
			if(exploded > 0) {
				exploded --;
				bullet.setExplosionSize(explosionTime - exploded);
			}
			
			if(exploded == 0) {
				
				for(Glue glue : mine) {
					glue.hide();
				}
				this.bullet.setPosition(this.getPosition());
				attacking = false;
				this.bullet.hold();
				tasks.setReady();
			}
				
		}

		
		if(tasks.ready(this.getPosition().x, this.getPosition().y, this.getPosition().z, this.getRotY(), player.getPosition()) && exploded == 0) {
			
			if( attackMode ) {
				if(tasks.isAimGoal()) {
					
					float distance = calculateDistanceToPlayer(player.getPosition(), this.getPosition());
					if(distance < attackDistance) {
						tasks.attack(50);
					}else {
						tasks.goTo(18);
					}
					
				}else if(tasks.isGoToGoal()) {
					tasks.aimPlayer();
				}else {
					tasks.aimPlayer();
				}
				
			}else {
				tasks = null;
			}
			
		};
	}
	
	public boolean move(Terrain terrain) {
		
		if(damageCounter > 0) {
			damageCounter --;
			if(damageCounter == 0) {
				damageModel.hide();
				
				if(healthPoints <= 0 ) {
					dead = true;
					return true;
				}
			}
		}
		
		
		
		if(bullet.getPosition().y +  6< terrain.getHeightOfTerrain(bullet.getPosition().x, bullet.getPosition().z) && exploded <= 0) {
			bullet.explode();
			exploded = explosionTime;
		}
		move();
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float rotDistance = currentTurnSpeed * DisplayManager.getFrameTimeSeconds();
		
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		
		float lastPosY = super.getPosition().y;
		
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x + dx, super.getPosition().z + dz);
		float lastTerrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(lastPosY + MAX_HEIGHT_CHANGE <= terrainHeight) {
			dx = 0;
			dz = 0;
			terrainHeight = lastTerrainHeight;
		}
		
		this.increasePosition(dx, 0, dz);
		
		this.increaseRotation(0, rotDistance, 0);
		
		
		this.setRotX(0);
		this.setRotZ(0);
	
		super.getPosition().y = terrainHeight;
		
		//TerrainAngle
		//Vector3f normalsBelow = terrain.getNormals(super.getPosition().x, super.getPosition().z);
		//this.setRotX(normalsBelow.x * 90);
		stickTerrain(terrain);
		return false;
	}
	
	public void stickTerrain(Terrain terrain) {
		Vector3f normalsBelow = terrain.getNormals(super.getPosition().x, super.getPosition().z);
		float angle = 0;
		float x = normalsBelow.x;
		float y = normalsBelow.y;
		float z = normalsBelow.z;
		
		float absX = Math.abs(x);
		float absZ = Math.abs(z);
		
		if(x > 0) {
			if(z > 0) {
				angle = x/(z + x) * 90;
			}else if(z < 0){
				angle = (absZ/(absZ + absX) * 90) + 90;
			}else {
				angle = 90;
			}
		}else if(x < 0){
			if(z > 0) {
				angle = (absZ/(absZ + absX) * 90) + 270;
			}else if(z<0){
				angle = (absX/(absZ + absX) * 90) + 180;
			}else {
				angle = 270;
			}
		}else {
			if(z > 0) {
				angle = 0;
			}else if(z<0){
				angle = 180;
			}else {
				angle = 0;
			}
		}
		
		
		this.increaseRorationByPose(angle, 90 - y * 90);
	}
	
	public void increaseRorationByPose(float angleAim, float angle) {
		float rotationY = this.getRotY();
		
		
		float angleDif = Math.abs(angleAim - rotationY);
		float factor;
		
		if(angleDif < 180) {
			factor = (180 - angleDif) / 180;
		}else {
			factor = (angleDif - 180) / 180;
		}
		
		factor = factor * 2 - 1;
		
		if(attacking) {
			
			float angleX = calculateXAngleToPlayer(player) + 2;
			if(angleX > 10) {
				angleX = 10;
			}
			if(angleX < -10) {
				angleX = -10;
			}
			this.setExtraRotX(factor * angle - angleX);
		}else {
			this.setExtraRotX(factor * angle);
		}
		
		
		
		
	}
	
	
	public void attack() {
		
		
	}
	
	public List<Glue> getGlued(){
		return mine;
	}
	
	public void setBullet(Bullet tBum) {
		bullet = tBum;
	}
	
	public void addDamage(Glue exp) {
		damageModel = exp;
	}
	
	public float calculateAngleToPlayer(Vector3f playerPos) {
		float b = playerPos.z - this.getPosition().z;
		float a = playerPos.x - this.getPosition().x;
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
		
		float angle = - (this.getRotY() - halfer);
		
		if(angle < 0) {
			angle = 360 + angle;
		}
		
		
		return angle;
	}
	
	public float calculateXAngleToPlayer(Player player) {
		float distance = calculateDistanceToPlayer(player.getPosition(), this.getPosition());
		float heightDif = this.getPosition().y - player.getPosition().y;
		float angle = (float) Math.atan(distance/heightDif);
		
		return -angle;
	}
	
	public float calculateDistanceToPlayer(Vector3f playerPos, Vector3f objectPos) {
		float b = playerPos.z - objectPos.z;
		float a = playerPos.x - objectPos.x;
		float c = (float) Math.sqrt(a*a + b*b);
		
		return c;
	}
	
	private static float calculateDistance(Vector3f a, Vector3f b) {
		float dis = (float) Math.pow((Math.pow((b.x - a.x), 2) + Math.pow((b.y - a.y), 2) + Math.pow((b.z - a.z), 2)), 0.5);
		
		return dis;
	}
	
	public void upgrade(int scale) {
		this.healthPoints *= scale;
		this.setScale(scale);
		this.hitboxDepth *= scale;
		this.hitboxWidth *= scale;
		this.hitboxHeight *= scale;
	}
	
	public boolean isDead() {
		return dead;
	}
}
