package UPPERCASE.IO.MODELHandlers;

import org.json.JSONObject;

public abstract class GetHandlers {

	public void error(String errorMsg) {
		System.out.println("[UPPERCASE.IO-MODEL] `get` ERROR: " + errorMsg);
	}

	public void notExists() {
		System.out.println("[UPPERCASE.IO-MODEL] `get` NOT EXISTS!");
	}

	public void notAuthed() {
		System.out.println("[UPPERCASE.IO-MODEL] `get` NOT AUTHED!");
	}

	public void success(JSONObject savedData) {
	}
}
