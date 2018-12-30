/**
 * Created with IntelliJ IDEA.
 * User: wzw
 * Date: 2018/12/28
 * Time: 9:53
 * To change this template use File | Settings | File Templates.
 * Description: 方法抛异常后，会释放锁。
 * 展示不抛出异常前和抛出异常后的对比：一旦抛出异常，第二个线程会立刻进入同步方法，意味着锁已经释放。
 */
public class MutiThreadSynchronizedMethod7 implements Runnable {
    private static MutiThreadSynchronizedMethod7 mutiThreadSynchronizedMethod = new MutiThreadSynchronizedMethod7();

    @Override
    public void run() {
        if (Thread.currentThread().getName().equals("Thread-0")) {
            method1();
        } else {
            method2();
        }
    }

    private synchronized void method1() {
        System.out.println("将要抛出异常的线程" + Thread.currentThread().getName() + "正在运行");
        try {
            Thread.sleep(3000);
            throw new Exception();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("将要抛出异常的线程" + Thread.currentThread().getName() + "运行结束");
    }

    private synchronized void method2() {
        System.out.println("线程" + Thread.currentThread().getName() + "正在运行");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("线程" + Thread.currentThread().getName() + "运行结束");
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
