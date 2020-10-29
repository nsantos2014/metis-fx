package org.metis.fx;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppPreloader extends Preloader{
	private Stage stage;

	public void start(Stage stage) throws Exception {
		log.info("Started preloader");
		this.stage = stage;

		ProgressIndicator root = new ProgressIndicator(-1);
		root.setBackground(Background.EMPTY);
//		root.setOpacity(.5);
		Scene scene = new Scene(root, 100, 100);
		scene.setFill(Color.TRANSPARENT);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void handleApplicationNotification(PreloaderNotification pn) {

		if (pn instanceof StateChangeNotification) {
			log.info("Finnish preloader");
			stage.hide();
		}
	}

}
