package no.sandramoen.blipblop.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import no.sandramoen.blipblop.BlipBlopGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		float scale = 2.5f;
		int LGg8ThinQHeight = 3120;
		int LGg8ThinQWidth = 1440;

		config.title = "Blip Blop!";
		config.width = (int) (LGg8ThinQWidth / scale);
		config.height = (int) (LGg8ThinQHeight / scale);
		config.resizable = false;
		new LwjglApplication(new BlipBlopGame(), config);
	}
}
