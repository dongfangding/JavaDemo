package jdk.gc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DDf on 2019/4/15
 */
public class GCOverheadLimit {
	public static void main(String[] args) {
		int i = 0;
		List<String> list = new ArrayList<>();
		while (true) {
			list.add(String.valueOf(++i).intern());
		}
	}
}
