package UPPERCASE.IO.MODELHandlers;

import org.json.JSONObject;

public abstract class UpdateHandlers {

	public void error(String errorMsg) {
		System.out.println("[UPPERCASE.IO-MODEL] `update` ERROR: " + errorMsg);
	}

	public void notExists() {
		System.out.println("[UPPERCASE.IO-MODEL] `update` NOT EXISTS!");
	}

	public void notValid(JSONObject validErrors) {
		System.out.println("[UPPERCASE.IO-MODEL] `update` NOT VALID!: " + validErrors);
	}

	public void notAuthed() {
		System.out.println("[UPPERCASE.IO-MODEL] `update` NOT AUTHED!");
	}

	public void success(JSONObject savedData) {
	}
}
