package org.dodo.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 一致性hash算法
 * @author maxlim
 *
 */
public class ConsistentHashing {
    private ConcurrentSkipListMap<Integer, String> hashingKeys = new ConcurrentSkipListMap<>();
    private volatile List<String> keys = new ArrayList<>();
    private int virtualNodesNumber = 100;
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public ConsistentHashing(List<String> keys, int virtualNodesNumber) {
        this.virtualNodesNumber = virtualNodesNumber > 0 ? virtualNodesNumber : this.virtualNodesNumber;
        hashingKeys(keys);
    }
    public ConsistentHashing(String key, int virtualNodesNumber) {
        this.virtualNodesNumber = virtualNodesNumber > 0 ? virtualNodesNumber : this.virtualNodesNumber;
        hashingKey(key);
    }

    public void hashingKey(String key) {
        try {
            readWriteLock.writeLock().lock();
            hashingKeys.put(hash(key), key);
            //虚拟节点
            for (int i = 0; i < virtualNodesNumber; i++) {
                hashingKeys.put(hash(virtualKey(key, i)), key);
            }
            this.keys.add(key);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public int hash(String key) {
        return key.hashCode();
    }

    public String virtualKey(String key, int i) {
        return (i * 10) + "$" + key;
    }

    public void hashingKeys(List<String> keys) {
        keys.forEach(key -> {
            hashingKey(key);
        });
    }

    public String select(String value) {
        try {
            readWriteLock.readLock().lock();
            int hashValue = hash(value);
            String key = hashingKeys.get(hashValue);
            if(key == null) {
                Map.Entry<Integer, String> entry = hashingKeys.higherEntry(hashValue);
                if(entry == null) {
                    entry = hashingKeys.firstEntry();
                }
                key = entry.getValue();
            }
            return key;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void rebuild(List<String> keys) {
        try {
            readWriteLock.writeLock().lock();
            List<String> newKeys = keys.stream().filter(key -> ! this.keys.contains(key)).collect(Collectors.toList());
            List<String> removedKeys = this.keys.stream().filter(key -> ! keys.contains(key)).collect(Collectors.toList());
            if(newKeys != null) hashingKeys(newKeys);
            if(removedKeys != null) removeKeys(removedKeys);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void removeKeys(List<String> removedKeys) {
        removedKeys.forEach(key -> {
            removeKey(key);
        });
    }

    public void removeKey(String key) {
        try {
            readWriteLock.writeLock().lock();
            hashingKeys.remove(hash(key), key);
            //虚拟节点
            for (int i = 0; i < virtualNodesNumber; i++) {
                hashingKeys.remove(hash(virtualKey(key, i)), key);
            }
            keys.remove(key);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
