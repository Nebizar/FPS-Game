package engineTester;

import entities.Entity;

public class TemporaryItem {
	private Entity object;
	private int frames;
	
	public TemporaryItem(Entity object, int frames) {
		this.object = object;
		this.frames = frames;
	}

	public Entity getObject() {
		frames --;
		return object;
	}

	public int getFrames() {
		return frames;
	}
	
	
	
}
