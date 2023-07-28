import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.SortedSet;

public class SkipListSet<T extends Comparable<T>> implements SortedSet<T>{
    SkipListSetItem header = new SkipListSetItem(null);
    int listMaxHeight = 1;
    int listSize = 0;
    
    
    public SkipListSet()
    {
    }
    public SkipListSet(Collection<? extends T> c)
    {
        addAll(c);
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
        public int compareTo(Object o) {

            return this.compareTo(o);
        }
    }

    class SortedSetExampleIterator<T extends Comparable<T>> implements Iterator<T>
    {
        SkipListSetItem current;
        public SortedSetExampleIterator()
        {
            current = header;
        }
        @Override
        public boolean hasNext() {
            return (current.links.get(0).nextItem != null)
        }
        @Override
        public T next() {
            current = current.links.get(0).nextItem;
            return (T) current.value;
        }
    }

    public void reBalance()
    {
        // //visiting every item in the list and re randomizing its height
        if(listSize == 0)
            return;
        
        SkipListSetItem curItem = header.links.get(0).nextItem;
        ArrayList<SkipListSetItem> pointers = new ArrayList<>();
        for (int i=0; i<listMaxHeight; i++) {
            pointers.add(null);
        }
        
        while(curItem != null)
        {
            SkipListSetItem nextItem = curItem.links.get(0).nextItem;
            curItem.links.clear();

            int rHeight = randomHeight();
            
            for (int i = 0; i < rHeight; i++) {
                if(pointers.get(i) == null)
                {
                    if(header.links.size() <= i)
                        header.links.add(new NodeLinks(null, null));

                    header.links.get(i).nextItem = curItem;
                    curItem.links.add(new NodeLinks(null, null));
                }
                else
                {
                    curItem.links.add(new NodeLinks(null, pointers.get(i)));
                    pointers.get(i).links.get(i).nextItem = curItem;
                }
                pointers.remove(i);
                pointers.add(i, curItem);
            }

            curItem = nextItem;
        }
    }
    
    public void printList()
    {
        if(isEmpty())
        {
            System.out.println("List is empty");
            return;
        }

        System.out.print("Header = "); header.printItem();

        int elements = 0;
        SkipListSetItem curItem = header.links.get(0).nextItem;
        while(curItem != null)
        {
            curItem.printItem();
            curItem = curItem.links.get(0).nextItem;
            elements++;
        }
        System.out.println("Elements = " + elements);
        System.out.println("List size = " + listSize);
    }
    public void printListValues()
    {
        System.out.print("Header = "); header.printItem();

        int elements = 0;
        SkipListSetItem curItem = header.links.get(0).nextItem;
        while(curItem != null)
        {
            curItem = curItem.links.get(0).nextItem;
            elements++;
        }
        System.out.println("Elements = " + elements);
        System.out.println("List size = " + listSize);
        System.out.println("Temp ArrayList size = " + listSize);
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
    @SuppressWarnings("unchecked")
    public boolean contains(Object o){
        if(listSize == 0)
            return false;
        
        SkipListSetItem curItem = header;
        
        //find position of next value
        for(int i=header.links.size()-1; i>=0; i--)
        {   
            while(curItem.links.get(i).nextItem != null && curItem.links.get(i).nextItem.value.compareTo((T)o) < 0)
            {
                curItem = curItem.links.get(i).nextItem;
            }
        }

        //Move to found item
        curItem = curItem.links.get(0).nextItem;
        //check to see if this value
        if(curItem != null && curItem.value.compareTo((T)o) == 0)
            return true;
        else
            return false;
    
    }
    @Override
    public Iterator<T> iterator() {
        return new SortedSetExampleIterator<T>();
    }
    @Override
    @SuppressWarnings("unchecked")
    public Object[] toArray() {
        Object[] returnArray = new Object[listSize];
        SkipListSetItem curItem = header.links.get(0).nextItem;
        int counter = 0;
        while(curItem != null)
        {
            returnArray[counter] = curItem.value;
            curItem = curItem.links.get(0).nextItem;
            counter++;
        }
        return returnArray;
    }
    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        return (T[]) Arrays.copyOf(toArray(), listSize, a.getClass());
    }
    
    @Override
    public boolean add(T e) {
        //empty list
        if(listSize == 0)
        {
            SkipListSetItem newItem = new SkipListSetItem(e);

            //set new item links
            newItem.addLink(0, null, null);
            
            //set header links
            header.links.add(0, new NodeLinks(newItem, null));
            listSize++;
            return true;
        }

        SkipListSetItem curItem = header;
        ArrayList<SkipListSetItem> pointers = new ArrayList<>(header.links.size());
       
        //find position of next value
        for(int i=header.links.size()-1; i>=0; i--)
        {
            while(curItem.links.get(i).nextItem != null && curItem.links.get(i).nextItem.value.compareTo(e) < 0)
            {
                curItem = curItem.links.get(i).nextItem;
            }
            pointers.add(0, curItem);
        }

        //the spot of where to insert item
        curItem = curItem.links.get(0).nextItem;

        //prevent duplicates
        if(curItem == null || curItem.value.compareTo(e) != 0)
        {
            SkipListSetItem newItem = new SkipListSetItem(e);
            
            //checks for new height needed
            int height = randomHeight();
            newMaxHeight();
            
            //add new item to the list
            listSize++;

            //fill out links for new object
            for(int i = 0; i < height; i++)
            {

                //normal add
                if(i < pointers.size() && pointers.get(i) != null)
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
                    //increase header size
                    if(header.links.size()-1 <= i)
                    {
                        header.addLink(i, newItem, null);
                        newItem.addLink(i, null, null);
                    }
                    else
                    {
                        //reassign header links
                        newItem.addLink(i, header.links.get(i).nextItem, null);
                        //change headers pointer to new item
                        if(header.links.get(i).nextItem  != null)
                        {
                            header.links.get(i).nextItem.links.get(i).previousItem = newItem;
                        }
                        header.links.get(i).nextItem = newItem;
                    }
                    
                }
            }
            return true;
        }
        //item was a duplicate
        return false;
    }

    public boolean newMaxHeight()
    {
        int newMaxHeight = (int) Math.ceil(Math.log(listSize));

        if(newMaxHeight >= 1 && listMaxHeight != newMaxHeight)
        {
            listMaxHeight = newMaxHeight;
            return true;
            
        }

        return false;
    }

    static long RandomSeed = 1;
    static Random RandomGenerator = new Random(RandomSeed);
    public int randomHeight()
    {
        int level = 1;
        double possibleLevels = RandomGenerator.nextDouble();
        while(possibleLevels > .5 && level < listMaxHeight)
        {
            possibleLevels = RandomGenerator.nextDouble();
            level++;
        }
        return level;
    }

    @Override
    @SuppressWarnings("unchecked")
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
                curItem = curItem.links.get(i).nextItem;
            }
            pointers.add(0, curItem);
        }
        
        //the spot of where to delete item
        curItem = curItem.links.get(0).nextItem;

        //if item is in list
        if(curItem != null && curItem.value.compareTo((T)o) == 0)
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
            listSize--;
        }


        //shear Off Height
        int oldHeight = listMaxHeight;
        if(newMaxHeight())
        {
            for(int i=oldHeight; i>listMaxHeight; i--)
            {
                curItem = header;

                //current height is not populated
                if(curItem.links.size() < i)
                    break;

                while(curItem != null)
                {
                    SkipListSetItem nextItem = curItem.links.get(i).nextItem;
                    curItem.links.remove(i);
                    curItem = nextItem;
                }
            }
        }
        return true;
    }
    @Override
    public boolean containsAll(Collection<?> c) {
        for(Object item : c)
        {
            if(!contains(item))
                return false;
        }
        return true;
    }
    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean changed = false;
        
        for(T t : c)
        {
            if(add(t))
                changed = true;
        }

        return changed;
    }
    @Override
    public boolean equals(Object o) {
        //TODO NEED TO FINISH
        System.out.println("this = " + this);
        System.out.println("o = " + o);

        if(this == o)
        {
            System.out.println("Marker !!");
            return true;
        }
        else
        {
            System.out.println("false marker !!");
            return false;
        }
    }
    @Override
    public int hashCode()
    {
        SkipListSetItem curItem = header.links.get(0).nextItem;
        int totalHashCode = 0;

        while(curItem != null)
        {
            totalHashCode += curItem.value.hashCode();
            curItem = curItem.links.get(0).nextItem;
        }
        return totalHashCode;
    }
    @Override
    public boolean retainAll(Collection<?> c) {
        boolean change = false;
        SkipListSetItem curItem = header.links.get(0).nextItem;

        //loops through list
        while(curItem != null)
        {
            //if curItem is not in collection to keep
            if(!c.contains(curItem.value))
            {
                remove(curItem.value);
                change = true;
            }
            curItem = curItem.links.get(0).nextItem;
        }

        return change;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for(Object t : c)
        {
            if(remove(t))
                changed = true;
        }

        return changed;
    }
    @Override
    public void clear() {
        header = new SkipListSetItem(null);
        listSize = 0;
    }
    //Do not implement
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }
    //Do not implement
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        throw new UnsupportedOperationException();
    }
    //Do not implement
    @Override
    public SortedSet<T> headSet(T toElement) {
        throw new UnsupportedOperationException();
    }
    //Do not implement
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
        
        if(listSize == 0)
            return null;
        
        SkipListSetItem curItem = header;
        
        //find position of next value
        for(int i=header.links.size()-1; i>=0; i--)
        {
            while(curItem.links.get(i).nextItem != null)
            {
                curItem = curItem.links.get(i).nextItem;
            }
        }
        
        //return last value
        return curItem.value;
    }
}
    
