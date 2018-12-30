/**
 * Created with IntelliJ IDEA.
 * User: wzw
 * Date: 2018/12/28
 * Time: 9:53
 * To change this template use File | Settings | File Templates.
 * Description: 访问同一个对象的不同的普通同步方法，线程安全，无法并行运行
 */
public class MutiThreadSynchronizedMethod5 implements Runnable {
    private static MutiThreadSynchronizedMethod5 mutiThreadSynchronizedMethod = new MutiThreadSynchronizedMethod5();

    @Override
    public void run() {
        if (Thread.currentThread().getName().equals("Thread-0")) {
            method1();
        } else {
            method2();
        }
    }

    private synchronized void method1() {
        System.out.println("访问方法1的线程" + Thread.currentThread().getName() + "正在运行");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("访问方法1的线程" + Thread.currentThread().getName() + "运行结束");
    }

    private synchronized void method2() {
        System.out.println("访问方法2的线程" + Thread.currentThread().getName() + "正在运行");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("访问方法2的线程" + Thread.currentThread().getName() + "运行结束");
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(mutiThreadSynchronizedMethod);
        Thread thread2 = new Thread(mutiThreadSynchronizedMethod);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println("finished");
    }
}
