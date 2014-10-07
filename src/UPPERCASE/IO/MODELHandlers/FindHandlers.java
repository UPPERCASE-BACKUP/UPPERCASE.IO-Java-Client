package UPPERCASE.IO.MODELHandlers;

import java.util.List;

import org.json.JSONObject;

public abstract class FindHandlers {

	public void error(String errorMsg) {
		System.out.println("[UPPERCASE.IO-MODEL] `find` ERROR: " + errorMsg);
	}

	public void notAuthed() {
		System.out.println("[UPPERCASE.IO-MODEL] `find` NOT AUTHED!");
	}

	public void success(List<JSONObject> savedDataSet) {
	}
}
