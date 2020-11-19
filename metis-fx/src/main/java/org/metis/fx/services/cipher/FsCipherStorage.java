package org.metis.fx.services.cipher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.metis.fx.workbench.cipher.model.CipherKey;
import org.metis.fx.workbench.cipher.model.CipherKeyType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FsCipherStorage extends JsonStorage implements CipherStorage {

	private ConcurrentLinkedQueue<String> conckeyList;

	private ObservableList<String> keyList;

	private AtomicLong idSequence = new AtomicLong();

	public FsCipherStorage() {
		super(getDataPath(), "keys.json");
		
		this.conckeyList = new ConcurrentLinkedQueue<>();

		this.keyList = FXCollections.observableArrayList();
	}

	private static Path getDataPath() {
		final String userHome = System.getProperty("user.home");
		Path methisPath = Path.of(userHome, "methis-data");
		return methisPath.resolve("cipher");
	}

	@PostConstruct
	public void init() {
		super.init();
		
		newSimetricKey("sample1", "asdasdassad");
	}

	public ObservableList<String> keyListProperty() {
		return keyList;
	}

	
	public void newSimetricKey(String id, String key) {
		ObjectNode root = null;

		root = (ObjectNode) getRoot();
		JsonNode keyData = root.path(id);
		if (keyData.isMissingNode()) {
			keyData = this.objectMapper.createObjectNode();
			CipherKey keyObj = CipherKey.builder().id(id).type(CipherKeyType.SYMMETRIC).key(key).build();
			root.putPOJO(id, keyObj);

			conckeyList.add(id);

			saveData(root);
		}

	}

	@Scheduled(fixedRate = 10000, initialDelay = 1000)
	public void scanKeys() {
		log.info("Scan keys");
		Platform.runLater(() -> {
			keyList.clear();
			keyList.addAll(conckeyList);
		});
	}

	public void appendKey(CipherKey d) {
		switch (d.getType()) {
		case SYMMETRIC:
			newSimetricKey(d.getId(), d.getKey());
			break;
		case ASYMMETRIC:
			break;

		default:
			break;
		}

	}

	public String generateId() {
		JsonNode root = getRoot();
		return String.format("key%06d", idSequence.getAndIncrement());
	}

	@Override
	protected JsonNode initData() {
		ObjectNode root = this.objectMapper.createObjectNode();
		root.put("idSequence", idSequence.longValue());
		return root;
	}

	@Override
	protected JsonNode loadData(Path filepath) {
		ObjectNode root = null;
		try {
			root = (ObjectNode) this.objectMapper.readTree(filepath.toFile());
			long idSeq = root.path("idSequence").asLong(-1);
			if (idSeq > idSequence.longValue()) {
				idSequence.set(idSeq);
			}

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return root;
	}

	@Override
	protected void saveData(Path filepath, JsonNode root) {
		try {
			this.objectMapper.writeValue(filepath.toFile(), root);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
