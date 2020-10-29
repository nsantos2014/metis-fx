package org.metis.fx.workshop;

import org.metis.fx.StageReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;

@Slf4j
@Component
public class WorkshopInitializer implements ApplicationListener<StageReadyEvent> {
	private final FxWeaver fxWeaver;
	private Stage mainStage;
	
	public WorkshopInitializer(FxWeaver fxWeaver) {
		this.fxWeaver = fxWeaver;
	}
	
	@Override
	public void onApplicationEvent(StageReadyEvent event) {
		log.info("Load main Window");
        mainStage = event.stage;
        
        Scene scene = new Scene(fxWeaver.loadView(WorkshopController.class));
        scene.getStylesheets().add(WorkshopController.class.getResource("metis-workshop.css").toExternalForm());
        scene.getStylesheets().add(WorkshopController.class.getResource("metis.css").toExternalForm());
        //scene.getStylesheets().add(MainWindow.class.getResource("java-keywords.css").toExternalForm());
		
        mainStage.setTitle("Metis");
        mainStage.setScene(scene);
		//Rectangle2D bounds = Screen.getPrimary().getBounds();
//		//stage.initStyle(StageStyle.UTILITY);
//		stage.initStyle(StageStyle.UNDECORATED);
//		
//		stage.setResizable(false);
//		
//		stage.setX(bounds.getMinX());
//		stage.setY(bounds.getMinY());
////		stage.setWidth(bounds.getWidth());
////		stage.setHeight(100);
		
		mainStage.show();
	}
}
