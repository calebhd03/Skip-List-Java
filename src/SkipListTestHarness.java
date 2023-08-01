import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
public class SkipListTestHarness {
private static class CPUTimer {
public static <T> long timeFor(Callable<T> task) {
    try {
        long start = System.currentTimeMillis();
        T t = task.call();
        long end = System.currentTimeMillis();
        return end - start;
    } catch (Exception e) {
        System.out.println(e.toString());
        e.printStackTrace();
    }
    return 0;
    }
}

static long RandomSeed = 1;
static Random RandomGenerator = new Random(RandomSeed);
static byte[] buf = new byte[1024];

private static ArrayList<Integer> generateIntArrayList(int howMany) {
    ArrayList<Integer> list = new ArrayList<Integer>(howMany);
    for(int i = 0; i < howMany; i++) {
        list.add(Integer.valueOf(RandomGenerator.nextInt()));
    }

    return list;
}

private static ArrayList<Double> generateDoubleArrayList(int howMany) {
    ArrayList<Double> list = new ArrayList<Double>(howMany);
    for(int i = 0; i < howMany; i++) {
        list.add(Double.valueOf(RandomGenerator.nextDouble()));
    }

    return list;
}

private static String generateRandomString(int len) {
    if(len > 1024) len = 1024;
    buf[len - 1] = (byte) 0;
    for(int j = 0; j < (len - 1); j++) {
        buf[j] = (byte) (RandomGenerator.nextInt(94) + 32);
    }
    return new String(buf);
}

private static ArrayList<String> generateStringArrayList(int howMany, int len) {
    ArrayList<String> list = new ArrayList<String>(howMany);
    for(int i = 0; i < howMany; i++) {
        list.add(generateRandomString(len));
    }
    return list;
}

private static <T> ArrayList<T> generateStrikeList(ArrayList<? extends T> fromList, int howMany) {
    ArrayList<T> strikeList = new ArrayList<T>(howMany);
    int fromLast = fromList.size() - 1;
    for(int i = 0; i < howMany; i++) {
        strikeList.add(fromList.get(RandomGenerator.nextInt(fromLast)));
    }
    return strikeList;
}

private static <T> ArrayList<T> generateRemoveList(ArrayList<? extends T> fromList) {
    ArrayList<T> removeList = new ArrayList<T>(fromList.size()/2);
    for(int i = 0; i < fromList.size() / 2; i++) {
        removeList.add(fromList.get(i));
    }
    return removeList;
}

private static <T> int executeFinds(Collection<? extends T> coll, ArrayList<?extends T> strikes) {
    boolean sentinel;
    int failures = 0;
    for (T e: strikes) {
        sentinel = coll.contains(e);
        if(sentinel == false) {
            failures++;
        }
    }
    if(failures >= 0) {
        System.out.printf("(%,d missing) ", failures);
    }
    return 0;
}

private static <T extends Comparable<T>> void executeCase(ArrayList<? extends T> values, ArrayList<? extends T> strikes, boolean includeLinkedList, boolean includeRemoves) {
    ArrayList<T> removeList = generateRemoveList(strikes);
    long start;
    long end;
    long ms;
    if(includeLinkedList) {
        LinkedList<T> linkedList = new LinkedList<T>();
        System.out.printf(" LinkedList ");
        ms = CPUTimer.timeFor(() -> linkedList.addAll(values));
        System.out.printf("add: %,6dms ", ms);
        ms = CPUTimer.timeFor(() -> executeFinds(linkedList, strikes));
        System.out.printf("find: %,6dms ", ms);

        if(includeRemoves) {
            ms = CPUTimer.timeFor(() ->
            linkedList.removeAll(removeList));
            System.out.printf("del: %,6dms ", ms);
            ms = CPUTimer.timeFor(() -> executeFinds(linkedList, strikes));
            System.out.printf("find: %,6dms ", ms);
        }
        System.out.printf("\n");
    }

    System.gc();

    if(true) {
        SkipListSet<T> skipListSet = new SkipListSet<T>();
        System.out.printf(" SkipListSet ");
        ms = CPUTimer.timeFor(() -> skipListSet.addAll(values));
        System.out.printf("add: %,6dms ", ms);
        //System.out.print("!!!!! skiplist size = " + skipListSet.size());
        ms = CPUTimer.timeFor(() -> executeFinds(skipListSet, strikes));
        System.out.printf("find: %,6dms ", ms);

        if(includeRemoves) {
            ms = CPUTimer.timeFor(() ->
            skipListSet.removeAll(removeList));
            System.out.printf("del: %,6dms ", ms);
            ms = CPUTimer.timeFor(() -> executeFinds(skipListSet, strikes));
            System.out.printf("find: %,6dms ", ms);
        }

        System.out.printf("\n");
        System.out.printf("");
        start = System.currentTimeMillis();
        skipListSet.reBalance();
        end = System.currentTimeMillis();
        ms = end - start;
        System.out.printf("bal: %,6dms ", ms);
        ms = CPUTimer.timeFor(() -> executeFinds(skipListSet, strikes));
        System.out.printf("find: %,6dms ", ms);
        System.out.printf("\n");
        //skipListSet.printListValues();
    }

    System.gc();

    if(true) {
        TreeSet<T> treeSet = new TreeSet<T>();
        System.out.printf(" TreeSet ");
        ms = CPUTimer.timeFor(() -> treeSet.addAll(values));
        System.out.printf("add: %,6dms ", ms);
        ms = CPUTimer.timeFor(() -> executeFinds(treeSet, strikes));
        System.out.printf("find: %,6dms ", ms);

        if(includeRemoves) {
            ms = CPUTimer.timeFor(() -> treeSet.removeAll(removeList));
            System.out.printf("del: %,6dms ", ms);
            ms = CPUTimer.timeFor(() -> executeFinds(treeSet,
            strikes));
            System.out.printf("find: %,6dms ", ms);
        }

        System.out.printf("\n");
    }

    System.gc();
    System.out.printf("\n");
}

public static void executeStringCase(int listSize, int strikeSize, int stringSize, boolean includeLinkedList, boolean includeRemoves) {
    System.out.printf("CASE: %,d strings of length %,d, %,d finds, %,dremovals. Generating...\n", listSize, stringSize, strikeSize, (strikeSize/2));
    ArrayList<String> strings = generateStringArrayList(listSize,
    stringSize);
    ArrayList<String> strikes = generateStrikeList(strings, strikeSize);
    executeCase(strings, strikes, includeLinkedList, includeRemoves);
}

public static void executeIntCase(int listSize, int strikeSize, boolean includeLinkedList, boolean includeRemoves) {
    System.out.printf("CASE: %,d integers, %,d finds, %,d removals.Generating...\n", listSize, strikeSize, strikeSize/2);
    ArrayList<Integer> intlist = generateIntArrayList(listSize);
    ArrayList<Integer> strikes = generateStrikeList(intlist, strikeSize);
    executeCase(intlist, strikes, includeLinkedList, includeRemoves);
}

public static void executeDoubleCase(int listSize, int strikeSize, boolean includeLinkedList, boolean includeRemoves) {
    System.out.printf("CASE: %,d doubles, %,d finds, %,d removals.Generating...\n", listSize, strikeSize, strikeSize/2);
    ArrayList<Double> doubles = generateDoubleArrayList(listSize);
    ArrayList<Double> strikes = generateStrikeList(doubles, strikeSize);
    executeCase(doubles, strikes, includeLinkedList, includeRemoves);
}
private static <T extends Comparable<T>> void testCase() {
      SkipListSet<T> skipListSet = new SkipListSet<>();
      SkipListTestHarness.executeIntCase(1000, 0, true, false);
      
      //skipListSet.printList();
}

public SkipListTestHarness() {}
public static void main(String args[]) {
    //SortedSet<String> list = new SortedSet<>();
    
    int values = 10;
    ArrayList<Integer> blankCollection = new ArrayList<Integer>(10);
    for(int i=0; i< values; i++)
    {
        blankCollection.add(i);
    }
    
    System.out.println("\n()");
    SkipListSet<Integer> blankSkipListSet = new SkipListSet<>();

    System.out.println("\n(COLLECTION)");
    SkipListSet<Integer> skipListSet = new SkipListSet<>(blankCollection);

    System.out.println("\nclear");
    skipListSet.clear();

    System.out.println("\nadd");
    skipListSet.add(47);

    System.out.println("\naddAll");
    skipListSet.addAll(blankCollection);

    System.out.println("\ncontains(47) " + skipListSet.contains(47));
    System.out.println("contains(-30) " + skipListSet.contains(-30));

    System.out.println("\ncontainsAll");
    ArrayList<Integer> blank = new ArrayList<Integer>();
    System.out.println("containsAll(blank collection) " + skipListSet.containsAll(blank));
    ArrayList<Integer> containsAllCollection = new ArrayList<>();
    for(int i=0; i< 8; i++)
    {
        containsAllCollection.add(i);
    }
    System.out.println("containsAll(partialCOllection) " + skipListSet.containsAll(containsAllCollection));
    containsAllCollection.add(-10);
    System.out.println("containsAll(allButOne) " + skipListSet.containsAll(containsAllCollection));

    //EQUALS NOT FINISHED
    System.out.println("\nequals");
    SkipListSet<Integer> equalsSkipList = new SkipListSet<>(blankCollection);
    skipListSet.clear();
    skipListSet.addAll(blankCollection);
    System.out.println("equals = " + skipListSet.equals(equalsSkipList));

    System.out.println("\nHash code " + skipListSet.hashCode());

    System.out.println("\nIs empty " + skipListSet.isEmpty());

    Iterator<Integer> i = skipListSet.iterator();
    System.out.println("\nIterator = " + i);

    System.out.println("\nIs empty " + skipListSet.isEmpty());

    System.out.println("\nlast = " + skipListSet.last());
    System.out.println("first = " + skipListSet.first());

    System.out.println("\nRemove");
    ArrayList<Integer> removeArrayList = new ArrayList<>();
    removeArrayList.add(9);
    removeArrayList.add(87);
    skipListSet.remove(0);
    skipListSet.removeAll(removeArrayList);

    System.out.println("\nRetainAll");
    ArrayList<Integer> retainArrayList = new ArrayList<>();
    retainArrayList.add(6);
    retainArrayList.add(7);
    retainArrayList.add(9);
    retainArrayList.add(8);
    skipListSet.retainAll(retainArrayList);


    System.out.println("\nSize = " + skipListSet.size());

    System.out.println("\nTo array = ");

    Object[] toArray = skipListSet.toArray();
    for (Object object : toArray) {
        System.out.println(object);
    }

    ArrayList<Integer> argArray = new ArrayList<>();

    System.out.println("\nitera = ");
    for(Object item : skipListSet)
    {
        System.out.println(item);
    }

    System.out.println("\nrebalance = ");
    for(int z=0; z< 100; z++)
    {
        skipListSet.add(z);
    }
    skipListSet.reBalance();

    

    

    for(int h=0; h< values; h++)
    {
        if(skipListSet.contains(h) == false)
        {
            System.out.println(h + " is missing from the list");
        }
    }

    SkipListSet<Integer> sls = new SkipListSet<>();
    sls.add(10);
    sls.add(9);
    sls.add(7);

    

    System.out.println((sls.iterator().next() == 0));

    Integer[] paraArray = new Integer[2];
    
    paraArray = sls.toArray(paraArray);

    for (Integer t : paraArray) {
        System.out.println("ParrArray[] " + t);
    }
    

    SkipListSet<Integer> sls1 = new SkipListSet<>();
    SkipListSet<Integer> sls2 = new SkipListSet<>();

    sls1.add(4);
    sls1.add(1);
    sls1.add(2);
    sls1.add(3);
    sls2.add(1);
    sls2.add(2);
    sls2.add(3);
    sls2.add(4);

    Integer num1 = 10;
    Integer num2 = 10;
    System.out.println("Equals = " + (sls1.equals(sls2)));
    System.out.println("Equals = " + (num1.equals(num2)));

    Iterator it = sls1.iterator();

    System.out.println(sls1);
    
    it.remove();

    System.out.println(sls1);


    // SkipListTestHarness.executeStringCase(100000, 10000, 1000, false, true);
    // System.gc();
    // SkipListTestHarness.executeStringCase(1000000, 10000, 1000, false, true);
    // System.gc();
    // SkipListTestHarness.executeStringCase(1000000, 100000, 1000, false, true);
    // System.gc();
    // SkipListTestHarness.executeDoubleCase(100000, 10000, false, true);
    // System.gc();
    // SkipListTestHarness.executeDoubleCase(1000000, 10000, false, true);
    // System.gc();
    // SkipListTestHarness.executeDoubleCase(1000000, 100000, false, true);
    // System.gc();
    // SkipListTestHarness.executeIntCase(100000, 10000, false, true);
    // System.gc();
    // SkipListTestHarness.executeIntCase(1000000, 10000, false, true);
    // System.gc();
    // SkipListTestHarness.executeIntCase(10000000, 10000, false, true);
    // System.gc();
}
}