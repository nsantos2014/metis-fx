package org.metis.fx.workbench.json;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.fxmisc.richtext.model.PlainTextChange;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import org.metis.fx.workbench.jwt.JSONHighlighter;
import org.reactfx.EventStreams;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXSnackbar;
import com.sun.javafx.collections.ObservableMapWrapper;

import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;

@FxmlView
@Component
@Slf4j
public class JsonEditor {
	
	private final AtomicInteger pageCounter= new AtomicInteger();
	
	@FXML
	AnchorPane root;

	private JFXSnackbar snackbar;

	@FXML
	CodeArea editorArea;

	private ObjectMapper objectMapper = new ObjectMapper();

	@FXML
	ToggleButton btnToggleTextWrap;
	
	Map<String,String> realCache=new ConcurrentHashMap<>();
	ObservableMap<String, String> cache=new ObservableMapWrapper<>(realCache);

	@FXML ComboBox<String> cmbPages;

	@FXML
	public void initialize() {
		snackbar = new JFXSnackbar(root);

		final JSONHighlighter jsonHighlighter = new JSONHighlighter();
		editorArea.multiPlainChanges().successionEnds(Duration.ofMillis(100))
				.subscribe(c -> editorArea.setStyleSpans(0, jsonHighlighter.computeHighlighting(editorArea.getText())));

		editorArea.setParagraphGraphicFactory(LineNumberFactory.get(editorArea));
		btnToggleTextWrap.selectedProperty().bindBidirectional(editorArea.wrapTextProperty());

		InputMap<KeyEvent> im = InputMap.consume(EventPattern.keyPressed(KeyCode.TAB),
				e -> editorArea.replaceSelection("  "));
		Nodes.addInputMap(editorArea, im);

		im = InputMap.consume(EventPattern.keyPressed(KeyCode.F, KeyCombination.CONTROL_DOWN), e -> doFormat());
		Nodes.addInputMap(editorArea, im);

		im = InputMap.consume(EventPattern.keyPressed(KeyCode.W, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN),
				e -> editorArea.setWrapText(!editorArea.isWrapText()));
		Nodes.addInputMap(editorArea, im);

		//cmbPages.setItems(FXCollections.observableArrayList(cache.keySet()));
		//cmbPages.itemsProperty().bind();
		//Bindings.bindContentBidirectional(cmbPages.getItems(), FXCollections.observableArrayList(cache.keySet()));
		
		EventStreams.changesOf(cmbPages.getSelectionModel().selectedItemProperty()).successionEnds(Duration.ofMillis(100))
		.subscribe(c -> changePage(c.getNewValue()) );
		
		
		
		editorArea.multiPlainChanges().successionEnds(Duration.ofMillis(100))
			.subscribe(c -> updatePage(c));
		
		
		Popup popup = new Popup();
        Label popupMsg = new Label();
        popupMsg.setStyle(
                "-fx-background-color: black;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 5;");
        popup.getContent().add(popupMsg);
        
        
        editorArea.setMouseOverTextDelay(Duration.ofSeconds(1));
        editorArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
            int chIdx = e.getCharacterIndex();
            Point2D pos = e.getScreenPosition();
            popupMsg.setText("Character '" + editorArea.getText(chIdx, chIdx+1) + "' at " + pos);
            popup.show(editorArea, pos.getX(), pos.getY() + 10);
        });
        editorArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> {
            popup.hide();
        });
		
        
		appendPage();
		
		
		Platform.runLater(() -> {
			editorArea.requestFocus();
		});

	}

	private void changePage(String pageName) {
		String text = cache.get(pageName);
		editorArea.replaceText(text);
	}

	private void updatePage(List<PlainTextChange> c) {
		final String pageName = cmbPages.getValue();
		cache.put(pageName, editorArea.getText());
	}

	@FXML
	public void formatText(ActionEvent event) {
		doFormat();
	}

	public void doFormat() {
		try {
			JsonNode jsonNode = objectMapper.readTree(editorArea.getText());
			editorArea.replaceText(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void appendPage() {
		String pageName = "newPage-"+pageCounter.getAndIncrement();
		cache.put(pageName, "");
		cmbPages.getItems().add(pageName);
		cmbPages.setValue(pageName);
		
	}

	@FXML public void newPage(ActionEvent event) {
		appendPage();
	}


}
