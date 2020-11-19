package org.metis.fx.services.cipher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JsonStorage {

	private Path dataPath;
	private Path filepath;
	private String filename;
	protected ObjectMapper objectMapper;

	public JsonStorage(Path dataPath, String filename) {
		this(dataPath, filename, new ObjectMapper());
	}

	public JsonStorage(Path dataPath, String filename, ObjectMapper objectMaper) {
		this.dataPath = dataPath;
		this.filename = filename;
		this.filepath = this.dataPath.resolve(filename);
		this.objectMapper = objectMaper;
	}

	@PostConstruct
	public void init() {
		try {
			Files.createDirectories(dataPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getRoot();
	}

	@PreDestroy
	public void shutdown() {

	}

	protected JsonNode getRoot() {
		if (Files.exists(this.filepath)) {
			return loadData(this.filepath);
		} else {
			JsonNode initData = initData();
			saveData(this.filepath, initData);
			return initData;
		}
	}

	protected void saveData(JsonNode root) {
		// TODO check for changes??
		saveData(this.filepath, root);
	}

	protected abstract JsonNode initData();

	protected abstract JsonNode loadData(Path filepath);

	protected abstract void saveData(Path filepath, JsonNode root);

}
