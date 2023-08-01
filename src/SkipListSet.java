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
    
    
    public SkipListSet(){}
    //Constructor that adds the given collection
    public SkipListSet(Collection<? extends T> c)
    {
        addAll(c);
    }

    //Object that holds the links between nodes
    public class NodeLinks
    {
        SkipListSetItem nextItem=null;
        SkipListSetItem previousItem=null;

        //Constructor that assigns pointers nextItem and previousIten
        NodeLinks(SkipListSetItem nexItem, SkipListSetItem prevItem)
        {
            this.nextItem = nexItem;
            this.previousItem = prevItem;
        }
    }
    //Object item wrapper 
    public class SkipListSetItem
    {
        T value = null;
        ArrayList <NodeLinks> links = new ArrayList<>();

        //Constructor that sets the value
        public SkipListSetItem(T e) {
            this.value =e;
        }
        //Add a link at the given level with pointers to next item and previous item
        public void addLink(int level, SkipListSetItem nextItem, SkipListSetItem prevItem)
        {
            links.add(level, new NodeLinks(nextItem, prevItem));
        }
        //Add a link at the next level with pointers to next item and previous item
        public void addLink(SkipListSetItem nextItem, SkipListSetItem prevItem)
        {
            links.add(new NodeLinks(nextItem, prevItem));
        }
        //Compare this item to o
        public int compareTo(Object o) {

            return this.compareTo(o);
        }
        //Return the link at the given level
        public NodeLinks links(int i)
        {
            return this.links.get(i);
        }
        //Return the next item at immediate item
        public SkipListSetItem neighbor()
        {
            return this.links(0).nextItem;
        }
        //Return the next item at the given level
        public SkipListSetItem getLinkItem(int i)
        {
            return this.links(i).nextItem;
        }
        //Return the previous item at the given level
        public SkipListSetItem getLinkPreviousItem(int i)
        {
            return this.links(i).previousItem;
        }
    }

    class SortedSetExampleIterator<T extends Comparable<T>> implements Iterator<T>
    {
        SkipListSetItem current;
        //Constructor that sets the current node to the header
        public SortedSetExampleIterator()
        {
            current = header;
        }
        @Override
        //Returns if their is another node after the current node
        public boolean hasNext() 
        {
            return (current.neighbor() != null);
        }
        @Override
        @SuppressWarnings("unchecked")
        //Returns the value of the next node
        public T next() {
            if(isEmpty()) return null;

            current = current.neighbor();
            return (T) current.value;
        }
    }

    //Rebalances the list
    public void reBalance()
    {
        if(isEmpty()) return;
        
        //Holds a list of back pointers 
        SkipListSetItem curItem = header.neighbor();
        ArrayList<SkipListSetItem> pointers = new ArrayList<>();
        for (int i=0; i<listMaxHeight; i++) {
            pointers.add(null);
        }
        
        //visiting every item in the list and re randomizing its height
        while(curItem != null)
        {
            SkipListSetItem nextItem = curItem.neighbor();
            curItem.links.clear();

            //New height of node
            int rHeight = randomHeight();
            
            //Reassign pointers for the current node
            for (int i = 0; i < rHeight; i++) {
                if(pointers.get(i) == null)
                {
                    if(header.links.size() <= i)
                        header.links.add(new NodeLinks(null, null));

                    header.links(i).nextItem = curItem;
                    curItem.links.add(new NodeLinks(null, null));
                }
                else
                {
                    curItem.links.add(new NodeLinks(null, pointers.get(i)));
                    pointers.get(i).links(i).nextItem = curItem;
                }
                pointers.remove(i);
                pointers.add(i, curItem);
            }

            //Move to next node in the list
            curItem = nextItem;
        }
    }

    @Override
    //Return the size of the list
    public int size() {
        return listSize;
    }
    @Override
    //Return if the list is empty
    public boolean isEmpty() {
        return (listSize == 0);
    }
    @Override
    @SuppressWarnings("unchecked")
    //Return boolean based on if the given object o is in the list
    public boolean contains(Object o){
        if(isEmpty()) return false;
        
        SkipListSetItem curItem = header;
        
        //find position of next value
        for(int i=header.links.size()-1; i>=0; i--)
        {   
            while(curItem.getLinkItem(i) != null && curItem.getLinkItem(i).value.compareTo((T)o) < 0)
            {
                curItem = curItem.getLinkItem(i);
            }
        }

        //Move to found item
        curItem = curItem.neighbor();

        //check to see if this value
        return (curItem != null && curItem.value.compareTo((T)o) == 0);
    }
    @Override
    //Returns a new iterator
    public Iterator<T> iterator() {
        return new SortedSetExampleIterator<>();
    }
    @Override
    //Returns a Object[] of this list
    public Object[] toArray() {
        Object[] returnArray = new Object[listSize];

        if(isEmpty()) return returnArray;

        SkipListSetItem curItem = header.neighbor();
        int counter = 0;

        //Add items of the list to the return array
        while(curItem != null)
        {
            returnArray[counter] = curItem.value;
            curItem = curItem.neighbor();
            counter++;
        }
        return returnArray;
    }
    @Override
    @SuppressWarnings("unchecked")
    //Returns a new T[] of the current list
    public <T> T[] toArray(T[] a) {
        return (T[]) Arrays.copyOf(toArray(), listSize, a.getClass());
    }
    
    @Override
    //Adds the given value e to the list
    public boolean add(T e) {
        //empty list
        if(isEmpty())
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
            while(curItem.getLinkItem(i) != null && curItem.getLinkItem(i).value.compareTo(e) < 0)
            {
                curItem = curItem.getLinkItem(i);
            }
            pointers.add(0, curItem);
        }

        //the spot of where to insert item
        curItem = curItem.neighbor();

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
                    newItem.addLink(i, pointers.get(i).getLinkItem(i), pointers.get(i));
    
                    //reset next item's back link to the new item 
                    if(pointers.get(i).getLinkItem(i)!= null)
                        pointers.get(i).getLinkItem(i).links(i).previousItem = newItem;
    
                    //reset previous item's next link to the new item 
                    pointers.get(i).links(i).nextItem = newItem;
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
                        newItem.addLink(i, header.getLinkItem(i), null);
                        //change headers pointer to new item
                        if(header.getLinkItem(i)  != null)
                        {
                            header.getLinkItem(i).links(i).previousItem = newItem;
                        }
                        header.links(i).nextItem = newItem;
                    }
                    
                }
            }
            return true;
        }
        //item was a duplicate
        return false;
    }

    //Helper function that returns a new max height for the list
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

    //Helper function that returns a random height for a new node
    //50% chance of adding an addition layer to each node
    static long randomSeed = 1;
    static Random randomGenerator = new Random(randomSeed);
    public int randomHeight()
    {
        int level = 1;
        double possibleLevels = randomGenerator.nextDouble();
        while(possibleLevels > .5 && level < listMaxHeight)
        {
            possibleLevels = randomGenerator.nextDouble();
            level++;
        }
        return level;
    }

    @Override
    @SuppressWarnings("unchecked")
    //Removes the given value o from the list
    public boolean remove(Object o) {
        if(isEmpty()) return false;
        
        SkipListSetItem curItem = header;
        ArrayList<SkipListSetItem> pointers = new ArrayList<>(listMaxHeight);
        
        //search for the element to delete
        for(int i=header.links.size()-1; i>=0; i--)
        {
            while(curItem.getLinkItem(i) != null && curItem.getLinkItem(i).value.compareTo((T)o) < 0)
            {
                curItem = curItem.getLinkItem(i);
            }
            pointers.add(0, curItem);
        }
        
        //the spot of where to delete item
        curItem = curItem.neighbor();
        
        //if item is in list
        if(curItem != null && curItem.value.compareTo((T)o) == 0)
        {
            for(int i=0; i<curItem.links.size(); i++)
            {
                //reassign back link
                if(curItem.getLinkPreviousItem(i) != null)
                {
                    curItem.getLinkPreviousItem(i).links(i).nextItem = curItem.getLinkItem(i);
                }
                //points to header
                else
                {
                    header.links(i).nextItem = curItem.getLinkItem(i);
                }

                //reassign forward pointer
                if(curItem.getLinkItem(i) != null)
                {
                    curItem.getLinkItem(i).links(i).previousItem = curItem.getLinkPreviousItem(i);
                }
            }
            listSize--;
        }


        //shear off height
        int oldHeight = listMaxHeight;
        if(newMaxHeight())
        {
            for(int i=oldHeight; i>listMaxHeight; i--)
            {
                curItem = header;

                //current height is not populated
                if(curItem.links.size() < i)
                    break;

                //loop through the list and remove the top link
                while(curItem != null)
                {
                    SkipListSetItem nextItem = curItem.getLinkItem(i);
                    curItem.links.remove(i);
                    curItem = nextItem;
                }
            }
        }
        return true;
    }
    @Override
    //Returns true based on if all elements of the given collection is in the list
    public boolean containsAll(Collection<?> c) {
        for(Object item : c)
        {
            if(!contains(item))
                return false;
        }
        return true;
    }
    @Override
    //Adds all elements of the given collection to the list
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
    @SuppressWarnings("unchecked")
    //Returns true if this list is equal to the given object
    public boolean equals(Object o) {
        //direct refrence
        if(this == o) return true;
        
        //type check
        if (!(o instanceof SkipListSet))  return false;
        
        SkipListSet<T> oList = (SkipListSet<T>) o;

        //size is not correct
        if(oList.size() != this.size())  return false;
        
        //all objects are equal
        return this.containsAll(oList);

    }
    @Override
    //Returns the hash code of this list
    public int hashCode() {
        SkipListSetItem curItem = header.neighbor();
        int totalHashCode = 0;

        //loop through this list and add the hash code of each element
        while(curItem != null)
        {
            totalHashCode += curItem.value.hashCode();
            curItem = curItem.neighbor();
        }
        return totalHashCode;
    }
    @Override
    //Remove all elements from this list except the elements in the given collection
    public boolean retainAll(Collection<?> c) {
        boolean change = false;
        SkipListSetItem curItem = header.neighbor();

        //loops through list
        while(curItem != null)
        {
            //if curItem is not in collection to keep
            if(!c.contains(curItem.value))
            {
                remove(curItem.value);
                change = true;
            }
            curItem = curItem.neighbor();
        }

        return change;
    }

    @Override
    //Remove all elements from the given collection from this list
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
    //Clear this list
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
    //Returns the first value of this list
    public T first() {
        return header.neighbor().value;
    }
    @Override
    //Returns the last value of this list
    public T last() {
        if(isEmpty()) return null;
        
        SkipListSetItem curItem = header;
        
        //find position of next value
        for(int i=header.links.size()-1; i>=0; i--)
        {
            while(curItem.getLinkItem(i) != null)
            {
                curItem = curItem.getLinkItem(i);
            }
        }
        
        //return last value
        return curItem.value;
    }
}