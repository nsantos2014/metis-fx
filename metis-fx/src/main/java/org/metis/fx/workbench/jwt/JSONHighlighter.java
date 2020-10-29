package org.metis.fx.workbench.jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.metis.fx.api.Highlighter;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JSONHighlighter implements Highlighter {

	private static final String JSON_CURLY = "(?<JSONCURLY>\\{|\\})";
	private static final String JSON_PROPERTY = "(?<JSONPROPERTY>\\\".*\\\")\\s*:\\s*";
	private static final String JSON_VALUE = "(?<JSONVALUE>\\\".*\\\")";
	private static final String JSON_ARRAY = "\\[(?<JSONARRAY>.*)\\]";
	private static final String JSON_NUMBER = "(?<JSONNUMBER>\\d*.?\\d*)";
	private static final String JSON_BOOL = "(?<JSONBOOL>true|false)";

	private static final Pattern FINAL_REGEX = Pattern.compile(JSON_CURLY + "|" + JSON_PROPERTY + "|" + JSON_VALUE + "|"
			+ JSON_ARRAY + "|" + JSON_BOOL + "|" + JSON_NUMBER);
	private ObjectMapper objectMapper;

	public JSONHighlighter() {
		this.objectMapper = new ObjectMapper();
	}

//    @Override
//    public StyleSpans<Collection<String>> computeHighlighting(String text) {
//        Matcher matcher = FINAL_REGEX.matcher(text);
//        int lastKwEnd = 0;
//        StyleSpansBuilder<Collection<String>> spansBuilder
//                = new StyleSpansBuilder<>();
//        while (matcher.find()) {
//            String styleClass
//                    = matcher.group("JSONPROPERTY") != null ? "json_property"
//                    : matcher.group("JSONVALUE") != null ? "json_value"
//                    : matcher.group("JSONARRAY") != null ? "json_array"
//                    : matcher.group("JSONCURLY") != null ? "json_curly"
//                    : matcher.group("JSONBOOL") != null ? "json_bool"
//                    : matcher.group("JSONNUMBER") != null ? "json_number"
//                    : null;
//            /* never happens */
//            assert styleClass != null;
//            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
//            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
//            lastKwEnd = matcher.end();
//        }
//        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
//        return spansBuilder.create();
//    }

	@Override
	public StyleSpans<Collection<String>> computeHighlighting(String text) {
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		int lastKwEnd = 0;

		try {
			JsonParser parser = objectMapper.getFactory().createParser(text);
			log.info("Parser {} ",parser.isClosed());
			while (!parser.isClosed()) {
				JsonToken token = parser.nextToken();
				JsonLocation location = parser.getTokenLocation();
				String fieldName;
				int length;

				switch (token) {
				case FIELD_NAME:
					spansBuilder.add(Collections.emptyList(), (int) (location.getCharOffset() - lastKwEnd));
					fieldName = parser.getCurrentName();
					spansBuilder.add(Collections.singleton("json_property"), fieldName.length() + 2);
					lastKwEnd = (int) location.getCharOffset() + fieldName.length() + 2;
					break;
				case VALUE_EMBEDDED_OBJECT:
					break;
				case VALUE_FALSE:
				case VALUE_TRUE:
					spansBuilder.add(Collections.emptyList(), (int) (location.getCharOffset() - lastKwEnd));
					fieldName = parser.getCurrentName();
					length = parser.getTextLength();
					spansBuilder.add(Collections.singleton("json_bool"), length);
					lastKwEnd = (int) location.getCharOffset() + length;
					break;
				case VALUE_NUMBER_FLOAT:
				case VALUE_NUMBER_INT:
					spansBuilder.add(Collections.emptyList(), (int) (location.getCharOffset() - lastKwEnd));
					fieldName = parser.getCurrentName();
					length = parser.getTextLength();
					spansBuilder.add(Collections.singleton("json_number"), length);
					lastKwEnd = (int) location.getCharOffset() + length;
					break;
				case VALUE_NULL:
					spansBuilder.add(Collections.emptyList(), (int) (location.getCharOffset() - lastKwEnd));
					fieldName = parser.getCurrentName();
					length = parser.getTextLength();
					spansBuilder.add(Collections.singleton("json_value"), length);
					lastKwEnd = (int) location.getCharOffset() + length;
					break;
				case VALUE_STRING:
					spansBuilder.add(Collections.emptyList(), (int) (location.getCharOffset() - lastKwEnd));
					fieldName = parser.getCurrentName();
					length = parser.getTextLength();
					spansBuilder.add(Collections.singleton("json_value"), length + 2);
					lastKwEnd = (int) location.getCharOffset() + length + 2;
					break;
				case START_OBJECT:
				case END_OBJECT:
					spansBuilder.add(Collections.emptyList(), (int) (location.getCharOffset() - lastKwEnd));
					spansBuilder.add(Collections.singleton("json_curly"), 1);
					lastKwEnd = (int) location.getCharOffset() + 1;
					break;

				case START_ARRAY:
				case END_ARRAY:
					spansBuilder.add(Collections.emptyList(), (int) (location.getCharOffset() - lastKwEnd));
					spansBuilder.add(Collections.singleton("json_array"), 1);
					lastKwEnd = (int) location.getCharOffset() + 1;
					break;
				}

			}

		} catch (JsonProcessingException e) {
			log.error(" Parse error at : {} {}", e.getLocation(), e.getMessage());
		} catch (Exception e) {
			log.error(" Parse error : {} {}", e.getClass().getCanonicalName(), e.getMessage());
		}

		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}
}
