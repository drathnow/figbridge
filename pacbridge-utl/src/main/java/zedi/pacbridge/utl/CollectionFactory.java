package zedi.pacbridge.utl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CollectionFactory {

    @SuppressWarnings("rawtypes")
    public Map newSynchronizedMap(Map<?, ?> map) {
        return Collections.synchronizedMap(map);
    }
    
    @SuppressWarnings("rawtypes")
    public Set newSynchronizedMap(Set<?> set) {
        return Collections.synchronizedSet(set);
    }

    @SuppressWarnings("rawtypes")
    public Set newSynchronizedSet(Set<?> set) {
        return Collections.synchronizedSet(set);
    }

    @SuppressWarnings("rawtypes")
    public Map newTreeMap(TreeMap<?, ?> treeMap) {
        return treeMap;
    }

    @SuppressWarnings("rawtypes")
    public Set newSet(Set<?> set) {
        return set;
    }
    
    @SuppressWarnings("rawtypes")
    public LinkedList newLinkedList(LinkedList<?> linkedList) {
        return linkedList;
    }

    @SuppressWarnings("rawtypes")
    public List newSynchronizedList(List<?> arrayList) {
        return Collections.synchronizedList(arrayList); 
    }
}
