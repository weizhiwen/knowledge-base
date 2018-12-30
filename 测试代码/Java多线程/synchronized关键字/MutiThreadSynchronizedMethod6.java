/**
 * Created with IntelliJ IDEA.
 * User: wzw
 * Date: 2018/12/28
 * Time: 9:53
 * To change this template use File | Settings | File Templates.
 * Description: 同时访问静态synchronized和非静态synchronized方法，非静态方法不受影响，并行执行
 */
public class MutiThreadSynchronizedMethod6 implements Runnable {
    private static MutiThreadSynchronizedMethod6 mutiThreadSynchronizedMethod = new MutiThreadSynchronizedMethod6();

    @Override
    public void run() {
        if (Thread.currentThread().getName().equals("Thread-0")) {
            method1();
        } else {
            method2();
        }
    }

    private static synchronized void method1() {
        System.out.println("静态方法线程" + Thread.currentThread().getName() + "正在运行");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("静态方法线程" + Thread.currentThread().getName() + "运行结束");
    }

    private synchronized void method2() {
        System.out.println("非静态方法线程" + Thread.currentThread().getName() + "正在运行");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("非静态方法线程" + Thread.currentThread().getName() + "运行结束");
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
