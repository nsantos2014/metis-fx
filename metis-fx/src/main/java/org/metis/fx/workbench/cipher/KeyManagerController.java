package org.metis.fx.workbench.cipher;

import java.util.Arrays;
import java.util.function.Consumer;

import org.metis.fx.workbench.EditingDialogController;
import org.metis.fx.workbench.cipher.model.CipherKey;
import org.metis.fx.workbench.cipher.model.CipherKey.CipherKeyBuilder;
import org.metis.fx.workbench.cipher.model.CipherKeyType;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;

@FxmlView("KeyManagerDialog.fxml") 
@Component
public class KeyManagerController extends EditingDialogController<CipherKey>{
	
	@FXML
	private Stage stage;

    @FXML
    private AnchorPane dialog;

	@FXML AnchorPane symmetricKeyPanel;

	@FXML AnchorPane asymmetricKeyPanel;

	@FXML ComboBox<String> fldKeyType;

	@FXML TextField fldKeyId;

	private Consumer<CipherKey> saveConsumer;

	@FXML TextField fldKeyValue;

	@FXML TextArea fldPrivateKeyValue;

	@FXML TextArea fldPublicKeyValue;

    
	@FXML
	@Override
    public void initialize() { 
		super.initialize();
		
		Arrays.stream(CipherKeyType.values()).map(CipherKeyType::name).forEach(fldKeyType.getItems()::add);
		asymmetricKeyPanel.visibleProperty().bind(fldKeyType.valueProperty().isEqualTo("ASYMMETRIC"));
		symmetricKeyPanel.visibleProperty().bind(fldKeyType.valueProperty().isEqualTo("SYMMETRIC"));
		
	}
	
    
    @Override
    protected Parent getContent() {
    	return dialog;
    }

	@Override
	public void bindData(CipherKey data, Consumer<CipherKey> saveConsumer) {
		this.saveConsumer = saveConsumer;
		fldKeyId.setText(data.getId());
		fldKeyType.setValue(data.getType().name());
	}
    
	
	@Override
    protected void doSave() {
		CipherKeyType converted = convert(fldKeyType.getValue());
		CipherKeyBuilder builder = CipherKey.builder()
				.id(fldKeyId.getText())
				.type(converted);
		
		switch (converted) {
		case SYMMETRIC:
			builder.key(fldKeyValue.getText());
			break;
		case ASYMMETRIC:
			builder.privateKey(fldPrivateKeyValue.getText());
			builder.publicKey(fldPublicKeyValue.getText());
			break;

		default:
			break;
		}
		CipherKey t = builder

				.build();
		saveConsumer.accept(t);
    }


	public CipherKeyType convert(String string) {
		return CipherKeyType.valueOf(string);
	}
}

