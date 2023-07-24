import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.SortedSet;

public class SkipListSet<T extends Comparable<T>> implements SortedSet<T>{
    ArrayList<SkipListSetItem> list = new ArrayList<>();
    SkipListSetItem header = new SkipListSetItem(null);
    int listMaxHeight = 1;
    int listSize = 0;
    
    
    ArrayList<SkipListSet<T>.SkipListSetItem> SkipListSet()
    {
        list = new ArrayList<>();
        return list;
    }

    ArrayList<SkipListSet<T>.SkipListSetItem> SkipListSet(Collection<? extends T> c)
    {
        SkipListSet();
        addAll(c);
        return list;
    }




    public class NodeLinks
    {
        SkipListSetItem nextItem=null;
        SkipListSetItem previousItem=null;

        NodeLinks(SkipListSetItem nexItem, SkipListSetItem prevItem)
        {
            this.nextItem = nexItem;
            this.previousItem = prevItem;
        }
    }
    public class SkipListSetItem
    {
        T value;
        ArrayList <NodeLinks> links = new ArrayList<>();

        public SkipListSetItem(T e) {
            this.value =e;
        }
        public SkipListSetItem() {
        }
        public void addLink(int level, SkipListSetItem nextItem, SkipListSetItem prevItem)
        {
            links.add(level, new NodeLinks(nextItem, prevItem));
        }
        public void addLink(SkipListSetItem nextItem, SkipListSetItem prevItem)
        {
            links.add(new NodeLinks(nextItem, prevItem));
        }
        public void setValue(T value) {
            this.value = value;
        }
        public void printItem()
        {
            System.out.print("Value = " + this.value + " | ");

            System.out.print("Links = " + this.links.size() + " | ");
            for(int j =0; j<this.links.size(); j++)
            {
                if(this.links.get(j).previousItem != null)
                {
                    System.out.print(" | <link[" + j + "] = " +  this.links.get(j).previousItem.value);
                }
                else if(this.links.get(j).previousItem == null)
                {
                    System.out.print(" | link[" + j + "] = null");
                }
                if(this.links.get(j).nextItem != null)
                {
                    System.out.print("  >link[" + j + "] = " +  this.links.get(j).nextItem.value);
                }
                else if(this.links.get(j).nextItem == null)
                {
                    System.out.print("  link[" + j + "] = null");
                }
            }
            System.out.println("");
        }
    }

    class SortedSetExampleIterator<T extends Comparable<T>> implements Iterator<T>
    {
        SkipListSetItem current;
        public SortedSetExampleIterator(ArrayList<SkipListSetItem> list)
        {
            current = list.get(0);
        }
        @Override
        public boolean hasNext() {
            if(current.links.get(0).nextItem != null)
                return true;
            else
                return false;
        }
        @Override
        public T next() {
            T value = (T) current.value;
            current = current.links.get(0).nextItem;
            return value;
        }
    }

    public void reBalance()
    {

    }
    
    public void printList()
    {
        System.out.print("Header = "); header.printItem();


        for(int i=0; i<listSize; i++)
        {
            SkipListSetItem curItem = list.get(i);
            curItem.printItem();
        }
    }

    @Override
    public int size() {
        return listSize;
    }
    @Override
    public boolean isEmpty() {
        if(listSize == 0)
            return true;
        else
            return false;
    }
    @Override
    public boolean contains(Object o) {
        if(listSize == 0)
            return false;
        

        
        SkipListSetItem curItem = header;
        
        //find position of next value
        for(int i=header.links.size()-1; i>=0; i--)
        {
            while(curItem.links.get(i).nextItem != null && curItem.links.get(i).nextItem.value.compareTo((T)o) < 0)
            {
                //System.out.println("Continue searching level; value = " + curItem.value + " e = " + e);
                curItem = curItem.links.get(i).nextItem;
            }
        }

        //the spot of where to insert item
        if(curItem.value == (T)o)
            return true;
        else
            return false;
    
    }
    @Override
    public Iterator<T> iterator() {
        return new SortedSetExampleIterator<T>(list);
    }
    @Override
    public Object[] toArray() {
        return list.toArray();
    }
    @Override
    public <T> T[] toArray(T[] a) {
        for(int i=0; i<listSize; i++)
        {
            a[i] = (T) list.get(i).value;
        }
        return a;
    }

    @Override
    public boolean add(T e) {
        
        //empty list
        if(listSize == 0)
        {
            SkipListSetItem newItem = new SkipListSetItem(e);

            //list.add(0, newItem);
            //set new item links
            newItem.addLink(0, null, null);
            
            //set header links
            header.links.add(0, new NodeLinks(newItem, null));
            listSize++;
            return true;
        }

        SkipListSetItem curItem = header;
        ArrayList<SkipListSetItem> pointers = new ArrayList<>(listMaxHeight);
       
        //find position of next value
        for(int i=header.links.size()-1; i>=0; i--)
        {
            while(curItem.links.get(i).nextItem != null && curItem.links.get(i).nextItem.value.compareTo(e) < 0)
            {
                //System.out.println("Continue searching level; value = " + curItem.value + " e = " + e);
                curItem = curItem.links.get(i).nextItem;
            }
            pointers.add(0, curItem); //System.out.print("pointers +=  " ); curItem.printItem();
        }

        //the spot of where to insert item
        SkipListSetItem prevItem = curItem;
        curItem = curItem.links.get(0).nextItem;

        //System.out.println("Pointers = ");
        // for (SkipListSetItem p : pointers) {
        //     p.printItem();
        // }

        //prevent duplicates
        if(curItem == null || curItem.value.compareTo(e) != 0)
        {
            SkipListSetItem newItem = new SkipListSetItem(e);
            
            //checks for new height needed
            int height = randomHeight();
            if(newMaxHeight())
            {
                height = listMaxHeight-1;
                newItem.addLink(null, null);
                header.addLink(newItem, null);
            }
            
            //add new item to the list
            //System.out.println("curItem = " + curItem + " index of curItem = " + (list.indexOf(prevItem) + 1));
            //list.add(newItem);
            listSize++;
            //fill out links for new object
            for(int i = 0; i < height; i++)
            {
                //normal add
                if(pointers.get(i) != null)
                {
                    //set new item links
                    newItem.addLink(i, pointers.get(i).links.get(i).nextItem, pointers.get(i));
    
                    //reset next item's back link to the new item 
                    if(pointers.get(i).links.get(i).nextItem != null)
                        pointers.get(i).links.get(i).nextItem.links.get(i).previousItem = newItem;
    
                    //reset previous item's next link to the new item 
                    pointers.get(i).links.get(i).nextItem = newItem;
                }
                //add to header
                else
                {
                    //reassign header links
                    newItem.addLink(i, header.links.get(i).nextItem, null);
                    header.links.get(i).nextItem = newItem;
                }
            }

            //System.out.print("new item added = ");newItem.printItem();
        }

        
        return true;
    }

    public boolean newMaxHeight()
    {
        int newMaxHeight = (int) Math.ceil(Math.log(listSize));
        
        if(newMaxHeight >= 1)
        {
            if(listMaxHeight != newMaxHeight)
            {
                //System.out.println("new max height = " + newMaxHeight);
                listMaxHeight = newMaxHeight;
                return true;
            }
        }

        return false;
    }

    static long RandomSeed = 1;
    static Random RandomGenerator = new Random(RandomSeed);
    public int randomHeight()
    {
        int level = 0;
        double possibleLevels = RandomGenerator.nextDouble();
        while(possibleLevels > .5 && level < listMaxHeight)
        {
            possibleLevels = RandomGenerator.nextDouble();
            level++;
        }
        return level;
    }

    @Override
    public boolean remove(Object o) {
        //search for the element to delete
        if(listSize == 0)
            return false;

        SkipListSetItem curItem = header;
        ArrayList<SkipListSetItem> pointers = new ArrayList<>(listMaxHeight);
        
        //find position of next value
        for(int i=header.links.size()-1; i>=0; i--)
        {
            while(curItem.links.get(i).nextItem != null && curItem.links.get(i).nextItem.value.compareTo((T)o) < 0)
            {
                //System.out.println("Continue searching level; value = " + curItem.value + " e = " + e);
                curItem = curItem.links.get(i).nextItem;
            }
            pointers.add(0, curItem); //System.out.print("pointers +=  " ); curItem.printItem();
        }
        
        //the spot of where to delete item
        curItem = curItem.links.get(0).nextItem;

        if(curItem != null && curItem.value == (T)o)
        {
            for(int i=0; i<curItem.links.size(); i++)
            {
                //reassign back link
                if(curItem.links.get(i).previousItem != null)
                {
                    curItem.links.get(i).previousItem.links.get(i).nextItem = curItem.links.get(i).nextItem;
                }
                //points to header
                else
                {
                    header.links.get(i).nextItem = curItem.links.get(i).nextItem;
                }

                //reassign forward pointer
                if(curItem.links.get(i).nextItem != null)
                {
                    curItem.links.get(i).nextItem.links.get(i).previousItem = curItem.links.get(i).previousItem;
                }
            }
        }

        listSize--;
        return true;
    }
    @Override
    public boolean containsAll(Collection<?> c) {
    // TODO Auto-generated method stub
    return false;
    }
    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean changed = false;
        
        for(T t : c)
        {
            //System.out.println("");
            // System.out.println("------");
            // //System.out.println("");
            // System.out.println("Adding : " + t);
            if(add(t))
                changed = true;
            //printList();
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean change = false;
        for (SkipListSetItem item : list) {
            if(!c.contains(item))
            {
                remove(item.value);
                change = true;
            }
        }
        return change;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for(Object t : c)
        {
            // System.out.println("");
            // System.out.println("------");
            // System.out.println("");
            // System.out.println("removing : " + t);
            if(remove(t))
                changed = true;
            //printList();
        }

        return changed;
    }
    @Override
    public void clear() {
        list = new ArrayList<>();
    }
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        throw new UnsupportedOperationException();
    }
    @Override
    public SortedSet<T> headSet(T toElement) {
        throw new UnsupportedOperationException();
    }
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        throw new UnsupportedOperationException();
    }
    @Override
    public T first() {
        return header.links.get(0).nextItem.value;
    }
    @Override
    public T last() {
        //TODO method needs implementing
        return null;
    }
}
    
