
import java.util.*;

public class SkipListSet<T extends Comparable<T>> implements SortedSet<T>
{
    int MAX_HEIGHT = 8;
    private SkipListNode<T> head;
    int size;
    
    private class SkipListNode<E extends Comparable<E>>
    {
        private E value;
        private SkipListNode<E>[] next;

        public SkipListNode(E value, int height)
        {
            this.value = value;
            next = (SkipListSet<T>.SkipListNode<E>[]) new SkipListNode[height];
        }
    }

    private class SkipListSetIterator implements Iterator<T>
    {
        private SkipListNode<T> currentNode;

        public SkipListSetIterator()
        {
            currentNode = head.next[0];
        }

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public T next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }
            
            T value = currentNode.value;
            currentNode = currentNode.next[0];
            return value;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    public SkipListSet()
    {
        head = new SkipListNode<>(null, MAX_HEIGHT);
        size = 0;
    }

    public SkipListSet(Collection<? extends T> c)
    {
        this();
        addAll(c);
    }

    @Override
    public Comparator<? super T> comparator()
    {
        return null;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedSet<T> headSet(T toElement)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedSet<T> tailSet(T fromElement)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public T first()
    {
        if (size == 0)
        {
            throw new NoSuchElementException();
        }
        
        return head.next[0].value;
    }

    @Override
    public T last()
    {
        if (size == 0)
        {
            throw new NoSuchElementException();
        }
        
        SkipListNode<T> node = head;
        
        for (int i = MAX_HEIGHT - 1; i >= 0; i--)
        {
            while (node.next[i] != null)
            {
                node = node.next[i];
            }
        }
        
        return node.value;
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean contains(Object o)
    {
        if (o == null || !isValidType(o))
        {
            return false;
        }
        
        T element = (T) o;
        SkipListNode<T> node = head;
        for (int i = MAX_HEIGHT - 1; i >= 0; i--) {
            while (node.next[i] != null && node.next[i].value.compareTo(element) < 0)
            {
                node = node.next[i];
            }
        }
        
        node = node.next[0];
        return node != null && node.value.equals(element);
    }

    @Override
    public Iterator<T> iterator()
    {
        return new SkipListSetIterator();
    }

    @Override
    public Object[] toArray()
    {
        Object[] array = new Object[size];
        int index = 0;
        SkipListNode<T> node = head.next[0];
        
        while (node != null)
        {
            array[index++] = node.value;
            node = node.next[0];
        }
        
        return array;
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T1> T1[] toArray(T1[] a)
    {
        if (a.length < size)
        {
            a = Arrays.copyOf(a, size);
        }
        
        int index = 0;
        SkipListNode<T> node = head.next[0];
        
        while (node != null)
        {
            a[index++] = (T1) node.value;
            node = node.next[0];
        }
        
        if (a.length > size)
        {
            a[size] = null;
        }
        
        return a;
    }

    @Override
    public boolean add(T t)
    {
        if (t == null || !isValidType(t))
        {
            throw new NullPointerException();
        }
        
        SkipListNode<T>[] update = (SkipListSet<T>.SkipListNode<T>[]) new SkipListNode[MAX_HEIGHT];
        SkipListNode<T> node = head;
        
        for (int i = MAX_HEIGHT - 1; i >= 0; i--)
        {
            while (node.next[i] != null && node.next[i].value.compareTo(t) < 0)
            {
                node = node.next[i];
            }
            
            update[i] = node;
        }
        
        node = node.next[0];
        
        if (node != null && node.value.equals(t))
        {
            return false; // Element already exists
        }
        
        int height = randomHeight();
        
        if (height > MAX_HEIGHT)
        {
            height = MAX_HEIGHT;
        }
        
        
        
        SkipListNode<T> newNode = new SkipListNode<>(t, height);
       
        for (int i = 0; i < height; i++)
        {
            newNode.next[i] = update[i].next[i];
            update[i].next[i] = newNode;
        }
        
        size++;
        
        MAX_HEIGHT = (int) Math.ceil(Math.log(size + 1) / Math.log(2)); // Update MAX_HEIGHT
        
        SkipListNode<T>[] newHeadNext = Arrays.copyOf(head.next, MAX_HEIGHT);
        head.next = newHeadNext;
        
        //System.out.println("\n\n" + "There are " + size + " elements in the skiplist, and the current height is " + MAX_HEIGHT + "\n\n");
        
        return true;
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean remove(Object o)
    {
        if (o == null || !isValidType(o))
        {
            return false;
        }
        
        T element = (T) o;
        SkipListNode<T>[] update = new SkipListNode[MAX_HEIGHT];
        SkipListNode<T> node = head;
        
        for (int i = MAX_HEIGHT - 1; i >= 0; i--)
        {
            while (node.next[i] != null && node.next[i].value.compareTo(element) < 0)
            {
                node = node.next[i];
            }
            
            update[i] = node;
        }
        
        node = node.next[0];
        if (node != null && node.value.equals(element))
        {
            for (int i = 0; i < MAX_HEIGHT; i++)
            {
                if (update[i].next[i] != node)
                {
                    break;
                }
                
                update[i].next[i] = node.next[i];
            }
            
            size--;
            return true;
        }
        
        return false; // Element not found
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        for (Object element : c)
        {
            if (!contains(element))
            {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c)
    {
        boolean modified = false;
        for (T element : c)
        {
            if (add(element))
            {
                modified = true;
            }
        }
        
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        boolean modified = false;
        Iterator<T> iterator = iterator();
        
        while (iterator.hasNext())
        {
            if (!c.contains(iterator.next()))
            {
                iterator.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        boolean modified = false;
        for (Object element : c)
        {
            if (remove(element))
            {
                modified = true;
            }
        }
        
        return modified;
    }

    @Override
    public void clear()
    {
        head = new SkipListNode<>(null, MAX_HEIGHT);
        size = 0;
    }

    private int randomHeight()
    {
        int height = 1;
        
        while (height < MAX_HEIGHT && Math.random() < 0.5)
        {
            height++;
        }
        
        return height;
    }

    private boolean isValidType(Object o)
    {
        return o instanceof Comparable;
    }

	public void reBalance()
	{
		SkipListSet<T> newSkipList = new SkipListSet<>();

	    for (T element : this)
	    {
	        newSkipList.add(element);
	    }

	    this.head = newSkipList.head;
	    this.size = newSkipList.size;
	}
	
	@Override
	public boolean equals(Object obj)
	{
	    if (this == obj)
	    {
	        return true;
	    }

	    if (obj == null || !(obj instanceof SkipListSet))
	    {
	        return false;
	    }

	    SkipListSet<?> other = (SkipListSet<?>) obj;

	    if (size() != other.size())
	    {
	        return false;
	    }

	    Iterator<T> thisIterator = iterator();
	    Iterator<?> otherIterator = other.iterator();

	    while (thisIterator.hasNext())
	    {
	        T thisElement = thisIterator.next();
	        Object otherElement = otherIterator.next();

	        if (!Objects.equals(thisElement, otherElement))
	        {
	            return false;
	        }
	    }

	    return true;
	}

	@Override
	public int hashCode()
	{
	    int hashCode = 1;

	    for (T element : this)
	    {
	        hashCode = 31 * hashCode + Objects.hashCode(element);
	    }

	    return hashCode;
	}

}
