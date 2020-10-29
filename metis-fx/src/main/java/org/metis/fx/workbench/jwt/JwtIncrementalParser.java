package org.metis.fx.workbench.jwt;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodec;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultHeader;
import io.jsonwebtoken.impl.DefaultJws;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import io.jsonwebtoken.impl.DefaultJwt;
import io.jsonwebtoken.impl.TextCodec;
import io.jsonwebtoken.impl.crypto.JwtSignatureValidator;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.lang.Objects;
import io.jsonwebtoken.lang.Strings;

public class JwtIncrementalParser {
	private static final char SEPARATOR_CHAR = '.';
	private String jwt;
	private String base64UrlEncodedHeader;
	private String base64UrlEncodedPayload;
	private String base64UrlEncodedDigest;
	
	private boolean error=false;
	private String errorMessage="";

	public JwtIncrementalParser(String jwt) {
		this.jwt = jwt;
	}
	
	public boolean isError() {
		return error;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void parse() throws ExpiredJwtException, MalformedJwtException, SignatureException {

        Assert.hasText(jwt, "JWT String argument cannot be null or empty.");

        this.base64UrlEncodedHeader = null;
        this.base64UrlEncodedPayload = null;
        this.base64UrlEncodedDigest = null;

        int delimiterCount = 0;

        StringBuilder sb = new StringBuilder(128);

        for (char c : jwt.toCharArray()) {

            if (c == SEPARATOR_CHAR) {

                CharSequence tokenSeq = Strings.clean(sb);
                String token = tokenSeq!=null?tokenSeq.toString():null;

                if (delimiterCount == 0) {
                    base64UrlEncodedHeader = token;
                } else if (delimiterCount == 1) {
                    base64UrlEncodedPayload = token;
                }

                delimiterCount++;
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }

        if (delimiterCount != 2) {
            this.errorMessage = "JWT strings must contain exactly 2 period characters. Found: " + delimiterCount;            
        }
        if (sb.length() > 0) {
            base64UrlEncodedDigest = sb.toString();
        }

//        if (base64UrlEncodedPayload == null) {
//            throw new MalformedJwtException("JWT string '" + jwt + "' is missing a body/payload.");
//        }
        
    }
}
