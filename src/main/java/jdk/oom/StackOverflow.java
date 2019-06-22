package jdk.oom;

/**
 * @author DDf on 2019/4/14
 */
public class StackOverflow {

	public void deadLoop() {
		deadLoop();
	}


	public static void main(String[] args) {
		StackOverflow stackOverflow = new StackOverflow();
		stackOverflow.deadLoop();
	}
}
