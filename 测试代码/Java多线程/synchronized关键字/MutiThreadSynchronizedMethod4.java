/**
 * Created with IntelliJ IDEA.
 * User: wzw
 * Date: 2018/12/28
 * Time: 9:53
 * To change this template use File | Settings | File Templates.
 * Description: 同时访问同步方法与非同步方法，非同步方法不受影响，可以并行访问
 */
public class MutiThreadSynchronizedMethod4 implements Runnable {
    private static MutiThreadSynchronizedMethod4 mutiThreadSynchronizedMethod = new MutiThreadSynchronizedMethod4();

    @Override
    public void run() {
        if (Thread.currentThread().getName().equals("Thread-0")) {
            method1();
        } else {
            method2();
        }
    }

    private synchronized void method1() {
        System.out.println("同步方法的加锁线程" + Thread.currentThread().getName() + "正在运行");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("同步方法的加锁线程" + Thread.currentThread().getName() + "运行结束");
    }

    private void method2() {
        System.out.println("没加锁线程" + Thread.currentThread().getName() + "正在运行");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("没加锁线程" + Thread.currentThread().getName() + "运行结束");
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
