- **问题：ArrayList 的 size 和 capacity 怎么理解？**
如果把 ArrayList 看作一个杯子的话，capacity 就是杯子的容积，也就是代表杯子能装多少东西，而 size 就是杯子装的东西的体积。杯子可能装满了，也可能没装满，所以 capacity >= size 。capacity 过大和过小都不好，过大会造成浪费，过小又存放不下多个元素的值，capacity == size，则 ArrayList 空间利用率最大，但是不利于添加新的元素。当 ArrayList 实例内的元素个数不再改变了，可以使用 trimToSize() 方法最小化 ArrayList 实例来节省空间，也即是使 capacity == size。


- **问题：ArrayList 内部是怎么存放数据的？**
ArrayList 可以看做是数组的封装，使用 elementData 数组来存储数据，使用 size 来代表 elementData 的非 null 元素个数。elementData 前没有访问修饰符，所以只有同类和同包下的类可以直接方法，所以外界想要知道 ArrayList 实例内元素的个数就要通过 size 属性。elementData 数组类型是 Object 类型，可以存放任意的引用类型，不能存放基本的数据类型。


- **问题：ArrayList 类常量 EMPTY_ELEMENTDATA 和 DEFAULTCAPACITY_EMPTY_ELEMENTDATA 怎么理解？是不是多余？**
这两个类常量都是代表空 Object 数组，都代表 ArrayList 实例的空状态，也即是 elementData 数组中还没有元素。但是 EMPTY_ELEMENTDATA 是使用带初始化值的构造方法（有参构造函数，一个是指定初始容量，一个是指定初始集合）时使用的，DEFAULTCAPACITY_EMPTY_ELEMENTDATA 是使用默认的构造方法，也即是无参的构造方法时使用的。


- **问题：ArrayList 是怎样实现扩充的？**
扩容是发生在添加操作前的，要保证要添加元素在 elementData 数组中有位置，也即是 size 加上要添加的元素个数要小于 capacity（size + num <= capacity 就说明容量是充足的），所以在添加方法中，先调用 ensureCapacityInternal(int) 方法来确保 elementData 容量充足，然后再进行具体的添加操作。如果 ensureCapacityInternal 方法（ensureCapacityInternal 方法中有调用了其他方法）发现数组容量不够了，就会扩容。扩容实际的方法是 grow(int) 方法，使用位运算符来使数组的容量扩容 1.5 倍。但是需要注意的是，没有指定初始化值的 ArrayList 实例，第一次扩容并不是以 1.5 倍扩容的，而是使用的默认容量 10，所以网上很多直接说 ArrayList 扩容是 1.5 倍也有不当之处，这点从 JDK 源码中可以很明确的看出来。
如果在构造 ArrayList 实例时，指定初始化值（初始化容量或者集合），那么就会创建指定大小的 Object 数组，并把该数组对象的引用赋值给 elementData；如果不指定初始化值，在第一次添加元素值时会使用默认的容量大小 10 作为 elementData 数组的初始容量，使用 Arrays.conpyOf() 方法创建一个 Object[10] 数组。


- **问题：Arrays.copyOf 方法和 System.arraycopy 方法的区别？**
Arrays.copyOf(T[], int length) 方法是 Arrays 工具类中用来进行任意类型数组赋值（包括 null 值），并使数组具有指定长度的方法，ArrayList 中用这个方法来实现 elementData 数组的元素移动。但实际上 Arrays.copyOf 方法最终调用的是 System.arraycopy(U[], int srcPos, T[], desPos, int length) 方法，这个方法是一个本地方法，不能直接看源码。U 和 T 都是一种泛型，只是为了便于区分，U 表示的是原始数组（源数组）类型，T 表示的是存放拷贝值的数组（目标数组）类型，srcPos 是指原始数组中的起始位置（从原始数组的哪个位置开始拷贝），desPos 是指存放拷贝值的数组拷贝起始位置（从目标数组的哪个位置插入这些拷贝的值），length 表示要拷贝的元素数量（要从原始数组中拷贝多少个）。


- **问题：ArrayList 的 add 操作优化？**
核心就是避免 ArrayList 内部进行扩容。
1、对于普通少量的 add 操作，如果插入元素的个数已知，最好使用带初始化参数的构造方法，避免 ArrayList 内部再进行扩容，提高性能。
2、对于大量的 add 操作，最好先使用 ensureCapacity 方法来确保 elementData 数组中有充足的容量来存放我们后面 add 操作的元素，避免 ArrayList 实例内部进行扩容。上面提到的 ensureCapacityInternal 方法是一个私有方法，不能直接调用，而 ensureCapacity 方法是一个共有方法，专门提供给开发者使用的，提高大量 add 操作的性能。
测试代码如下：
``` java
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
```

- **问题：subList 方法生成的子列表会对父列表产生影响吗？**
会，不管使修改子列表的值还是修改父列表的值都会对双方产生影响。阅读源码，就会发现，subList 方法后的子列表对元素的操作实际上调用的还是父列表中对应的方法。

- **问题：ArrayList 中既有 Itr 迭代器类，又有 ListItr 迭代器类，该用哪个？**
集合可以看作是数组的包装类型，遍历并不像数组那样方便，迭代器是为了迭代集合中的元素而存在的。Itr 迭代器类实现了 Iterator 接口，ListItr 迭代器类继承 Itr 迭代器类，并且实现了 ListIterator 接口，所以 ListItr 类的功能比 Itr 类更强大。Itr 类在迭代过程中不能修改 List 的结构（如 add 操作），否则会抛出并发修改异常 ConcurrentModificationException，并且在 next 方法之后才能 remove 元素，而 ListItr 类还支持在迭代过程中添加元素，对于 List 集合元素操作更加友好。所以对于 List 集合迭代，最好使用 ListItr 迭代器类。


ArrayList 源码（如无特殊说明，都是基于 JDK1.8 的）：
``` java
/*
 * Copyright (c) 1997, 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 版权所有（c）1997,2017，Oracle和/或其附属公司。版权所有。
 * ORACLE 所有权/机密。使用须遵守许可条款。
 */

package java.util;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import sun.misc.SharedSecrets;

/**
 * Resizable-array implementation of the <tt>List</tt> interface.  Implements
 * all optional list operations, and permits all elements, including
 * <tt>null</tt>.  In addition to implementing the <tt>List</tt> interface,
 * this class provides methods to manipulate the size of the array that is
 * used internally to store the list.  (This class is roughly equivalent to
 * <tt>Vector</tt>, except that it is unsynchronized.)
 * 可调整大小的数据实现自 List 接口，实现了所有可选择的列表操作的方法和所有的元素，包括 null。
 * 除了实现 List 接口外，这个类（ArrayList） 还提供了一些方法用来控制存储列表内部的数组大小。
 * （除了它不是同步的外，这个类大致相当于 Vector（类））
 * 
 * 
 * <p>The <tt>size</tt>, <tt>isEmpty</tt>, <tt>get</tt>, <tt>set</tt>,
 * <tt>iterator</tt>, and <tt>listIterator</tt> operations run in constant
 * time.  The <tt>add</tt> operation runs in <i>amortized constant time</i>,
 * that is, adding n elements requires O(n) time.  All of the other operations
 * run in linear time (roughly speaking).  The constant factor is low compared
 * to that for the <tt>LinkedList</tt> implementation.
 * size、isEmpty、get、set、iterator 和 listIterator 这些操作（方法）运行在恒定时间（时间复杂度都是 O(1)）。
 * add 这个操作（方法）运行在均摊恒定时间，添加 n 个元素需要 O(n) 时间。
 * 其他所有的操作（方法）运行在线性时间（粗略的讲）。
 * 这个常数因子（10）相比于 LinkedList 实现类来说是比较低的。
 * 
 * 
 * <p>Each <tt>ArrayList</tt> instance has a <i>capacity</i>.  The capacity is
 * the size of the array used to store the elements in the list.  It is always
 * at least as large as the list size.  As elements are added to an ArrayList,
 * its capacity grows automatically.  The details of the growth policy are not
 * specified beyond the fact that adding an element has constant amortized
 * time cost.
 * 每个 ArrayList 实例都有一个 capacity，这个 capacity 是被用来在列表中存储元素的数组大小。
 * capacity 总是至少和列表的大小一样大。
 * 当元素被添加到 ArrayList 中，列表的 capacity 也会自动增长。
 * 除了添加一个元素有恒定均摊时间花费这个事实外，增长策略的细节没有做规定。
 * 
 * 
 * <p>An application can increase the capacity of an <tt>ArrayList</tt> instance
 * before adding a large number of elements using the <tt>ensureCapacity</tt>
 * operation.  This may reduce the amount of incremental reallocation.
 * 一个程序在添加大量的元素之前可以先增加 ArrayList 实例的 capacity，通过使用 ensureCapacity 方法可能会减* 少重新分配增量的次数。
 * 
 * 
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access an <tt>ArrayList</tt> instance concurrently,
 * and at least one of the threads modifies the list structurally, it
 * <i>must</i> be synchronized externally.  (A structural modification is
 * any operation that adds or deletes one or more elements, or explicitly
 * resizes the backing array; merely setting the value of an element is not
 * a structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the list.
 * 注意：这个实现类（ArrayList） 不是同步的。
 * 如果多个线程并发访问一个 ArrayList 实例，并且至少有一个线程在结构上修改了列表，那么这个线程必须在
 * （ArrayList 的）方法外部进行同步操作。（结构上的修改是添加删除一个或多个元素，或者是显示调整后备数组的
 * 大小，仅仅是设置值（使用 set 方法）不算是结构上的修改。）
 * 这通常是通过在列表上自然封装的一些对象进行同步操作来完成的。
 * 
 * 
 * If no such object exists, the list should be "wrapped" using the
 * {@link Collections#synchronizedList Collections.synchronizedList}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the list:<pre>
 *   List list = Collections.synchronizedList(new ArrayList(...));</pre>
 * 如果不存在此类对象，列表应该用 Collections.synchronizedList 静态方法包装。
 * 这（使用包装列表）应该在创建的时候做，为了防止非同步的访问列表（示例代码如下）：
 * List list = Collections.synchronizedList(new ArrayList(...));
 *
 * 
 * <p><a name="fail-fast">
 * The iterators returned by this class's {@link #iterator() iterator} and
 * {@link #listIterator(int) listIterator} methods are <em>fail-fast</em>:</a>
 * if the list is structurally modified at any time after the iterator is
 * created, in any way except through the iterator's own
 * {@link ListIterator#remove() remove} or
 * {@link ListIterator#add(Object) add} methods, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of
 * concurrent modification, the iterator fails quickly and cleanly, rather
 * than risking arbitrary, non-deterministic behavior at an undetermined
 * time in the future.
 * 这个类（ArrayList）的 iterator() 方法返回出来的迭代器和 listIterator 方法都是 fail-fast 的。
 * 如果列表在迭代器创建之后在结构上被修改，除了调用迭代器的 remove 方法和 add 方法外，迭代器都会抛出
 * ConcurrentModificationException 异常。
 * 因此，在并发修改情况下，迭代器快速干净地出 fail，而不是在未来某个不确定的时间，冒任意和不确定的风险。
 * 
 * 
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:  <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 * 注意：迭代器的 fail-fast 行为可能不像它保证的那样，一般来说，在非同步并发修改情况下，不可能去给出
 * 任何硬性的保证。
 * fail-fast 的迭代器会尽最大的努力抛出 ConcurrentModificationException 异常。
 * 因此，写程序依赖这个异常为了正确性这点是错误的，迭代器的 fail-fast 行为仅仅被用来检查（程序中的） bug。
 * 
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 * 这个类（ArrayList）是 Java 集合框架的一员。
 * 
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see     Collection
 * @see     List
 * @see     LinkedList
 * @see     Vector
 * @since   1.2
 * 编写者：Josh Bloch、Neal Gafter
 * 参看：Collection、List、LinkedList、Vector
 * 这个类自从 Java 1.2 就有了
 */

public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    // 序列号
    private static final long serialVersionUID = 8683452581122892189L;

    /**
     * Default initial capacity.
     * 默认的初始化容量，10。
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Shared empty array instance used for empty instances.
     * （初始化容量为 0或者初始化集合为空）空实例共享此空数组（私有静态不可变变量）。
     */
    private static final Object[] EMPTY_ELEMENTDATA = {};

    /**
     * Shared empty array instance used for default sized empty instances. We
     * distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when
     * first element is added.
     * （没有指定初始化）默认（容量）大小（10）空实例共享此空数组（私有静态不可变变量），我们将它和 EMPTY_ELEMENTDATA 区分开来是为了知道当第一元素被添加时需要扩容多少。
     */
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    /**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer. Any
     * empty ArrayList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * will be expanded to DEFAULT_CAPACITY when the first element is added.
     * 存储 ArrayList 元素的数组。ArrayList 的容量是这个数组的长度。
     * 当添加第一个元素时，任何带有 elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA 的空 ArrayList
     * 都将扩展为 DEFAULT_CAPACITY。
     * 
     * transient 关键字修饰，序列化时该值不会被带上
     */
    transient Object[] elementData; // non-private to simplify nested class access 非私有方便内部嵌套类访问

    /**
     * The size of the ArrayList (the number of elements it contains).
     * ArrayList 的大小（ArrayList 包含的元素数量）
     * 
     * @serial
     * 对象序列化时被带上
     */
    private int size;

    /**
     * Constructs an empty list with the specified initial capacity.
     * 使用指定初始化容量构造一个空列表
     * 
     * @param  initialCapacity  the initial capacity of the list
     * initialCapacity 参数为这个列表的初始化容量
     * @throws IllegalArgumentException if the specified initial capacity
     *         is negative
     * 抛出 IllegalArgumentException 异常，如果指定的初始化容量是负数
     */
    public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            // 创建指定初始化容量的 Object 数组
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            // 使用有指定初始化值的共享空实例
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            // 抛出 IllegalArgumentException 异常
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     * 使用初始化容量 10 来构造一个空列表
     */
    public ArrayList() {
        // 使用无指定初始化值的共享空实例，和上面的空实例区分开
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     * 使用包含元素（元素个数可能为 0）的指定集合构造列表，并按照这个集合的迭代器返回元素顺序构造
     * 
     * @param c the collection whose elements are to be placed into this list
     * c 参数为要将它的元素放入列表的集合
     * @throws NullPointerException if the specified collection is null
     * 抛出 NullPointerException（空指针）异常，如果指定的集合为 null
     */
    public ArrayList(Collection<? extends E> c) {
        // 如果集合 c 为 null，则在调用 toArray 方法时会抛出空指针异常
        elementData = c.toArray();
        if ((size = elementData.length) != 0) {
            // c.toArray might (incorrectly) not return Object[] (see 6260652)
            // c.toArray 是具体的集合类中的方法，可能并不会正确的返回 Object 数组，所以这里要判断一下，
            // 如果不是 Object 数组，就通过 Arrays 工具类的 copyOf 方法转换成 Object 数组
            if (elementData.getClass() != Object[].class)
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        } else {
            // replace with empty array.
            // 使用有指定初始化值的共享空实例
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }

    /**
     * Trims the capacity of this <tt>ArrayList</tt> instance to be the
     * list's current size.  An application can use this operation to minimize
     * the storage of an <tt>ArrayList</tt> instance.
     * 调整 ArrayList 实例的 capacity 为列表当前的 size。
     * 程序可以使用这个方法最小化 ArrayList 实例的存储（节省不需要的空间）。
     */
    public void trimToSize() {
        // 修改次数加 1
        modCount++;
        // 在 size 小于数组的长度（数组中存在 null 引用）前提下
        // 如果 size == 0 ，说明数组中都是 null 引用，就让 elementData 指向 EMPTY_ELEMENTDATA，
        // 否则，就拷贝 size 长度的 elementData 数组元素，再让 elementData 指向这个数组对象
        if (size < elementData.length) {
            elementData = (size == 0)
              ? EMPTY_ELEMENTDATA
              : Arrays.copyOf(elementData, size);
        }
    }

    /**
     * Increases the capacity of this <tt>ArrayList</tt> instance, if
     * necessary, to ensure that it can hold at least the number of elements
     * specified by the minimum capacity argument.
     * 如果有需要，增加 ArrayList 实例的容量值，来确保它可以容纳至少
     * 指定的最小容量（minCapacity）数量的元素。
     * 
     * @param   minCapacity   the desired minimum capacity
     * minCapacity 参数为列表所需的最小容量
     */
    public void ensureCapacity(int minCapacity) {
        // 如果 elementData ！= DEFAULTCAPACITY_EMPTY_ELEMENTDATA（未指定初始化值的 ArrayList 实例），则 minExpand = 0
        // 否则 minExpand = DEFAULT_CAPACITY（10）
        int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
            // any size if not default element table
            ? 0
            // larger than default for default empty table. It's already
            // supposed to be at default size.
            : DEFAULT_CAPACITY;
        // 如果 minCapacity > minExpand，一种情况是 minCapacity > 0，另一种情况是 minCapacity > 10
        if (minCapacity > minExpand) {
            ensureExplicitCapacity(minCapacity);
        }
    }

    // 类内私有方法，对象无法直接方法，供内部其他方法使用
    // 计算列表的容量
    private static int calculateCapacity(Object[] elementData, int minCapacity) {
        // 如果 elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA（未指定初始化值的 ArrayList 实例），则返回 DEFAULT_CAPACITY（10） 和 minCapacity 数值最大的一个作为列表的容量
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            return Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        // 否则（不是未指定初始化值得 ArrayList 实例），直接返回 minCapacity
        return minCapacity;
    }

    // 类内私有方法，对象无法直接方法，供内部其他方法使用
    // 确保 ArrayList 内部容量充足
    private void ensureCapacityInternal(int minCapacity) {
        ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
    }

    // 类内私有方法，对象无法直接方法，供内部其他方法使用
    // 确保已经有明确的容量
    private void ensureExplicitCapacity(int minCapacity) {
        // 修改次数加 1
        modCount++;

        // overflow-conscious code
        // 有溢出意识的代码
        // 当 minCapacity 大于 elementData 数组长度时，再调用 grow 方法。
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     * 能分配的最大数组大小，Integer.Max_VALUE - 8 
     * 为什么最大数组大小不是 Integer 最大值？因为一些虚拟机（VMs）在数组中会保留一些头消息，会占用一些空间
     * 尝试分配比这个（规定最大）值更大的值可能会导致 OutOfMemoryError：请求的数组大小超过了虚拟机的限制
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     * 确保 ArrayList 实例可以容纳至少指定的最小容量（minCapacity）数量的元素。
     * 
     * @param minCapacity the desired minimum capacity
     * minCapacity 参数为列表所需的最小容量
     */
    private void grow(int minCapacity) {
        // overflow-conscious code
        // 有溢出意识的代码

        // oldCapacity 的值为 elementData 中数组长度值
        int oldCapacity = elementData.length;
        // 使用位运算提高运算速度，newCapacity 是 oldCapacity 的 1.5 倍。
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        // 如果 oldCapacity 的 1.5 倍还比 minCapacity 小，那么 newCapacity = minCapacity
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        // 如果 oldCapacity 的 1.5 倍比 MAX_ARRAY_SIZE 大，那么调用 hugeCapacity 做点事情
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);

        // minCapacity is usually close to size, so this is a win:
        // minCapacity 的值通常接近于 size 的值，所以这就是个胜利（节省空间）

        // 最终 elementData 指向复制了 newCapacity 的新数组对象
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    // 对于巨大的容量的做法
    private static int hugeCapacity(int minCapacity) {
        // 如果 minCapacity < 0 就直接抛出 OutOfMemoryError
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        // 否则如果 minCapacity > MAX_ARRAY_SIZE，返回 Integer.MAX_VALUE，或者返回 MAX_ARRAY_SIZE 值
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }

    /**
     * Returns the number of elements in this list.
     * 返回列表中元素的数量
     * @return the number of elements in this list
     * 返回值是列表中元素的数量
     */
    public int size() {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this list contains no elements.
     * 如果列表中没有元素则返回 true
     * 
     * @return <tt>true</tt> if this list contains no elements
     * 返回值为列表的 size 是否等于 0 
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this list contains
     * at least one element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     * 如果列表包含指定的元素则返回 true，
     * 更正式的说是，当且仅当列表包含至少一个元素 e，
     * 就像这样 return (o == null ? e == null : o.equals(e))
     * 如果测试对象 o 为 null，那么这一个元素 e 指向 null，返回 true
     * 否则就用 o 调用 equals 方法，判断 o 和 e 是否相等来决定返回值
     * 
     * @param o element whose presence in this list is to be tested
     * o 参数为在这个列表中被测试的元素
     * @return <tt>true</tt> if this list contains the specified element
     * 返回值为 o 在列表中的位置是否大于 0
     */
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     * 返回列表中第一次出现指定元素的索引（下标），如果列表中不存在这个（指定）元素就返回 -1
     * 更正式的说是，返回最小的索引，
     * 就像这样 (o == null ? get(i) == null : o.equals(get(i)))
     * 或者（列表中）没有该元素的索引就返回 -1
     */
    public int indexOf(Object o) {
        // 判断 o 是不是指向 null
        if (o == null) {
            // 如果 o 指向 null，正序遍历元素列表（注意这里用 size，不用 capacity），如果找到就返回索引值
            for (int i = 0; i < size; i++)
                if (elementData[i]==null)
                    return i;
        } else {
            // 如果 o 不指向 null，正序遍历元素列表，使用元素的 equals 方法判断元素值，如果找到就返回索引值
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i]))
                    return i;
        }
        // 找不到返回 -1
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     * 返回列表中最后一次出现指定元素的索引（下标），如果列表中不存在这个（指定）元素就返回 -1
     * 更正式的说是，返回最小的索引，
     * 就像这样 (o == null ? get(i) == null : o.equals(get(i)))
     * 或者（列表中）没有该元素的索引就返回 -1
     */
    public int lastIndexOf(Object o) {
        // 判断 o 是不是指向 null
        if (o == null) {
            // 如果 o 指向 null，倒序遍历元素列表（注意这里用 size，不用 capacity），如果找到就返回索引值
            for (int i = size-1; i >= 0; i--)
                if (elementData[i]==null)
                    return i;
        } else {
            // 如果 o 不指向 null，正序遍历元素列表，使用元素的 equals 方法判断元素值，如果找到就返回索引值
            for (int i = size-1; i >= 0; i--)
                if (o.equals(elementData[i]))
                    return i;
        }
        // 找不到返回 -1
        return -1;
    }

    /**
     * Returns a shallow copy of this <tt>ArrayList</tt> instance.  (The
     * elements themselves are not copied.)
     * 返回 ArrayList 实例的浅拷贝（元素本身没有拷贝，只是把数组中的对象引用拷贝了一遍）
     * 
     * @return a clone of this <tt>ArrayList</tt> instance
     * 返回值是 ArrayList 实例的克隆
     */
    public Object clone() {
        try {
            // 只是复制了 ArrayList 数组对象引用（栈中），没有拷贝具体的对象（堆中）
            ArrayList<?> v = (ArrayList<?>) super.clone();
            // elementData 复制和 modCount 值复制
            v.elementData = Arrays.copyOf(elementData, size);
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            // 这不应该发生，因为我们已经声明了 Cloneable
            throw new InternalError(e);
        }
    }

    /**
     * Returns an array containing all of the elements in this list
     * in proper sequence (from first to last element).
     * 以适当的顺序（从第一个到最后一个元素）返回一个包含列表中所有元素的数组
     * 
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     * 这个返回的数组是安全的，列表中没有保留对数组元素值的引用（换句话说，这个方法必须分配一个新的数组）
     * 调用者因此可以自由修改返回的数组（对列表中的值不会有影响，反过来，列表中的值修改也不会影响数组中的值）
     * 
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     * 这个方法充当基于数组和基于集合API的桥梁（集合与数组的转换）
     *
     * @return an array containing all of the elements in this list in
     *         proper sequence
     * 返回值为以适当的顺序包含列表中所有元素的数组
     */
    public Object[] toArray() {
        // copyOf 方法最终调用的是 System.arraycopy 静态方法，并且是个本地方法，无法查看源代码
        return Arrays.copyOf(elementData, size);
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element); the runtime type of the returned
     * array is that of the specified array.  If the list fits in the
     * specified array, it is returned therein.  Otherwise, a new array is
     * allocated with the runtime type of the specified array and the size of
     * this list.
     * 以适当的顺序（从第一个到最后一个元素）返回一个包含列表中所有元素的数组；
     * 返回数组的运行类型是指定数组的类型。
     * 如果列表适合指定的数组，则返回到指定数组中（指定数组长度和列表 size 大小相等）
     * 否则一个以指定数组的类型为运行类型，大小为列表 size 的新数组将被分配
     * 
     * <p>If the list fits in the specified array with room to spare
     * (i.e., the array has more elements than the list), the element in
     * the array immediately following the end of the collection is set to
     * <tt>null</tt>.  (This is useful in determining the length of the
     * list <i>only</i> if the caller knows that the list does not contain
     * any null elements.)
     * 如果列表适合指定的数组并且还有剩余空间（即指定数组比列表有更多的元素），在数组中紧跟集合末尾的元素被设置为 null。（仅当调用者知道列表中不包含任何 null 元素，在决定列表长度时才是有用的）
     *
     * @param a the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * a 参数是一个用来存储列表元素的数组，前提是这个数组足够大；否则，一个以转换目的和指定数组相同类型的数组将会被分配
     * @return an array containing the elements of the list
     * 返回值为包含列表元素的数组
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this list
     * 抛出 ArrayStoreException 异常，如果指定数组的类型不是列表元素类型的超类型
     * @throws NullPointerException if the specified array is null
     * 抛出 NullPointerException 异常，如果指定的数组是 null
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        // 如果指定的数组长度小于 size，返回一个以列表元素填充的新数组
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        // 数组拷贝，System.arraycopy 方法为本地方法
        System.arraycopy(elementData, 0, a, 0, size);
        // 如果指定数组长度大于 size，超过列表 size 的部分赋值为 null
        if (a.length > size)
            a[size] = null;
        // 再将填充后指定的数组返回
        return a;
    }

    // Positional Access Operations
    // 按位置访问操作（方法）
    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) elementData[index];
    }

    /**
     * Returns the element at the specified position in this list.
     * 返回列表中的指定位置的元素
     * 
     * @param  index index of the element to return
     * index 参数为要返回元素的下标
     * @return the element at the specified position in this list
     * 返回值为列表中指定位置的元素
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * 抛出 IndexOutOfBoundsException 异常
     */
    public E get(int index) {
        // 边界检查
        rangeCheck(index);

        return elementData(index);
    }

    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     * 用指定元素替换列表中指定位置的元素值
     *
     * @param index index of the element to replace
     * index 参数为要替换的元素的下标
     * @param element element to be stored at the specified position
     * element 参数为要存储到指定位置的元素值
     * @return the element previously at the specified position
     * 返回值为先前指定位置的旧元素值
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * 抛出 IndexOutOfBoundsException 异常
     */
    public E set(int index, E element) {
        // 边界检查
        rangeCheck(index);
        // 保存旧值
        E oldValue = elementData(index);
        // 替换成新值
        elementData[index] = element;
        // 返回旧值
        return oldValue;
    }

    /**
     * Appends the specified element to the end of this list.
     * 添加指定元素到列表末尾
     * 
     * @param e element to be appended to this list
     * e 参数为要添加到列表的元素
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     * 返回值为 true（当被指定为集合时）
     */
    public boolean add(E e) {
        // 确保在 ArrayList 实例的 size 基础上加 1，容量仍然充足，扩充是以 elementData 数组长度的 1.5 倍扩的
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        // 这里一行代码实现了两步，一步是 size 加 1，另一步是将 e 添加到 elementData 末尾
        elementData[size++] = e;
        return true;
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     * 插入指定的值到列表指定的位置。
     * 将当前位置的元素（如果有的话）和任何后续的元素向右移动（将它们的索引加 1）
     *
     * @param index index at which the specified element is to be inserted
     * index 参数为要被插入的指定元素的索引
     * @param element element to be inserted
     * element 参数为要插入的元素
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * 抛出 IndexOutOfBoundsException 异常
     */
    public void add(int index, E element) {
        // add 方法的边界检查
        rangeCheckForAdd(index);
        // 确保在 ArrayList 实例的 size 基础上加 1，容量仍然充足
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        // 元素向后移动是通过 System.arraycopy 方法实现的
        System.arraycopy(elementData, index, elementData, index + 1,
                         size - index);
        // 数组指定位置赋值
        elementData[index] = element;
        // ArrayList 的 size 要加 1
        size++;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     * 移除列表指定位置的元素，将后续的元素向左移动（将它们的减 1）
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E remove(int index) {
        // 边界检查
        rangeCheck(index);

        // 修改次数加 1
        modCount++;
        // 保存旧值
        E oldValue = elementData(index);

        // 计算要移动的元素个数
        int numMoved = size - index - 1;
        if (numMoved > 0)
            // 使用 System.arraycopy 方法实现元素向左移动
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        // 一行代码，两步操作，一步是将 size 减 1，另一步是将最后一个元素指向 null，让垃圾回收器清理没有引用的对象
        elementData[--size] = null; // clear to let GC do its work
        // 返回旧值
        return oldValue;
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If the list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists).  Returns <tt>true</tt> if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     * 移除列表中第一次出现的指定元素，前提是这个元素值存在。
     * 如果列表不包含这个元素，列表不会改变。
     * 
     *
     * @param o element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     */
    public boolean remove(Object o) {
        // 如果 o 指向 null，遍历 elementData 数组，如果找到了指定元素就删除并返回 true
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    fastRemove(index);
                    return true;
                }
        } else {
            // 否则，遍历 elementData 数组，使用 equals 方法判断相等，如果找到了指定元素就删除并返回 true
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true;
                }
        }
        // 没有找到，返回 false
        return false;
    }

    /*
     * Private remove method that skips bounds checking and does not
     * return the value removed.
     * 私有的删除方法，跳过了边界检查并且不返回删除的值
     */
    private void fastRemove(int index) {
        // 修改次数加 1
        modCount++;
        // 计算要移动的元素的个数
        int numMoved = size - index - 1;
        if (numMoved > 0)
            // 使用 System.arraycopy 方法实现元素向左移动
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        // 一行代码，两步操作，一步是将 size 减 1，另一步是将最后一个元素指向 null，让垃圾回收器清理没有引用的对象
        elementData[--size] = null; // clear to let GC do its work
    }

    /**
     * Removes all of the elements from this list.  The list will
     * be empty after this call returns.
     * 移除列表中的所有元素，列表将会在调用返回后置为空
     */
    public void clear() {
        // 修改次数加 1
        modCount++;

        // clear to let GC do its work
        // elementData 数组中的引用都置为 null，让垃圾回收器清除没有引用的对象
        for (int i = 0; i < size; i++)
            elementData[i] = null;

        size = 0;
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the
     * specified collection's Iterator.  The behavior of this operation is
     * undefined if the specified collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified collection is this list, and this
     * list is nonempty.)
     * 将指定集合中所有的元素添加到列表末尾，按照指定集合中迭代器的顺序返回元素。
     * 如果指定集合在操作过程中被修改了，那么这个操作（方法）的行为时不确定的。
     * （这意味着如果指定集合是这个列表，并且这个列表不为空，调用此方法是不确定的）
     *
     * @param c collection containing elements to be added to this list
     * c 参数为要添加到列表中包含元素的集合
     * @return <tt>true</tt> if this list changed as a result of the call
     * 如果列表作为调用的结果被改变了，则返回 ture
     * @throws NullPointerException if the specified collection is null
     * 抛出 NullPointerException 异常，如果指定集合为 null
     */
    public boolean addAll(Collection<? extends E> c) {
        // 集合转数组
        Object[] a = c.toArray();
        // 集合中的元素个数，也即是 Object 数组的长度
        int numNew = a.length;
        // 确保在 ArrayList 实例的 size 基础上加上集合的元素个数，容量仍然充足
        ensureCapacityInternal(size + numNew);  // Increments modCount
        // 使用 System.arraycopy 方法实现添加集合中的元素
        System.arraycopy(a, 0, elementData, size, numNew);
        // ArrayList 的 size 要加上新增的元素个数
        size += numNew;
        // 返回新增元素个数是否不等于 0
        return numNew != 0;
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     * 从指定位置开始，将指定集合中的所有元素插入了列表中。
     * 移动先前位置的元素（如果有的话）以及任何后续的元素到右边（增加它们的索引）。
     * 新元素将按照指定集合中​​迭代器返回的顺序出现在列表中。
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * index 参数为从指定集合中插入的第一个元素的索引
     * @param c collection containing elements to be added to this list
     * c 参数为要添加到列表中包含元素的集合
     * @return <tt>true</tt> if this list changed as a result of the call
     * 如果列表作为调用的结果被改变了，则返回 ture
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * 抛出 IndexOutOfBoundsException 异常
     * @throws NullPointerException if the specified collection is null
     * 抛出 NullPointerException 异常，如果指定集合为 null
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        // add 操作的边界检查
        rangeCheckForAdd(index);
        // 集合转数组
        Object[] a = c.toArray();
        // 数组长度
        int numNew = a.length;
        // 确保在 ArrayList 实例的 size 基础上加上集合的元素个数，容量仍然充足
        ensureCapacityInternal(size + numNew);  // Increments modCount

        // 要移动的元素的个数
        int numMoved = size - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew,
                             numMoved);
        // 使用 System.arraycopy 方法实现添加集合中的元素
        System.arraycopy(a, 0, elementData, index, numNew);
        // ArrayList 的 size 要加上新增的元素个数
        size += numNew;
         // 返回新增元素个数是否不等于 0
        return numNew != 0;
    }

    /**
     * Removes from this list all of the elements whose index is between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     * This call shortens the list by {@code (toIndex - fromIndex)} elements.
     * (If {@code toIndex==fromIndex}, this operation has no effect.)
     * 删除列表中从 fromIndex（包含）到 toIndex（不包含）索引对应元素
     * 移动任何后续的元素到左边（减少它们的索引）。
     * 慈此调用通过删除 toIndex - fromIndex 之间的元素缩短列表。
     * 如果 toIndex == fromIndex 的话，此操作无效
     *
     * @throws IndexOutOfBoundsException if {@code fromIndex} or
     *         {@code toIndex} is out of range
     *         ({@code fromIndex < 0 ||
     *          fromIndex >= size() ||
     *          toIndex > size() ||
     *          toIndex < fromIndex})
     * 下面几种情况会抛出 IndexOutOfBoundsException 异常。
     * 如果 fromIndex 或者 toIndex 越界
     * 或者 fromIndex < 0
     * 或者 fromIndex >= size
     * 或者 toIndex > size
     * 或者 toIndex < fromIndex
     */
    protected void removeRange(int fromIndex, int toIndex) {
        // 修改次数加 1
        modCount++;
        // 计算要移动的次数
        int numMoved = size - toIndex;
        // 使用 System.arraycopy 方法实现元素向左移动
        System.arraycopy(elementData, toIndex, elementData, fromIndex,
                         numMoved);

        // clear to let GC do its work
        // 计算新的 size 大小
        int newSize = size - (toIndex-fromIndex);
        // 将删除元素的引用置为 null，让垃圾回收器清除没有引用的对象
        for (int i = newSize; i < size; i++) {
            elementData[i] = null;
        }
        size = newSize;
    }

    /**
     * Checks if the given index is in range.  If not, throws an appropriate
     * runtime exception.  This method does *not* check if the index is
     * negative: It is always used immediately prior to an array access,
     * which throws an ArrayIndexOutOfBoundsException if index is negative.
     * 检查是否给定的索引在范围内。如果不再，抛出适当的运行时异常。
     * 这个方法不检查索引是不是负数：它（这个方法）总是在数组访问之前使用，如果索引为负数，
     * 抛出 ArrayIndexOutOfBoundsException 异常
     */
    private void rangeCheck(int index) {
        // 判断索引是否大于等于 size，如果是，则抛出 IndexOutOfBoundsException
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * A version of rangeCheck used by add and addAll.
     * 被 add 和 addAll 使用的边界检查版本
     */
    private void rangeCheckForAdd(int index) {
        // 判断索引是否大于 size 或者小于 0（为负数），如果是，抛出IndexOutOfBoundsException 异常
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     * 构造一个 IndexOutOfBoundsException 的详细消息。
     * 在错误处理代码许多可能的重构中，这种“描述”对服务器和客户端虚拟机都表现最好。
     * 
     */
    private String outOfBoundsMsg(int index) {
        // 返回一个包含当前给定的 index 和 size 的字符串
        return "Index: "+index+", Size: "+size;
    }

    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection.
     * 移除所有指定集合中存在的元素在列表中也存在的元素
     *
     * @param c collection containing elements to be removed from this list
     * c 参数为要从列表中删除元素的集合
     * @return {@code true} if this list changed as a result of the call
     * 如果列表作为调用的结果被改变了，则返回 ture
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection
     * 抛出 ClassCastException 异常，如果此列表的元素的类与指定的集合不兼容
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements
     * 抛出 NullPointerException 异常，如果此列表包含 null 元素，并且指定的集合不允许 null 元素
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see Collection#contains(Object)
     * 参看 Collection的contains(Object) 方法
     */
    public boolean removeAll(Collection<?> c) {
        // 使用 Objects 工具类检查 c 集合是否指向 null
        Objects.requireNonNull(c);
        // 根据第二个参数来判断是要删除还是要保留
        return batchRemove(c, false);
    }

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection.  In other words, removes from this list all
     * of its elements that are not contained in the specified collection.
     * 保存列表中指定集合中的元素（相当于是做一个交集）。
     * 换句话说，就是移除列表中有的而指定集合没有的那些元素。
     * 
     * @param c collection containing elements to be retained in this list
     * c 参数为要保留在列表中的元素集合
     * @return {@code true} if this list changed as a result of the call
     * 如果列表作为调用的结果被改变了，则返回 ture
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection
     * 抛出 ClassCastException 异常，如果此列表的元素的类与指定的集合不兼容
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements
     * 抛出 NullPointerException 异常，如果此列表包含 null 元素，
     * 并且指定的集合不允许 null 元素
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see Collection#contains(Object)
     * 参看 Collection的contains(Object) 方法
     */
    public boolean retainAll(Collection<?> c) {
        // 使用 Objects 工具类检查 c 集合是否指向 null
        Objects.requireNonNull(c);
        // 根据第二个参数来判断是要删除还是要保留
        return batchRemove(c, true);
    }

    // complement 为 true，保留集合中存在的元素
    // complement 为 false，删除集合中存在的元素
    private boolean batchRemove(Collection<?> c, boolean complement) {
        final Object[] elementData = this.elementData;
        int r = 0, w = 0;
        boolean modified = false;
        try {
            for (; r < size; r++)
                // 执行保留或者删除操作
                if (c.contains(elementData[r]) == complement)
                    // 一行代码，两步操作，一步是在数组下标为 w 的位置重新赋值，另一步是 w 加 1
                    elementData[w++] = elementData[r];
        } finally {
            // Preserve behavioral compatibility with AbstractCollection,
            // even if c.contains() throws.
            // 保留与AbstractCollection的行为兼容性，即使 c.contains（）抛出也是如此
            if (r != size) {
                // 移动数组
                System.arraycopy(elementData, r,
                                 elementData, w,
                                 size - r);
                w += size - r;
            }
            if (w != size) {
                // clear to let GC do its work
                // 给无用的数组元素赋 null 值，让垃圾回收器回收没有引用的对象
                for (int i = w; i < size; i++)
                    elementData[i] = null;
                // 修改次数
                modCount += size - w;
                // size = w
                size = w;
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Save the state of the <tt>ArrayList</tt> instance to a stream (that
     * is, serialize it).
     * 保存 ArrayList 实例的状态到流中（也就是说，序列化它）
     *
     * @serialData The length of the array backing the <tt>ArrayList</tt>
     *             instance is emitted (int), followed by all of its elements
     *             (each an <tt>Object</tt>) in the proper order.
     * 序列化的数据为 ArrayList 实例中数组的长度，然后是以正确顺序排列的 Object 元素
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException{
        // Write out element count, and any hidden stuff
        // 写出元素计数和任何隐藏的东西，在写之前先保存修改次数，以防在写的过程中结构改变
        int expectedModCount = modCount;
        // 执行默认的序列化机制，写出一些隐含的信息
        s.defaultWriteObject();

        // Write out size as capacity for behavioural compatibility with clone()
        // 写出大小作为与 clone() 方法兼容性的容量
        s.writeInt(size);

        // Write out all elements in the proper order.
        // 以正确的顺序写出所有的元素，遍历 elementData 数组中前 size 的元素
        for (int i=0; i<size; i++) {
            s.writeObject(elementData[i]);
        }
        // 如果写入流后的修改次数和写入流前的修改次数不一致，抛出 ConcurrentModificationException 并发修改异常
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * Reconstitute the <tt>ArrayList</tt> instance from a stream (that is,
     * deserialize it).
     * 复原 ArrayList 实例从流中（也就是说，反序列化它）
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        elementData = EMPTY_ELEMENTDATA;

        // Read in size, and any hidden stuff
        // 读入 size 和任何隐藏的东西，执行默认的反序列化机制，读入一些隐含的信息
        s.defaultReadObject();

        // Read in capacity
        // 读入容量
        s.readInt(); // ignored

        if (size > 0) {
            // be like clone(), allocate array based upon size not capacity
            // 就像克隆一样，存储数组是基于 size 的而不是 capacity
            int capacity = calculateCapacity(elementData, size);
            // 检查数组是不是 Object 类型
            SharedSecrets.getJavaOISAccess().checkArray(s, Object[].class, capacity);
            // 确保这个实例的 elementData 容量充足，此时 capacity == size
            ensureCapacityInternal(size);

            Object[] a = elementData;
            // Read in all elements in the proper order.
            // 以正确的顺序读入所有元素
            for (int i=0; i<size; i++) {
                a[i] = s.readObject();
            }
        }
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     * The specified index indicates the first element that would be
     * returned by an initial call to {@link ListIterator#next next}.
     * An initial call to {@link ListIterator#previous previous} would
     * return the element with the specified index minus one.
     * 从列表中的指定位置开始，返回列表中元素的列表迭代器（按正确顺序）。
     * 指定的索引表示初始调用 ListIterator 的 next 方法将返回的第一个元素。
     * 对 ListIterator 的 previous 方法的初始调用将返回指定索引减去1的元素
     * <p>The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     * 返回的列表迭代器是 fail-fast 的。
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * 抛出 IndexOutOfBoundsException 下表越界异常
     */
    public ListIterator<E> listIterator(int index) {
        // 越界检查
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: "+index);
        return new ListItr(index);
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence).
     * 返回列表中元素的列表迭代器（按正确顺序）
     *
     * <p>The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     * 返回的列表迭代器是 fail-fast 的。
     * 
     * @see #listIterator(int)
     * 参看 listIterator(int) 方法
     */
    public ListIterator<E> listIterator() {
        return new ListItr(0);
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     * 返回列表中元素的列表迭代器（按正确顺序）
     * <p>The returned iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     * 返回的列表迭代器是 fail-fast 的。
     * @return an iterator over the elements in this list in proper sequence
     * 返回列表中元素的列表迭代器（按正确顺序）
     */
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * An optimized version of AbstractList.Itr
     * AbstractList.Itr的优化版本，私有内部类
     */
    private class Itr implements Iterator<E> {
        int cursor;       // index of next element to return 将要返回的下一个元素索引
        int lastRet = -1; // index of last element returned; -1 if no such 上一个返回元素的索引，如果不是这样的话就是 -1
        // 在迭代过程中，期望修改的次数应该始终等于修改次数，否则会抛出并发修改异常
        int expectedModCount = modCount;

        Itr() {}

        // 判断是否有下一个元素
        public boolean hasNext() {
            return cursor != size;
        }

        // 返回迭代的下一个元素
        @SuppressWarnings("unchecked")
        public E next() {
            // 检查修改是否不一致
            checkForComodification();
            // 当前要返回的元素索引
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            // 下一个元素的索引
            cursor = i + 1;
            // 一行代码两步操作，一部是返回当前元素，另一步是将 lastRet 置为当前元素的索引
            return (E) elementData[lastRet = i];
        }

        // 删除元素
        public void remove() {
            // remove() 方法要在 next() 方法之后使用，否则会抛出 IllegalStateException 异常
            if (lastRet < 0)
                throw new IllegalStateException();
            // 检查修改是否不一致
            checkForComodification();

            try {
                // ArrayList 本身的 remove(int) 方法
                ArrayList.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                // 更改期望的元素修改次数
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public void forEachRemaining(Consumer<? super E> consumer) {
            Objects.requireNonNull(consumer);
            final int size = ArrayList.this.size;
            int i = cursor;
            if (i >= size) {
                return;
            }
            final Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length) {
                throw new ConcurrentModificationException();
            }
            while (i != size && modCount == expectedModCount) {
                consumer.accept((E) elementData[i++]);
            }
            // update once at end of iteration to reduce heap write traffic
            // 在迭代结束时更新一次以减少堆写入容量
            cursor = i;
            lastRet = i - 1;
            checkForComodification();
        }

        // 检查并发修改异常
        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    /**
     * An optimized version of AbstractList.ListItr
     * AbstractList.ListItr的优化版本，私有内部类
     */
    private class ListItr extends Itr implements ListIterator<E> {
        // 带参构造方法
        ListItr(int index) {
            super();
            cursor = index;
        }

        // 是否有上一个元素
        public boolean hasPrevious() {
            return cursor != 0;
        }

        // 返回下一个元素的索引
        public int nextIndex() {
            return cursor;
        }

        // 返回上一个元素的索引
        public int previousIndex() {
            return cursor - 1;
        }

        @SuppressWarnings("unchecked")
        // 返回上一个元素
        public E previous() {
            // 检查修改异常
            checkForComodification();
            // 计算上一个元素的索引
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();
            Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            // 下一个元素为当前元素
            cursor = i;
            // 返回上一个元素
            return (E) elementData[lastRet = i];
        }

        // 修改元素值
        public void set(E e) {
            if (lastRet < 0)
                throw new IllegalStateException();
            // 检查修改异常
            checkForComodification();

            try {
                ArrayList.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        // 添加元素值
        public void add(E e) {
            // 检查修改异常
            checkForComodification();

            try {
                // 在当前元素的下一个位置添加新元素
                int i = cursor;
                ArrayList.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * Returns a view of the portion of this list between the specified
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.  (If
     * {@code fromIndex} and {@code toIndex} are equal, the returned list is
     * empty.)  The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations.
     * 返回指定的 fromIndex（包含该位置的值） 和 toIndex（不包含该位置的值） 之间的此列表的视图
     * （如果 fromIndex == toIndex ，则返回的列表为空）
     * 返回的列表是依赖于此列表的，因此在对返回列表的非结构化更改会反映到此列表中，反之亦然（就是说对两者任一方的更改都会影响另一方）。
     * 返回的列表支持列表所有的操作。
     * 
     * <p>This method eliminates the need for explicit range operations (of
     * the sort that commonly exist for arrays).  Any operation that expects
     * a list can be used as a range operation by passing a subList view
     * instead of a whole list.  For example, the following idiom
     * removes a range of elements from a list:
     * <pre>
     *      list.subList(from, to).clear();
     * </pre>
     * 此方法消除了对显式范围方法的需要（通常还需要对数组进行排序）
     * 任何需要对整个列表部分的操作都可以传递 subList，而不是对整个列表进行操作（提高性能）。例如，下面的语句是从列表中删除一系列元素：list.subList(from, to).clear();
     * 
     * 
     * Similar idioms may be constructed for {@link #indexOf(Object)} and
     * {@link #lastIndexOf(Object)}, and all of the algorithms in the
     * {@link Collections} class can be applied to a subList.
     * 可以为 indexOf（Object）和 lastIndexOf（Object）构造类似的语句，Collections类中的所有算法都可以应用于 subList。
     *
     * <p>The semantics of the list returned by this method become undefined if
     * the backing list (i.e., this list) is <i>structurally modified</i> in
     * any way other than via the returned list.  (Structural modifications are
     * those that change the size of this list, or otherwise perturb it in such
     * a fashion that iterations in progress may yield incorrect results.)
     * 如果依赖列表（进行操作的列表）通过返回的列表以外的方式对结构进行修改，那么通过这个方法返回的列表就被变得不确定。
     * （结构化修改是指改变了这个列表的大小，或者其他扰乱列表的方式，使得正在进行的迭代可能产生不正确的结果）
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * 抛出 IndexOutOfBoundsException 下表越界异常
     * @throws IllegalArgumentException {@inheritDoc}
     * 抛出 IllegalArgumentException 非法参数异常
     */
    public List<E> subList(int fromIndex, int toIndex) {
        // 检查返回列表的边界
        subListRangeCheck(fromIndex, toIndex, size);
        return new SubList(this, 0, fromIndex, toIndex);
    }

    // 返回列表边界检查
    static void subListRangeCheck(int fromIndex, int toIndex, int size) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > size)
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                                               ") > toIndex(" + toIndex + ")");
    }

    // SubList 继承自 AbstractList，并没有存储实际元素数据，而是存储的索引数据
    private class SubList extends AbstractList<E> implements RandomAccess {
        private final AbstractList<E> parent; // 父列表，也即是产生 subList 的列表
        private final int parentOffset; // 相对于父列表的偏移值
        private final int offset; // 相对于子列表自己的偏移值
        int size; // 子列表的大小

        // 有参构造方法
        SubList(AbstractList<E> parent,
                int offset, int fromIndex, int toIndex) {
            this.parent = parent;
            this.parentOffset = fromIndex;
            this.offset = offset + fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = ArrayList.this.modCount;
        }

        // 修改方法，返回修改前的值
        public E set(int index, E e) {
            rangeCheck(index);
            checkForComodification();
            E oldValue = ArrayList.this.elementData(offset + index);
            ArrayList.this.elementData[offset + index] = e;
            return oldValue;
        }

        // 根据索引获取元素
        public E get(int index) {
            rangeCheck(index);
            checkForComodification();
            return ArrayList.this.elementData(offset + index);
        }

        // 获取子列表的 size 大小
        public int size() {
            checkForComodification();
            return this.size;
        }

        // 添加元素，对父列表和子列表都会造成影响
        public void add(int index, E e) {
            rangeCheckForAdd(index);
            checkForComodification();
            parent.add(parentOffset + index, e);
            this.modCount = parent.modCount;
            this.size++;
        }

        // 根据索引移除元素，并返回移除的值，对父列表和子列表都会造成影响
        public E remove(int index) {
            rangeCheck(index);
            checkForComodification();
            E result = parent.remove(parentOffset + index);
            this.modCount = parent.modCount;
            this.size--;
            return result;
        }

        // 移除指定索引范围内（不包括后面的索引）的元素，实际调用的还是父列表中的 removeRange 方法
        protected void removeRange(int fromIndex, int toIndex) {
            checkForComodification();
            parent.removeRange(parentOffset + fromIndex,
                               parentOffset + toIndex);
            this.modCount = parent.modCount;
            this.size -= toIndex - fromIndex;
        }

        // 添加指定集合中的所有元素到子列表末尾，是调用 SubList 类中的 addAll 方法实现的
        public boolean addAll(Collection<? extends E> c) {
            return addAll(this.size, c);
        }

        // 从指定的子列表中的索引位置添加集合中的所有元素
        public boolean addAll(int index, Collection<? extends E> c) {
            rangeCheckForAdd(index);
            int cSize = c.size();
            if (cSize==0)
                return false;

            checkForComodification();
            parent.addAll(parentOffset + index, c);
            this.modCount = parent.modCount;
            this.size += cSize;
            return true;
        }

        // 返回子列表的迭代器
        public Iterator<E> iterator() {
            return listIterator();
        }

        // 返回子列表的列表迭代器
        public ListIterator<E> listIterator(final int index) {
            checkForComodification();
            rangeCheckForAdd(index);
            final int offset = this.offset;

            // 返回一个实现 ListIterator 的实现类
            return new ListIterator<E>() {
                int cursor = index;
                int lastRet = -1;
                int expectedModCount = ArrayList.this.modCount;

                public boolean hasNext() {
                    return cursor != SubList.this.size;
                }

                @SuppressWarnings("unchecked")
                public E next() {
                    checkForComodification();
                    int i = cursor;
                    if (i >= SubList.this.size)
                        throw new NoSuchElementException();
                    Object[] elementData = ArrayList.this.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i + 1;
                    return (E) elementData[offset + (lastRet = i)];
                }

                public boolean hasPrevious() {
                    return cursor != 0;
                }

                @SuppressWarnings("unchecked")
                public E previous() {
                    checkForComodification();
                    int i = cursor - 1;
                    if (i < 0)
                        throw new NoSuchElementException();
                    Object[] elementData = ArrayList.this.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i;
                    return (E) elementData[offset + (lastRet = i)];
                }

                @SuppressWarnings("unchecked")
                public void forEachRemaining(Consumer<? super E> consumer) {
                    Objects.requireNonNull(consumer);
                    final int size = SubList.this.size;
                    int i = cursor;
                    if (i >= size) {
                        return;
                    }
                    final Object[] elementData = ArrayList.this.elementData;
                    if (offset + i >= elementData.length) {
                        throw new ConcurrentModificationException();
                    }
                    while (i != size && modCount == expectedModCount) {
                        consumer.accept((E) elementData[offset + (i++)]);
                    }
                    // update once at end of iteration to reduce heap write traffic
                    lastRet = cursor = i;
                    checkForComodification();
                }

                public int nextIndex() {
                    return cursor;
                }

                public int previousIndex() {
                    return cursor - 1;
                }

                public void remove() {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try {
                        SubList.this.remove(lastRet);
                        cursor = lastRet;
                        lastRet = -1;
                        expectedModCount = ArrayList.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                public void set(E e) {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try {
                        ArrayList.this.set(offset + lastRet, e);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                public void add(E e) {
                    checkForComodification();

                    try {
                        int i = cursor;
                        SubList.this.add(i, e);
                        cursor = i + 1;
                        lastRet = -1;
                        expectedModCount = ArrayList.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                final void checkForComodification() {
                    if (expectedModCount != ArrayList.this.modCount)
                        throw new ConcurrentModificationException();
                }
            };
        }

        // SubList 的 subList 方法
        public List<E> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new SubList(this, offset, fromIndex, toIndex);
        }

        // 边界检查
        private void rangeCheck(int index) {
            if (index < 0 || index >= this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        // 为添加操作的编边界检查
        private void rangeCheckForAdd(int index) {
            if (index < 0 || index > this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        // 越过边界时的消息封装
        private String outOfBoundsMsg(int index) {
            return "Index: "+index+", Size: "+this.size;
        }

        // 检查并发修改异常
        private void checkForComodification() {
            if (ArrayList.this.modCount != this.modCount)
                throw new ConcurrentModificationException();
        }

        // 返回分裂迭代器
        public Spliterator<E> spliterator() {
            checkForComodification();
            return new ArrayListSpliterator<E>(ArrayList.this, offset,
                                               offset + this.size, this.modCount);
        }
    }

    @Override
    // Java8 Lambda 表达式遍历列表元素方式
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        final int expectedModCount = modCount;
        @SuppressWarnings("unchecked")
        final E[] elementData = (E[]) this.elementData;
        final int size = this.size;
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            action.accept(elementData[i]);
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    @Override
    // 过滤器删除元素
    public boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        // figure out which elements are to be removed
        // any exception thrown from the filter predicate at this stage
        // will leave the collection unmodified
        // 找出要删除哪些元素在此阶段从过滤谓词，抛出的任何异常都将使集合保持不变

        int removeCount = 0;
        final BitSet removeSet = new BitSet(size);
        final int expectedModCount = modCount;
        final int size = this.size;
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            @SuppressWarnings("unchecked")
            final E element = (E) elementData[i];
            if (filter.test(element)) {
                removeSet.set(i);
                removeCount++;
            }
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }

        // shift surviving elements left over the spaces left by removed elements
        // 向左移动后续的元素
        final boolean anyToRemove = removeCount > 0;
        if (anyToRemove) {
            final int newSize = size - removeCount;
            for (int i=0, j=0; (i < size) && (j < newSize); i++, j++) {
                i = removeSet.nextClearBit(i);
                elementData[j] = elementData[i];
            }
            for (int k=newSize; k < size; k++) {
                elementData[k] = null;  // Let gc do its work
            }
            this.size = newSize;
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            modCount++;
        }

        return anyToRemove;
    }

    @Override
    @SuppressWarnings("unchecked")
    // 根据操作符替换列表中的所有与元素值
    public void replaceAll(UnaryOperator<E> operator) {
        Objects.requireNonNull(operator);
        final int expectedModCount = modCount;
        final int size = this.size;
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            elementData[i] = operator.apply((E) elementData[i]);
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        modCount++;
    }

    @Override
    @SuppressWarnings("unchecked")
    // 根据比较器排序
    public void sort(Comparator<? super E> c) {
        final int expectedModCount = modCount;
        Arrays.sort((E[]) elementData, 0, size, c);
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        modCount++;
    }
}
```