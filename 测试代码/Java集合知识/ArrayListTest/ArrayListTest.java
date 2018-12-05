import java.util.ArrayList;
import java.util.List;

public class ArrayListTest {
    // 测试 null 值
    public static void testNullValue() {
        ArrayList<String> list = new ArrayList<>();
        String e = null;
        String o = "null";
        list.add(e);
        System.out.println(list.contains(e)); // true
        System.out.println(list.contains(o)); // false
        System.out.println(list.contains(null)); // true
    }

    // 测试列表修改前后对 toArray 方法的影响
    public static void testModifyChange() {
        ArrayList<String> list = new ArrayList<>(3);
        list.add("1");
        list.add("2");
        list.add("3");
        System.out.println(list);

        String[] array = new String[list.size()];
        list.toArray(array);
        // 修改前的数组
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }
        list.set(0, "111");
        // 修改后的列表
        System.out.println(list);
        // 修改后的数组
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }
    }

    // 测试大量 add 操作的优化
    public static void testEnsureCapacity() {
        ArrayList<Integer> list1 = new ArrayList<>();
        int addCount = 1_000_000; // 这个值不要太小，否则效果不明显
        // 没有优化
        long begin1 = System.currentTimeMillis();
        for (int i = 0; i < addCount; i++) {
            list1.add(i);
        }
        long end1 = System.currentTimeMillis();
        long cost1 = end1 - begin1;

        ArrayList<Integer> list2 = new ArrayList<>();
        // 有优化
        list2.ensureCapacity(addCount);
        long begin2 = System.currentTimeMillis();
        for (int i = 0; i < addCount; i++) {
            list2.add(i);
        }
        long end2 = System.currentTimeMillis();
        long cost2 = end2 - begin2;

        System.err.println("大量 add 操作没有优化前的时间花费：" + cost1);
        System.err.println("大量 add 操作优化后的时间花费：" + cost2);
    }

    // 测试 subList 方法的影响
    public static void testSubList() {
        ArrayList<Integer> list = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        // 遍历 list 列表
        list.forEach(value -> System.out.printf("%d\t", value));
        System.out.println();
        List<Integer> subList = list.subList(2, 5);
        // 遍历 subList 列表
        subList.forEach(value -> System.out.printf("%d\t", value));
        System.out.println();
        System.out.println("--------修改值会对父列表 list 和子列表 subList 都产生影响--------");
        // 修改 subList 列表的值
        subList.set(1, 22);
        list.forEach(value -> System.out.printf("%d\t", value));
        System.out.println();
        subList.forEach(value -> System.out.printf("%d\t", value));
    }

    public static void main(String[] args) {
        // testNullValue();
        // testModifyChange();
        // testEnsureCapacity();
        // testSubList();
    }
}