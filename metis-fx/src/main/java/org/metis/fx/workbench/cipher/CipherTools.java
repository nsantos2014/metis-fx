package org.metis.fx.workbench.cipher;

import java.time.Duration;

import org.metis.fx.services.cipher.FsCipherStorage;
import org.metis.fx.workbench.cipher.model.CipherKey;
import org.metis.fx.workbench.cipher.model.CipherKeyType;
import org.reactfx.EventStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@FxmlView
@Component
public class CipherTools {

	@FXML
	PasswordField fldPassword;
	@FXML
	TextField fldPasswordPlain;
	@FXML
	ToggleButton switchPasswordView;

	@FXML
	TextArea fldKey;
	@FXML
	TextArea fldEncrypted;
	@FXML
	Label lblValidationStatus;
	@FXML
	Label lblKeyType;
	@FXML
	ToggleButton switchModeView;

	@FXML
	ComboBox<String> cmbKeyList;

	FontAwesomeIconView iconUnmasked;

	FontAwesomeIconView iconMasked;
	
	@Autowired
	FsCipherStorage storage;
	@Autowired
	FxWeaver fxWeaver;

	@FXML
	public void initialize() {
		iconMasked = new FontAwesomeIconView(FontAwesomeIcon.EYE);
		iconUnmasked = new FontAwesomeIconView(FontAwesomeIcon.EYE_SLASH);

		switchPasswordView.setGraphic(iconMasked);

		ChangeListener<? super Boolean> listener = (obs, ov, nv) -> {
			if (nv) {
				switchPasswordView.setGraphic(iconUnmasked);
			} else {
				switchPasswordView.setGraphic(iconMasked);
			}

		};
		switchPasswordView.selectedProperty().addListener(listener);

		this.fldPasswordPlain.textProperty().bindBidirectional(this.fldPassword.textProperty());

		this.fldPasswordPlain.visibleProperty().bind(switchPasswordView.selectedProperty());
		this.fldPassword.editableProperty().bind(this.fldPasswordPlain.editableProperty());

		EventStreams.changesOf(cmbKeyList.getSelectionModel().selectedItemProperty())
				.successionEnds(Duration.ofMillis(100)).subscribe(c -> selectKey(c.getNewValue()));
		
		cmbKeyList.setItems(storage.keyListProperty());
	}

	@FXML
	public void encrypt(ActionEvent event) {

	}

	@FXML
	public void decrypt(ActionEvent event) {

	}

	public void selectKey(String id) {

	}

	@FXML
	public void newKey(ActionEvent event) {
		final KeyManagerController controller = fxWeaver.loadController(KeyManagerController.class);
		controller.bindData(CipherKey.builder()
				.type(CipherKeyType.SYMMETRIC)
				.id(storage.generateId()).build(), d-> {
			storage.appendKey(d);
		});
		controller.show();
	}

}
