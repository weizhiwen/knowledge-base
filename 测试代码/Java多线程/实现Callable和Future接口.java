import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class 实现Callable和Future接口 {
	public static void main(String[] args) {
		MyCall myCall = new MyCall();
		FutureTask<Integer> futureTask = new FutureTask<>(myCall);
		// 创建 5 个线程
		for(int i= 0; i < 5; i++) {
			new Thread(futureTask, "线程"+(i+1)).start();
		}
	}
}

class MyCall implements Callable<Integer> {
	@Override
	// 方法体的内容
	public Integer call() throws Exception {
		int i;
		for (i = 0; i < 5; i++) {
			System.out.println(Thread.currentThread().getName() + "运行 " + (i+1) + " 次!");
		}
		return i;
	}
	
}