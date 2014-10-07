package UPPERCASE.IO.MODELHandlers;

public abstract class CheckIsExistsHandlers {

	public void error(String errorMsg) {
		System.out.println("[UPPERCASE.IO-MODEL] `checkIsExists` ERROR: " + errorMsg);
	}

	public void notAuthed() {
		System.out.println("[UPPERCASE.IO-MODEL] `checkIsExists` NOT AUTHED!");
	}

	public void success(Boolean isExists) {
	}
}
