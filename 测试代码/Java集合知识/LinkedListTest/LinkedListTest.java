import java.util.LinkedList;

public class LinkedListTest {
    public static void testLinkedListException() {
        LinkedList list = new LinkedList<>();
        // 抛出非检查异常
        System.out.println(list.getFirst());
    }

    public static void testBitOperation() {
        // LinkedList 当作链表来使用
        System.out.println(9 >> 1); // 4
    }

    public static void testLinkedListAsLinkList() {
        // LinkedList 当作链表来使用，链表的 index 也是从 0 开始的
        LinkedList<Integer> list = new LinkedList<>();
        list.addFirst(0); // 链表首部插入
        list.add(1); // 链表末端插入
        list.add(2, 2); // 链表指定位置插入
        list.addLast(3); // 链表尾部插入
        printLinkedList(list);
        list.set(0, 10);
        list.remove(1); // 删除指定索引的元素
        printLinkedList(list);
    }

    public static void testLinkedListAsQueue() {
        // LinkedList 当作队列来使用
        LinkedList<Integer> queue = new LinkedList<>();

        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        printLinkedList(queue);
        queue.poll();
        queue.poll();
        printLinkedList(queue);        
    }

    public static void testLinkedListAsStack() {
        // LinkedList 当作栈来使用
        LinkedList<Integer> stack = new LinkedList<>();
        // 对栈进行 push 操作
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        printLinkedList(stack);
        // 对栈进行 pop 操作
        stack.pop();
        stack.poll();
        printLinkedList(stack);
    }

    // 打印 LinkedList 中元素的方法
    private static <T> void  printLinkedList(LinkedList<T> list) {
        for (T value : list) {
            System.out.print(value + " ");
        }
    }

    public static void main(String[] args) {
        // testLinkedListException();
        // testBitOperation();
        // testLinkedListAsLinkList();
        // testLinkedListAsQueue();
        // testLinkedListAsStack();
    }
}