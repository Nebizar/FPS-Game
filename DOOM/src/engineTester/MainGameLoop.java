package engineTester;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Bullet;
import entities.Camera;
import entities.Enemy;
import entities.Entity;
import entities.Glue;
import entities.Gun;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import toolbox.MousePicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainGameLoop {
	
	private static Loader loader;
	private static List<Entity> mapModels;
	private static List<GuiTexture> guis;
	private static List<Enemy> enemies;
	private static List<TemporaryItem> temporaryItems;
	private static List<Enemy> killedEnemies = new ArrayList<Enemy>();

	private static Player player;
	private static Entity fireball;
	private static Gun gun;
	private static Terrain terrain;
	private static Terrain terrainSeen;
	
	private static TexturedModel staticBumModel;
	
	private static Camera camera;
	private static Light light;
	private static GuiRenderer guiRenderer;
	private static MousePicker picker;
	private static MasterRenderer renderer;
	
	
	//Tank components
	private static RawModel tankRawModel;
	private static TexturedModel tankStaticModel;
	private static RawModel tankFireModel1;
	private static TexturedModel tankFireStaticModel1;
	private static RawModel tankFireModel2;
	private static TexturedModel tankFireStaticModel2;
	private static RawModel tankDamageModel;
	private static TexturedModel tankDamageStaticModel;
	private static RawModel bulletExplosionModel;
	private static TexturedModel bulletExplosionStaticModel;
	private static RawModel tankBulletModel;
	private static TexturedModel tankBulletStaticModel;
	private static Random generator = new Random();

	private static int lastSpawnTime = 0;
	private static int spawnFrequency =12;
	
	private static int shotCount = 0;

	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		loader = new Loader();
		renderer = new MasterRenderer();
		
		loadOthers();

		
		
		lastSpawnTime = (int) (DisplayManager.getCurrentTime() / 1000);
		loadEnemies();
		
		//Temporary items
		temporaryItems = new ArrayList<TemporaryItem>();
		
		
		
		while(!Display.isCloseRequested()) {
			camera.move();
			player.move(terrain);
			
			pifPaf();
			moveAndRender();
			
			
			renderer.processTerrain(terrainSeen);
			//renderer.processEntity(player);
			
			int acctTime = (int) (DisplayManager.getCurrentTime() / 1000);
			
			if(acctTime - lastSpawnTime > spawnFrequency) {
				spawnTank(generator.nextInt(3) + 1);
				lastSpawnTime = acctTime;
				
				if(spawnFrequency > 2) {
					spawnFrequency --;
				}
			}
			
			int hearts = player.howAreYou();
			
			if(hearts < 5){
				guis.get(hearts+1).hide();
			}
			
			if(hearts == 0) {
				break;
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
				break;
			}
			
		}
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
	
	public static void loadMapObjects() {
		//Map models
		mapModels = new ArrayList<Entity>();
		//MAP GRASS
		RawModel grassModel = OBJLoader.loadObjModel("grass1", loader);
		TexturedModel grassStaticModel = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grass1")));
		mapModels.add(new Entity(grassStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1));
		grassModel = OBJLoader.loadObjModel("grass2", loader);
		grassStaticModel = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grass2")));
		grassStaticModel.getTexture().setHasTransparency(true);
		mapModels.add(new Entity(grassStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1));
		grassModel = OBJLoader.loadObjModel("grass3", loader);
		grassStaticModel = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grass3")));
		mapModels.add(new Entity(grassStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1));
		grassModel = OBJLoader.loadObjModel("grass4", loader);
		grassStaticModel = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grass4")));
		mapModels.add(new Entity(grassStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1));
				
		mapModels.add(new Entity(grassStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1));
		grassModel = OBJLoader.loadObjModel("bus", loader);
		grassStaticModel = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("bus")));
		mapModels.add(new Entity(grassStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1));
				
		mapModels.add(new Entity(grassStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1));
		grassModel = OBJLoader.loadObjModel("clouds", loader);
		grassStaticModel = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("clouds")));
		mapModels.add(new Entity(grassStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1));
		mapModels.add(new Entity(grassStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1));
		grassModel = OBJLoader.loadObjModel("palm1", loader);
		grassStaticModel = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("palm1")));
		mapModels.add(new Entity(grassStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1));
		grassModel = OBJLoader.loadObjModel("palm2", loader);
		grassStaticModel = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("palm2")));
		mapModels.add(new Entity(grassStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1));
				
		grassModel = OBJLoader.loadObjModel("road", loader);
		grassStaticModel = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("road")));
		mapModels.add(new Entity(grassStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1));
		//grassModel = OBJLoader.loadObjModel("land", loader);
		//grassStaticModel = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("land")));
		//mapModels.add(new Entity(grassStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1));
	}
	
	
	private static void loadOthers() {
		//Player
		
		light = new Light(new Vector3f(200,500,200), new Vector3f(1, 1, 1));
		
		terrain = new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("land")), "heightMap", true);
		terrainSeen = new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("land")), "heightMapSeen", false);
		
		RawModel playerModel = OBJLoader.loadObjModel("guy", loader);
		TexturedModel staticPlayerModel = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("guy")));
		player = new Player(staticPlayerModel, new Vector3f(5,terrain.getHeightOfTerrain(5,  5),5), 0, 0, 0, 1);
		player.setScale(6f);
				
				
		RawModel fireballModel = OBJLoader.loadObjModel("firebal", loader);
		TexturedModel staticFireballModel = new TexturedModel(fireballModel, new ModelTexture(loader.loadTexture("firebal")));
		fireball = new Entity(staticFireballModel, new Vector3f(0,0,0), 0, 0, 0, 1);
		fireball.hide();
				
				
				
		RawModel bumModel = OBJLoader.loadObjModel("bum", loader);
		staticBumModel = new TexturedModel(bumModel, new ModelTexture(loader.loadTexture("firebal")));
				
				
		RawModel gunModel = OBJLoader.loadObjModel("gunCenter", loader);
		TexturedModel staticGunModel = new TexturedModel(gunModel, new ModelTexture(loader.loadTexture("mp7")));
		gun = new Gun(staticGunModel, new Vector3f(0,0,0), 0, 0, 0, 1);
		camera = new Camera(player, gun, fireball);		
		
		
		guis = new ArrayList<GuiTexture>();
		
		GuiTexture gui = new GuiTexture(loader.loadTexture("heart"), new Vector2f(0.94f, 0.9f), new Vector2f(0.03f, 0.05f));
		guis.add(gui);
		
		gui = new GuiTexture(loader.loadTexture("heart"), new Vector2f(0.94f, 0.9f), new Vector2f(0.03f, 0.05f));
		guis.add(gui);
		
		gui = new GuiTexture(loader.loadTexture("heart"), new Vector2f(0.90f, 0.9f), new Vector2f(0.03f, 0.05f));
		guis.add(gui);
		
		gui = new GuiTexture(loader.loadTexture("heart"), new Vector2f(0.86f, 0.9f), new Vector2f(0.03f, 0.05f));
		guis.add(gui);
		
		gui = new GuiTexture(loader.loadTexture("heart"), new Vector2f(0.82f, 0.9f), new Vector2f(0.03f, 0.05f));
		guis.add(gui);
		
		gui = new GuiTexture(loader.loadTexture("heart"), new Vector2f(0.78f, 0.9f), new Vector2f(0.03f, 0.05f));
		guis.add(gui);
		
		gui = new GuiTexture(loader.loadTexture("pinnule"), new Vector2f(-0.00025f * 9, -0.00025f * 16), new Vector2f(0.0025f * 9, 0.0025f * 16));
		guis.add(gui);
		
		
		loadMapObjects();
		
		guiRenderer = new GuiRenderer(loader);
		
		
		
		for(Entity mapModel : mapModels) {
			mapModel.setPosition(new Vector3f(410,-132f,400));
			mapModel.setScale(8f);
			
		}
		
		
		picker = new MousePicker(camera, renderer.gotProjectionMatrix(), terrain);

		Mouse.setGrabbed(true);
	}
	
	public static void loadEnemies() {
		//Enemies
		enemies = new ArrayList<Enemy>(); 
		
			
		
		//Models
		tankRawModel = OBJLoader.loadObjModel("tankerSmall", loader);
		tankStaticModel = new TexturedModel(tankRawModel, new ModelTexture(loader.loadTexture("tank")));
		
		tankFireModel1 = OBJLoader.loadObjModel("tankFire1", loader);
		tankFireStaticModel1 = new TexturedModel(tankFireModel1, new ModelTexture(loader.loadTexture("firebal")));
		
		tankFireModel2 = OBJLoader.loadObjModel("tankFire2", loader);
		tankFireStaticModel2 = new TexturedModel(tankFireModel2, new ModelTexture(loader.loadTexture("firebal")));
		
		tankDamageModel = OBJLoader.loadObjModel("tankDamaged", loader);
		tankDamageStaticModel = new TexturedModel(tankDamageModel, new ModelTexture(loader.loadTexture("firebal")));
		
		bulletExplosionModel = OBJLoader.loadObjModel("bum", loader);
		bulletExplosionStaticModel = new TexturedModel(bulletExplosionModel, new ModelTexture(loader.loadTexture("firebal")));
		
		tankBulletModel = OBJLoader.loadObjModel("tankBullet", loader);
		tankBulletStaticModel = new TexturedModel(tankBulletModel, new ModelTexture(loader.loadTexture("tankBullet")));
		
		spawnTank(1);
		
	}
	
	public static void spawnTank(int size) {
		//Objects
		List<Glue> gluedItems = new ArrayList<Glue>();
				//Tank 
				Enemy badGuy = new Enemy(tankStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1, player);
				badGuy.upgrade(size);
				
				//Tank fire
				Glue tankFire1 = new Glue(tankFireStaticModel1, new Vector3f(0, 0, 0), 0, 0, 0, 1, badGuy, new Vector3f(0,0,0));
				tankFire1.hide();
				gluedItems.add(tankFire1);
				tankFire1.setScale(size);
				
				Glue tankFire2 = new Glue(tankFireStaticModel2, new Vector3f(0, 0, 0), 0, 0, 0, 1, badGuy, new Vector3f(0,0,0));
				gluedItems.add(tankFire2);
				tankFire2.hide();
				tankFire2.setScale(size);
				
				//Damage
				Glue tankDamage = new Glue(tankDamageStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1, badGuy, new Vector3f(0,0,0));
				gluedItems.add(tankDamage);
				tankDamage.hide();
				badGuy.addDamage(tankDamage);
				tankDamage.setScale(size);
				
				//Bullet explosion
				Glue bulletExplosion = new Glue(bulletExplosionStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1, badGuy, new Vector3f(0,0,0));
				gluedItems.add(bulletExplosion);
				bulletExplosion.hide();
				bulletExplosion.setScale(30 * size);
				
				//Bullet
				Bullet tankBullet = new Bullet(tankBulletStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1, badGuy, new Vector3f(0,0,0), bulletExplosion);
				gluedItems.add(tankBullet);
				tankBullet.hide();
				badGuy.setBullet(tankBullet);
				tankBullet.setScale(size);
				
				badGuy.addGlued(gluedItems);
				
						
				badGuy.setPosition(new Vector3f(generator.nextInt(500) + 5, 0,generator.nextInt(500) + 5));
				//tankBum.hide();
				enemies.add(badGuy);
	}
	
	public static void pifPaf() {
		if(Mouse.isButtonDown(0) && shotCount == 0) {
			picker.update();
			Vector3f TP = picker.getCurrentTerrainPoint();
			
			if(TP != null) {
				Entity bum = new Entity(staticBumModel, TP, 0, 0, 0, 1);
				TemporaryItem effect = new TemporaryItem(bum, 4);
				temporaryItems.add(effect);
			}
			
			player.pifPaf();
			Vector3f promien = picker.getCurrentRay();
			
			for(Enemy enemy : enemies) {
				
				float distance = calculateDistance(enemy.getPosition(), camera.getPosition());
				Vector3f point = getPointOnRay(promien, distance, camera);
			
				enemy.hit(point);

				
			}
			shotCount = 8;		
		}
		
		
		List<TemporaryItem> toRemove = new ArrayList<TemporaryItem>();
		
		for(TemporaryItem temporaryItem : temporaryItems) {
			renderer.processEntity(temporaryItem.getObject());
			
			if(temporaryItem.getFrames() <= 0 ) {
				toRemove.add(temporaryItem);								///WATCH OUT
			}
		}
		
		temporaryItems.removeAll(toRemove);
		toRemove.clear();
		
		
		if(shotCount > 0) {
			gun.setExtraRotX( shotCount * - 1.5f);
			fireball.setExtraRotX( shotCount * - 1.5f);
			fireball.show();
			shotCount --;
		}
		
		if(shotCount > 5) {
			fireball.show();
		}else {
			fireball.hide();
		}
	}
	
	public static void moveAndRender() {
		for(Entity mapModel : mapModels) {
			renderer.processEntity(mapModel);
		}
		
		
		
		for(Enemy enemy : enemies) {
			
			if(enemy.move(terrain)) {
				//Enemy killed
				killedEnemies.add(enemy);
				System.out.println("KILL");
			}
			
			renderer.processEntity(enemy);
			
			List<Glue> gluedItems = enemy.getGlued();
					
			for(Glue glue : gluedItems) {
				glue.move();
				renderer.processEntity(glue);
			}
		}
		
		enemies.removeAll(killedEnemies);
		killedEnemies.clear();
	
		
		
		renderer.processEntity(gun);
		renderer.processEntity(fireball);
		renderer.render(light, camera);

		guiRenderer.render(guis);
		
		DisplayManager.updateDisplay();
	}
	
	private static Vector3f getPointOnRay(Vector3f ray, float distance, Camera camera) {
		Vector3f camPos = camera.getPosition();
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return Vector3f.add(start, scaledRay, null);
	}
	
	private static float calculateDistance(Vector3f a, Vector3f b) {
		float dis = (float) Math.pow((Math.pow((b.x - a.x), 2) + Math.pow((b.y - a.y), 2) + Math.pow((b.z - a.z), 2)), 0.5);
		
		return dis;
	}
}
