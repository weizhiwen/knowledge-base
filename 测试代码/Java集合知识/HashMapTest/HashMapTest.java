import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class HashMapTest {
    // 测试 initCapacity 2 次幂化方法
    public static void testTableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        System.out.println(n+1);
    }

    // 测试 HashMap 中的一些参数值
    public static void testEveryFields(int initCapacity) throws Exception {
        HashMap<Integer, Integer> hashMap = new HashMap<>(initCapacity);
        // hashMap.put(1, 1); 验证在 HashMap 为空的情况下，第一次 put 操作，threshold 值的变化
        Class<?> mapType = hashMap.getClass();
        // capacity 方法没有使用关键字修饰，只能在同类或同包下访问，外部是不能直接访问的，这里使用反射设置可以访问
        Method capacityMethod = mapType.getDeclaredMethod("capacity");
        capacityMethod.setAccessible(true);
        System.out.println("capacity：" + capacityMethod.invoke(hashMap));
        // threshold 属性没有使用关键字修饰，只能在同类或同包下访问，外部是不能直接访问的，这里使用反射设置可以访问
        Field thresholdField = mapType.getDeclaredField("threshold");
        thresholdField.setAccessible(true);
        System.out.println("threshold：" + thresholdField.get(hashMap));
        // size 属性可以直接获得
        System.out.println("size：" + hashMap.size());
        // loadFactor 属性没有使用关键字修饰，并且 loadFactor() 方法也没有使用关键字修饰，都只能在同类或同包下访问，外部是不能直接访问的，这里使用反射设置可以访问
        Field loadFactorField = mapType.getDeclaredField("loadFactor");
        loadFactorField.setAccessible(true);
        System.out.println("loadFactor：" + loadFactorField.get(hashMap));
        Field modCountField = mapType.getDeclaredField("modCount");
        modCountField.setAccessible(true);
        System.out.println("modCount：" + modCountField.get(hashMap));
    }

    // 注意：这里我为了省事就直接抛出所有异常了，没有对异常进行处理
    public static void main(String[] args) throws Exception {
        // testTableSizeFor(15);
        testEveryFields(10);
        testEveryFields(50);
    }
}