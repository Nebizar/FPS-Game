package textures;

public class ModelTexture {
	
	private int textureID;
	
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	
	public ModelTexture(int id) {
		this.textureID = id;
	}
	
	public int getID() {
		return textureID;
	}
	
	public float getShineDamper() {
		return shineDamper;
	}
	
	public float getReflectivity() {
		return reflectivity;
	}
	
	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}
	
	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
	
	public void setHasTransparency(boolean trans) {
		hasTransparency = trans;
	}
	
	public boolean isHasTransparency() {
		return hasTransparency;
	}

	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}
	
	
}
