package no.sandramoen.blipblop.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import no.sandramoen.blipblop.BlipBlopGame;

import java.awt.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Blip Blop";
		config.resizable = false;

		// resolution
		// Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		// double scale = dimension.getWidth() / dimension.getHeight();
		int LGg8ThinQHeight = 3120;
		int LGg8ThinQWidth = 1440;
		config.width = (int) (LGg8ThinQWidth / 2.5);
		config.height = (int) (LGg8ThinQHeight / 2.5);

		new LwjglApplication(new BlipBlopGame(), config);
	}
}
