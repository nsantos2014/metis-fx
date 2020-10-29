package org.metis.fx.workshop;

import org.metis.fx.workbench.cipher.CipherTools;
import org.metis.fx.workbench.dashboard.Dashboard;
import org.metis.fx.workbench.json.JsonEditor;
import org.metis.fx.workbench.jwt.JwtTools;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@FxmlView("Workshop.fxml")
@Component
public class WorkshopController {
	@FXML
	BorderPane workarea;

	private FxWeaver fxWeaver;

	private Node dashboard=null;

	private Node cipherTools;

	private Node jwtTools;

	private Node jsonEditor;

	
	@FXML
	public void initialize() {
		openDashboard(null);
	}

	public WorkshopController( FxWeaver fxWeaver) {
		this.fxWeaver = fxWeaver;
	}

	public void openDashboard(ActionEvent event) {
		if( dashboard==null) {
			dashboard=fxWeaver.loadView(Dashboard.class);
		}
		workarea.setCenter(dashboard);
	}
	public void openEncryptionTools(ActionEvent event) {
		if( cipherTools==null) {
			cipherTools=fxWeaver.loadView(CipherTools.class);
		}
		workarea.setCenter(cipherTools);
	}

	public void openJwtTools(ActionEvent event) {
		if( jwtTools==null) {
			jwtTools=fxWeaver.loadView(JwtTools.class);
		}
		workarea.setCenter(jwtTools);
	}

	public void openJsonEditor(ActionEvent event) {
		if( jsonEditor==null) {
			jsonEditor=fxWeaver.loadView(JsonEditor.class);
		}
		workarea.setCenter(jsonEditor);
	}
	
	public void closeApp(ActionEvent event) {
		Platform.exit();
	}
}
