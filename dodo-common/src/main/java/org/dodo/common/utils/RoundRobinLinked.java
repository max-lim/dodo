package org.dodo.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 轮询算法
 * @author maxlim
 *
 */
public class RoundRobinLinked {
    private class Node {
        String key;//key of org.dodo.rpc.Node
        volatile int weight;
        AtomicInteger atomicCounter;
        AtomicBoolean closed = new AtomicBoolean(false);

        public Node(String key, int weight) {
            this.key = key;
            this.weight = weight;
            this.atomicCounter = new AtomicInteger(0);
        }
        Node open() {
            closed.set(false);
            atomicCounter.set(0);
            return this;
        }
        String selected() {
            int counter = atomicCounter.get();
            if (counter >= weight || closed.get()) {
                return null;
            }
            counter = atomicCounter.incrementAndGet();
            if (counter < weight) {
                return key;
            }
            else if ( ! closed.get()) {
                if (closed.compareAndSet(false, true)) {
                    atomicCounter.set(0);
                    return key;
                }
            }
            return null;
        }
        @Override
        public String toString() {
            return "Node{" +
                    "key='" + key + '\'' +
                    ", weight=" + weight +
                    ", atomicCounter=" + atomicCounter +
                    ", closed=" + closed +
                    '}';
        }
    }
    private volatile List<Node> nodes = new ArrayList<>();
    private AtomicInteger atomicIndex = new AtomicInteger();
    private Map<String, Integer> nodesWithWeightsOrdered = new LinkedHashMap<>();
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static RoundRobinLinked build(List<String> keys) {
        RoundRobinLinked roundRobinLinked = new RoundRobinLinked();
        keys.forEach(key -> roundRobinLinked.nodes.add(roundRobinLinked.new Node(key, 1)));
        return roundRobinLinked;
    }

    public static RoundRobinLinked build(Map<String, Integer> nodesWithWeight) {
        RoundRobinLinked roundRobinLinked = new RoundRobinLinked();
        roundRobinLinked.nodesWithWeightsOrdered = nodesWithWeight;
        nodesWithWeight.forEach((key, weight) -> roundRobinLinked.nodes.add(roundRobinLinked.new Node(key, weight)));
        return roundRobinLinked;
    }

    /**
     * 并不能保证100%严格按照权重分配，会有偶发性的极小误差
     * @return
     */
    public String selectKey() {
        try {
            readWriteLock.readLock().lock();
            do {
                int index = atomicIndex.get();
                if(index < nodes.size()) {
                    String key = nodes.get(index).selected();
                    if(key != null) return key;
                }

                atomicIndex.compareAndSet(index, index + 1);
                index = atomicIndex.get();
                while (index >= nodes.size()) {
                    atomicIndex.compareAndSet(index, 0);
                    index = atomicIndex.get();
                }

                String key = nodes.get(index).open().selected();
                if(key != null) return key;
            } while (true);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * 重置节点权重时重建轮询链表
     * @param nodesWithWeightsOrdered
     */
    public void rebuild(Map<String, Integer> nodesWithWeightsOrdered) {
        try {
            readWriteLock.writeLock().lock();
            this.nodesWithWeightsOrdered.clear();
            this.nodesWithWeightsOrdered.putAll(nodesWithWeightsOrdered);

            if( ! nodes.isEmpty()) {
                nodes.forEach(node -> {
                    if(nodesWithWeightsOrdered.containsKey(node.key)) {
                        node.weight = nodesWithWeightsOrdered.get(node.key);
                    }
                });
            }
            else {
                nodesWithWeightsOrdered.forEach((key, weight) -> nodes.add(new Node(key, weight)));
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public RoundRobinLinked addNode(String key) {
        try {
            readWriteLock.writeLock().lock();
            boolean exists = false;
            for (Node node : nodes) {
                if(node.key.equals(key)) {
                    exists = true;
                    break;
                }
            }
            if( ! exists) {
                nodes.add(new Node(key, nodesWithWeightsOrdered.containsKey(key) ? nodesWithWeightsOrdered.get(key) : 1));
            }
            return this;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void removeNode(String key) {
        try {
            readWriteLock.writeLock().lock();
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                if(node.key.equals(key)) {
                    nodes.remove(i);
                    break;
                }
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
