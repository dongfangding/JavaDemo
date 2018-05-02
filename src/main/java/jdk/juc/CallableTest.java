package jdk.juc;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 一、创建执行线程的方式三：实现 Callable 接口。 相较于实现 Runnable 接口的方式，方法可以有返回值，并且可以抛出异常。
 * 
 * 二、执行 Callable 方式，需要 FutureTask 实现类的支持，用于接收运算结果。  FutureTask 是  Future 接口的实现类
 */
public class CallableTest {


	@Test
	public void test1() {
		ThreadDemo1 td = new ThreadDemo1();
		//1.执行 Callable 方式，需要 FutureTask 实现类的支持，用于接收运算结果。
		FutureTask<Integer> result = new FutureTask<>(td);
		new Thread(result).start();
		//2.接收线程运算后的结果
		try {
			//FutureTask 可用于 闭锁， get方法自带闭锁功能
			Integer sum = result.get();
			System.out.println(sum);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void test2() {
		ThreadDemo1 td = new ThreadDemo1();
		FutureTask<Integer> result = new FutureTask<>(td);
		Integer sum = 0;
		for (int i = 1; i <= 10 ; i ++) {
			new Thread(result).start();
			try {
				sum += result.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		System.out.println(sum);
	}

}

class ThreadDemo1 implements Callable<Integer> {

	@Override
	public Integer call() throws Exception {
		int sum = 0;
		
		for (int i = 0; i <= 1000; i++) {
			sum += i;
		}
		
		return sum;
	}
	
}