package org.metis.fx.workbench.jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.metis.fx.api.Highlighter;

public class EncodedJwtHighlighter implements Highlighter {
	private static final String JWT_HEADER = "(?<JWTENCODED>[A-Za-z0-9-_=]+)";
	private static final String JWT_DOT = "(?<JWTDOT>\\.)";

	private static final Pattern FINAL_REGEX = Pattern.compile(JWT_HEADER + "|" + JWT_DOT);

	@Override
	public StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = FINAL_REGEX.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

		boolean header=false;
		boolean payload=false;
		
		while (matcher.find()) {

			String encoded = matcher.group("JWTENCODED");
			String dot = matcher.group("JWTDOT");

			if (encoded == null && dot == null) {
				continue;
			}

			final String styleClass;
			if (dot != null) {
				styleClass = "cm-jwt-dot";
			} else if (encoded!=null){	
				if( !header ) {
					styleClass= "cm-jwt-header";
					header=true;
				} else if( !payload ) {
					styleClass= "cm-jwt-payload";
					payload=true;
				}else {
					styleClass= "cm-jwt-signature";
				}
				
			} else {
				continue;
			}
			
			
			/* never happens */
			assert styleClass != null;
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}

}
