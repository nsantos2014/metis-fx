package org.metis.fx.workbench;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class DialogController {

	@FXML
	private Stage stage;

	@FXML
    public void initialize() { 
        this.stage = new Stage();
        this.stage.initStyle(StageStyle.DECORATED);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(getContent()));
    }

    protected abstract Parent getContent();

    

	public void show() {    	
        stage.show(); 
    }
	
	public void close() {    	
		stage.close(); 
    }
	

	public void close(ActionEvent event) {    	
		close(); 
    }
    
    
	
}
