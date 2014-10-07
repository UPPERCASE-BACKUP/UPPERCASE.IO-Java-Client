package UPPERCASE.IO.MODELHandlers;

import org.json.JSONObject;

public abstract class CreateHandlers {

	public void error(String errorMsg) {
		System.out.println("[UPPERCASE.IO-MODEL] `create` ERROR: " + errorMsg);
	}

	public void notValid(JSONObject validErrors) {
		System.out.println("[UPPERCASE.IO-MODEL] `create` NOT VALID!: " + validErrors);
	}

	public void notAuthed() {
		System.out.println("[UPPERCASE.IO-MODEL] `create` NOT AUTHED!");
	}

	public void success(JSONObject savedData) {
	}
}
