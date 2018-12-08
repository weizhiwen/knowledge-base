import java.util.LinkedList;

public class LinkedListTest {
    public static void test01() {
        LinkedList list = new LinkedList<>();
        // 抛出非检查异常
        System.out.println(list.getFirst());
    }

    public static void test02() {
        System.out.println(9 >> 1);
    }

    public static void main(String[] args) {
        // test01();
        test02();
    }
}