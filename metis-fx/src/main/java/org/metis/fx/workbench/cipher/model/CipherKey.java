package org.metis.fx.workbench.cipher.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CipherKey {
	private String id;
	private CipherKeyType type;
	private String key;
	@JsonIgnore
	private String privateKey;
	@JsonIgnore
	private String publicKey;	
	
}

