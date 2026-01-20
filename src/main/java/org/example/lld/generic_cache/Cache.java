package org.example.lld.generic_cache;

import java.util.*;


////syntax
////<T> <E>
////
////
//// int calcualte(int a)
//// <T> T calculate(T a)
//
//
//
//class Adder<A,B,C>{
//
//    public C add(A a, B b){
//
//    }
//}





public class Cache<K,V> {
    private CacheStorage<K,V> storage;
    private CacheEvictionStrategy<K> evictionStrategy;


    public Cache(CacheStorage cacheStorage, CacheEvictionStrategy cacheEvictionStrategy){
        this.storage = cacheStorage;
        this.evictionStrategy = cacheEvictionStrategy;
    }

    public V get(K key){
        V value = this.storage.get(key);
        this.evictionStrategy.keyAccessed(key);
        return value;
    }

    public void put(K key, V value){
        if(this.storage.isFull() && this.storage.get(key)==null){
            K keyToBeRemoved = evictionStrategy.evictKey();
            this.storage.remove(keyToBeRemoved);
        }
        this.storage.put(key, value);
        this.evictionStrategy.keyAccessed(key);
    }
}



interface CacheEvictionStrategy<K>{
    K evictKey();
    void keyAccessed(K key);
}


interface CacheStorage<K, V>{
    V get(K key);
    void put(K key, V value);
    void remove(K key);
    boolean isFull();
}

class InMemoryCacheStorage<K,V> implements CacheStorage<K,V> {
    private Map<K, V> map;
    private int capacity;

    public InMemoryCacheStorage(int capacity){
        this.capacity = capacity;
        this.map = new HashMap<>();
    }

    @Override
    public V get(K key) {
        return this.map.get(key);
    }

    @Override
    public void put(K key, V value) {
        this.map.put(key, value);
    }

    @Override
    public void remove(K key) {
        this.map.remove(key);
    }

    @Override
    public boolean isFull() {
        return this.map.size() == this.capacity;
    }
}


class LRUEvictionStrategy<K> implements CacheEvictionStrategy<K> {

    private Deque<K> accessOrderList;
    private Set<K> existingKeys;

    public LRUEvictionStrategy(){
        this.accessOrderList = new LinkedList<>();
        this.existingKeys = new HashSet<>();
    }

    @Override
    public K evictKey() {
        K keyToBeRemoved = this.accessOrderList.removeFirst();
        this.existingKeys.remove(keyToBeRemoved);
        return keyToBeRemoved;
    }

    @Override
    public void keyAccessed(K key) {
        if (this.existingKeys.contains(key)){
            this.accessOrderList.remove(key);
        }
        this.accessOrderList.addLast(key);
        this.existingKeys.add(key);
    }
}


class CacheDemo{
    public static void main(String[] args) {
        int capacity = 3;
//        CacheStorage<String,String> inMemoryStorage = new InMemoryCacheStorage(capacity);
//        CacheEvictionStrategy<String> lruEvictionStrategy = new LRUEvictionStrategy();
//
//        Cache<String,String> cache = new Cache(inMemoryStorage, lruEvictionStrategy);
//
//        cache.put("1", "Apple");
//        cache.put("2", "Banana");
//        cache.put("3", "Cherry");
//        cache.put("4", "Date");
//
//        System.out.println("Should be none " + cache.get("1"));
//        System.out.println(cache.get("4"));
//        System.out.println(cache.get("3"));


        CacheStorage<Integer, String> cacheStorage = new InMemoryCacheStorage<>(capacity);
        CacheEvictionStrategy<Integer> evictionStrategy = new LRUEvictionStrategy<>();

        Cache<Integer,String> cache = new Cache<>(cacheStorage, evictionStrategy);

        cache.put(10, "Hello");
        cache.put(20, "World");
        cache.put(30, "Java");
        cache.put(40, "Python");
        System.out.println(cache.get(20));


    }
}