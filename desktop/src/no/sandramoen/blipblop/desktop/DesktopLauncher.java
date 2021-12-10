package no.sandramoen.blipblop.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.awt.Dimension;

import no.sandramoen.blipblop.BlipBlopGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		// resolution
		int LGg8ThinQHeight = 3120;
		int LGg8ThinQWidth = 1440;
		Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		config.height = (int) (dimension.height / 1.1); // 10% less than height of screen
		config.width = (int) (config.height / (LGg8ThinQHeight / LGg8ThinQWidth)); // width to mimic a phone

		// miscellaneous
		config.title = "Blip Blop!";
		config.resizable = false;
		config.addIcon("images/excluded/ic_launcher-desktop.png", Files.FileType.Internal);

		new LwjglApplication(new BlipBlopGame(null, "en"), config);
	}
}
