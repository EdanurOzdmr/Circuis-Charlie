package com.circuscharlie.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.circuscharlie.CircusCharlie;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 1080;
		config.height = 1920;

		new LwjglApplication(new CircusCharlie(), config);
	}
}
