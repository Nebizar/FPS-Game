package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.ContextAttribs;

public class DisplayManager {
	
	private static final int WIDTH = 1920;
	private static final int HEIGHT = 1080;
	private static final int FPS_CAP = 60;
	
	private static int ScreenHeightC;
	private static int ScreenWidthC;
	
	private static long lastFrameTime;
	private static float delta;
	
	private static boolean fullscreen = true;
	
	
	public static void createDisplay() {
		
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		try {
			
			
			try {
				Display.setFullscreen(true);
				
				ScreenHeightC = Display.getDisplayMode().getHeight();
				ScreenWidthC = Display.getDisplayMode().getHeight();
				
			} catch (LWJGLException e) {
				// TODO Auto-generated catch block
				fullscreen = false;
				Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
				
				ScreenHeightC = HEIGHT;
				
				ScreenWidthC = WIDTH;
			}
			Display.create(new PixelFormat(), attribs);
			Display.setTitle("Mini tank fighter");
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!fullscreen) {
			GL11.glViewport(0, 0, WIDTH,HEIGHT);
		}
		
		lastFrameTime = getCurrentTime();
	}
	
	public static void updateDisplay() {
		
		Display.sync(FPS_CAP);
		Display.update();
		
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
		
		
	}
	
	public static void closeDisplay() {	
		Display.destroy();	
	}
	
	public static long getCurrentTime() {
		return Sys.getTime() *1000 / Sys.getTimerResolution();
	}
	
	public static float getFrameTimeSeconds() {
		return delta;
	}
	
	public static int getScreenHeight() {
		return ScreenHeightC;
	}
	
	public static int getScreenWidth() {
		return ScreenWidthC;
	}
	
}
