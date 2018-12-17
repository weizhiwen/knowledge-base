import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Lambda实现Callable和Future接口 {
	public static void main(String[] args) {
		// 创建 5 个线程
		for(int i= 0; i < 5; i++) {
			FutureTask<Integer> futureTask = new FutureTask<>((Callable<Integer>)()->{
				int j;
				for (j = 0; j < 5; j++) {
					System.out.println(Thread.currentThread().getName() + "运行 " + (j+1) + " 次!");
				}
				return j;
			});
			new Thread(futureTask, "线程"+(i+1)).start();
		}
	}
}