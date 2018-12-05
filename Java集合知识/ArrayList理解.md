# 对 java.util.ArrayList 的理解

- **问题：ArrayList 的 size 和 capacity 怎么理解？**

如果把 ArrayList 看作一个杯子的话，capacity 就是杯子的容积，也就是代表杯子能装多少东西，而 size 就是杯子装的东西的体积。杯子可能装满了，也可能没装满，所以 capacity >= size 。capacity 过大和过小都不好，过大会造成浪费，过小又存放不下多个元素的值，capacity == size，则 ArrayList 空间利用率最大，但是不利于添加新的元素。当 ArrayList 实例内的元素个数不再改变了，可以使用 trimToSize() 方法最小化 ArrayList 实例来节省空间，也即是使 capacity == size。


- **问题：ArrayList 内部是怎么存放数据的？**

ArrayList 可以看做是数组的封装，使用 elementData 数组来存储数据，使用 size 来代表 elementData 的非 null 元素个数。elementData 前没有访问修饰符，所以只有同类和同包下的类可以直接方法，外界想要知道 ArrayList 实例内元素的个数就要通过 size 属性。elementData 数组类型是 Object 类型，可以存放任意的引用类型，不能存放基本的数据类型。


- **问题：ArrayList 类常量 EMPTY_ELEMENTDATA 和 DEFAULTCAPACITY_EMPTY_ELEMENTDATA 怎么理解？是不是多余？**

这两个类常量都是空 Object 数组的引用，都代表 ArrayList 实例的空状态，也即是 elementData 数组中还没有元素。但是 EMPTY_ELEMENTDATA 是使用带初始化值的构造方法（有参构造函数，一个是指定初始容量，一个是指定初始集合）时使用的，DEFAULTCAPACITY_EMPTY_ELEMENTDATA 是使用默认的构造方法，也即是无参的构造方法时使用的。


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

List 集合可以看作是数组的包装类型，遍历并不像数组那样方便，迭代器是为了迭代集合中的元素而存在的。Itr 迭代器类实现了 Iterator 接口，ListItr 迭代器类继承 Itr 迭代器类，并且实现了 ListIterator 接口，所以 ListItr 类的功能比 Itr 类更强大。Itr 类在迭代过程中不能修改 List 的结构（如 add 操作），否则会抛出并发修改异常 ConcurrentModificationException，并且在 next 方法之后才能 remove 元素，而 ListItr 类还支持在迭代过程中添加元素，对于 List 集合元素操作更加友好。所以对于 List 集合迭代，最好使用 ListItr 迭代器类。


