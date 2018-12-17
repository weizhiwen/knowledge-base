public class 实现Runnable接口 {
	public static void main(String[] args) {
		// 方法执行体
		MyRun myRun = new MyRun();
		// 创建 5 个线程
		for(int i= 0; i < 5; i++) {
			new Thread(myRun, "线程"+(i+1)).start();
		}
	}
}

class MyRun implements Runnable {
	@Override
	public void run() {
		for (int i = 0; i < 5; i++) {
			System.out.println(Thread.currentThread().getName() + "运行 " + (i+1) + " 次!");
		}
	}
	
}