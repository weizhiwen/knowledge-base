**volatile 的特性：**

1、可以保证代码的可见性，但不能保证代码的原子性。

- 线程修改后的共享变量值能够及时从线程的工作内存刷新到主内存中
- 其他线程能够及时把共享变量的最新值从主内存中刷新到线程的工作内存

2、禁止指令重排序避免多线程带来的问题。

- 重排序：程序实际执行的顺序与代码书写的顺序不同，指令重排序是编译器或处理器为了提高程序性能而做的优化，重排序不会给单线程程序带来内存可见性问题，但在多线程程序中，可能会造成内存可见性问题。
- as-if-serial 语义：无论如何重排序，程序执行的结果应该与代码执行的结果一致，Java 编译器、处理器都会保证 Java 程序在**单线程**下遵循 as-if-serial 语义。

**volatile 实现内存可见性的原理：**

总的来说，是通过加入内存屏障和禁止重排序优化来是实现的。

- 对 volatile 变量执行写操作时，会在写操作之后加入一条 store 屏障指令（变量修改后的值从线程的工作内容刷新到主内存）
- 对 volatile 变量执行读操作时，会在读操作之前加入一条 load 屏障指令（在读变量的值之前将主内存中最新值刷新到线程的工作内存中）

**volatile 使用的场合：**

想要在多线程中安全的使用 volatile 关键字修饰变量，必须同时满足下面两个条件：

1、对变量的写入操作不依赖当前变量的值

- i++、i += 1、i = i+ 1都是不满足该条件的
- boolean 变量、记录温度变化的变量是满足该条件的

2、该变量没有包含在具有其他变量的不变式中

- 不变式 low（volatile 变量） < up 就不满足该条件

**volatile 和 synchronized 比较：**

- volatile 不需要加锁，比 synchronized 更轻量级，不会阻塞线程
- 从内存可见性角度看，volatile 读相当于加锁，volatile 写相当于解锁
- synchronized 既可以保证可见性，又可以保证原子性，而 volatile 只能保证可见性，不能保证原子性

测试代码；

``` java
/**
 * volatile 关键字测试
 * 
 * @author Administrator
 *
 */
public class VolatileTest {
    // 可以加上和去掉 volatile 关键字分别运行
	private volatile boolean flag = false;

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public static void main(String[] args) {
		VolatileTest volatileTest = new VolatileTest();
		// 创建子线程
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				volatileTest.setFlag(true);
				System.out.println("flag = " + volatileTest.isFlag());
			}
		});
		thread.start();
		// 主线程
		while (true) {
			if (volatileTest.isFlag()) {
				System.out.println("读取到子线程修改的值");
				break;
			}
		}

	}
}
```