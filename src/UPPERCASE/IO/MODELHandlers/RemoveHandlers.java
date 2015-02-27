package UPPERCASE.IO.MODELHandlers;

import org.json.JSONObject;

public abstract class RemoveHandlers {

	public void error(String errorMsg) {
		System.out.println("[UPPERCASE.IO-MODEL] `remove` ERROR: " + errorMsg);
	}

	public void notExists() {
		System.out.println("[UPPERCASE.IO-MODEL] `remove` NOT EXISTS!");
	}

	public void notAuthed() {
		System.out.println("[UPPERCASE.IO-MODEL] `remove` NOT AUTHED!");
	}

	public void success(JSONObject originData) {
	}
}
