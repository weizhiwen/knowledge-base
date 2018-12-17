public class Lambda实现Runnable接口 {
	public static void main(String[] args) {
		// 创建 5 个线程
		for(int i= 0; i < 5; i++) {
			new Thread(()->{
				for (int j = 0; j < 5; j++) {
					System.out.println(Thread.currentThread().getName() + "运行 " + (j+1) + " 次!");
				}
			},  "线程"+(i+1)).start();
		}
	}
}