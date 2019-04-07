package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @see IDictionary and the assignment page for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;
    private int dicSize;
    private final int height = 17;

    // You're encouraged to add extra fields (and helper methods) though!

    public ChainedHashDictionary() {
        this.chains =  makeArrayOfChains(this.height);
        this.dicSize = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }

    private int getHash(K key) {
        if (key == null) {
            return 0;
        } else {
            return Math.abs(key.hashCode()) % this.chains.length;
        }
    }

    @Override
    public V get(K key) {
        int hValue = getHash(key);
        IDictionary<K, V> dic = this.chains[hValue];
        if (dic != null) {
            return dic.get(key);
        } else {
            throw new NoSuchKeyException();
        }
    }

    @Override
    public V getOrDefault(K key, V defaultValue) {
        try {
            return this.get(key);
        } catch (Exception e){
            return defaultValue;
        }
    }

    private void resize() {
        IDictionary<K, V>[] newChains = makeArrayOfChains(2 * this.chains.length);
        IDictionary<K, V>[] oldChains = this.chains;
        this.chains = newChains;
        this.dicSize = 0;
        for (int i = 0; i < oldChains.length; i++) {
            if (oldChains[i] != null) {
                for (KVPair<K, V> pair : oldChains[i]) {
                    this.put(pair.getKey(), pair.getValue());
                }
            }
        }
    }

    @Override
    public void put(K key, V value) {
        if (this.dicSize == chains.length) {
            this.resize();
        }
        int hValue = getHash(key);
        if (this.chains[hValue] == null) {
            this.chains[hValue]= new ArrayDictionary<>();
        }
        if (!this.chains[hValue].containsKey(key)) {
            this.dicSize++;
        }
        this.chains[hValue].put(key, value);
    }

    @Override
    public V remove(K key) {
        int hValue = getHash(key);
        if (this.chains[hValue] != null) {
            this.dicSize--;
            V result = this.chains[hValue].remove(key);
            if (this.chains[hValue].size() == 0) {
                this.chains[hValue] = null;
            }
            return result;
        } else {
            throw new NoSuchKeyException();
        }
    }

    @Override
    public boolean containsKey(K key) {
        int hValue = getHash(key);
        if (this.chains[hValue] != null) {
            return this.chains[hValue].containsKey(key);
        } else {
            return false;
        }
    }

    @Override
    public int size() {
        return this.dicSize;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 3. Think about what exactly your *invariants* are. As a
     *    reminder, an *invariant* is something that must *always* be
     *    true once the constructor is done setting up the class AND
     *    must *always* be true both before and after you call any
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        int current;
        Iterator<KVPair<K, V>> currentItor;

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.current = -1;
            nextIter();
        }

        private boolean nextIter() {
            for (int i = current+1; i < this.chains.length; i++) {
                if (this.chains[i] != null && chains[i].size() > 0) {
                    this.current = i;
                    this.currentItor = chains[i].iterator();
                    return true;
                }
            }
            this.current++;
            this.currentItor = null;
            return false;
        }

        @Override
        public boolean hasNext() {
            if (this.currentItor != null && this.currentItor.hasNext()) {
                return true;
            } else {
                return nextIter();
            }
        }

        @Override
        public KVPair<K, V> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            } else {
                return this.currentItor.next();
            }
        }
    }
}
