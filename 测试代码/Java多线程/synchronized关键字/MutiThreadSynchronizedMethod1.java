/**
 * Created with IntelliJ IDEA.
 * User: wzw
 * Date: 2018/12/28
 * Time: 9:53
 * To change this template use File | Settings | File Templates.
 * Description: 多个线程访问一个对象的同步方法，线程安全，无法并行运行
 */
public class MutiThreadSynchronizedMethod1 implements Runnable {
    private static MutiThreadSynchronizedMethod1 mutiThreadSynchronizedMethod1 = new MutiThreadSynchronizedMethod1();

    @Override
    public void run() {
        method();
    }

    private synchronized void method() {
        System.out.println("线程" + Thread.currentThread().getName() + "正在运行");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("线程" + Thread.currentThread().getName() + "运行结束");
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(mutiThreadSynchronizedMethod1);
        Thread thread2 = new Thread(mutiThreadSynchronizedMethod1);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println("finished");
    }

}
