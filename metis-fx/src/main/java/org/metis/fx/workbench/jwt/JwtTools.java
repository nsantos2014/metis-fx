package org.metis.fx.workbench.jwt;

import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.PlainTextChange;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import com.jfoenix.controls.JFXSnackbarLayout;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.TextCodec;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import net.rgielen.fxweaver.core.FxmlView;

@FxmlView
@Component
public class JwtTools {
	private JFXSnackbar snackbar;

	@FXML
	AnchorPane root;

	@FXML
	CodeArea encodedTokenEditor;
	@FXML
	CodeArea tokenHeaderEditor;
	@FXML
	CodeArea tokenPayloadEditor;

	private ObjectMapper objectMapper = new ObjectMapper();

	@FXML
	public void initialize() {

		snackbar = new JFXSnackbar(root);

		EncodedJwtHighlighter highligher = new EncodedJwtHighlighter();
		encodedTokenEditor.multiPlainChanges().successionEnds(Duration.ofMillis(100)).subscribe(
				c -> encodedTokenEditor.setStyleSpans(0, highligher.computeHighlighting(encodedTokenEditor.getText())));

		encodedTokenEditor.multiPlainChanges()
				// .suppressWhen(encodedTokenEditor.focusedProperty().not())
				.successionEnds(Duration.ofMillis(50)).subscribe(this::decode);

		encodedTokenEditor.setParagraphGraphicFactory(LineNumberFactory.get(encodedTokenEditor));

		final JSONHighlighter jsonHighlighter = new JSONHighlighter();
		tokenHeaderEditor.multiPlainChanges().successionEnds(Duration.ofMillis(100)).subscribe(c -> tokenHeaderEditor
				.setStyleSpans(0, jsonHighlighter.computeHighlighting(tokenHeaderEditor.getText())));

		tokenHeaderEditor.setParagraphGraphicFactory(LineNumberFactory.get(tokenHeaderEditor));

		tokenPayloadEditor.multiPlainChanges().successionEnds(Duration.ofMillis(100)).subscribe(c -> tokenPayloadEditor
				.setStyleSpans(0, jsonHighlighter.computeHighlighting(tokenPayloadEditor.getText())));

		tokenPayloadEditor.setParagraphGraphicFactory(LineNumberFactory.get(tokenPayloadEditor));

		Platform.runLater(()->{
			encodedTokenEditor.requestFocus();
			encodedTokenEditor.replaceText("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
					+ ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ"
					+ ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");	
		});
		

		
		// Jwts.parser().
	}

	public void decode(List<PlainTextChange> changes) {
		final String encoded = encodedTokenEditor.getText();

		try {
			// Jwt jwt = Jwts.parser().parse(encoded);
			JwtIncrementalParser parser = new JwtIncrementalParser(encoded);
			parser.parse();

			String[] splits = encoded.split("[.]");

			String headerStr = splits[0];
			String payloadStr = splits[1];
			// System.out.println(TextCodec.BASE64URL.decodeToString(headerStr));

			// System.out.println(Base64.getDecoder().TextCodec.BASE64URL.decodeToString(payloadStr));

			// Jwt jwt = Jwts.parser().parse(encoded);
			JsonNode jwtHeader = objectMapper.readTree(Base64.getDecoder().decode(headerStr));
			JsonNode jwtPayload = objectMapper.readTree(Base64.getDecoder().decode(payloadStr));

			tokenHeaderEditor.replaceText(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jwtHeader));
			tokenPayloadEditor
					.replaceText(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jwtPayload));

			sendAlert("JWT Parse: OK ");
		} catch (ExpiredJwtException e) {
			sendAlert("JWT Parse: " + e.getMessage());
		} catch (MalformedJwtException e) {
			sendAlert("JWT Parse: " + e.getMessage());
		} catch (SignatureException e) {
			sendAlert("JWT Parse: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			sendAlert("JWT Parse: " + e.getMessage());
		} catch (JsonProcessingException e) {
			sendAlert("JWT Parse: " + e.getMessage());
		} catch (IOException e) {
			sendAlert("JWT Parse: " + e.getMessage());
		}

		System.out.println("Decode");
	}

	public void encode(List<PlainTextChange> changes) {
		System.out.println("Encode");
	}

	public void sendAlert(String message) {
		snackbar.enqueue(new SnackbarEvent(new JFXSnackbarLayout(message), javafx.util.Duration.seconds(2)));
	}
}
