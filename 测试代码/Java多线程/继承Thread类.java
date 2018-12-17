public class 继承Thread类 {
	public static void main(String[] args) {
		// 创建 5 个线程
		for(int i= 0; i < 5; i++) {
			new MyThread("线程"+(i+1)).start();
		}
	}
}

class MyThread extends Thread {
	
	public MyThread(String name) {
		super(name);
	}

	@Override
	// 方法执行体
	public void run() {
		for (int i = 0; i < 5; i++) {
			System.out.println(Thread.currentThread().getName() + "运行 " + (i+1) + " 次!");
		}
	}
}