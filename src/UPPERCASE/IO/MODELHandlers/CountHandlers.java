package UPPERCASE.IO.MODELHandlers;

public abstract class CountHandlers {

	public void error(String errorMsg) {
		System.out.println("[UPPERCASE.IO-MODEL] `count` ERROR: " + errorMsg);
	}

	public void notAuthed() {
		System.out.println("[UPPERCASE.IO-MODEL] `count` NOT AUTHED!");
	}

	public void success(Long count) {
	}
}
