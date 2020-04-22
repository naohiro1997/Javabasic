package supportkit.utils;

public class Sleeper {

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// 無視する
		}
	}

	public static void sleepALittle() {
		sleep(10);
	}

}
