package gaia.cu9.ari.gaiaorbit.util;

import com.badlogic.gdx.utils.LongMap;

public class LargeLongMap<T> {

    private int N = 1;
    private LongMap<T>[] maps;
    private boolean empty = true;

    /**
     * Creates a LargeLongMap with the given number of backend maps
     * @param N
     */
    public LargeLongMap(int N) {
        this.N = N;
        this.maps = new LongMap[N];
        for (int i = 0; i < N; i++) {
            this.maps[i] = new LongMap<T>();
        }
    }

    public boolean containsKey(Long key) {
        int idx = (int) (key % N);
        return maps[idx].containsKey(key);
    }

    public void put(Long key, T value) {
        int idx = (int) (key % N);
        maps[idx].put(key, value);
        empty = false;
    }

    public T get(Long key) {
        int idx = (int) (key % N);
        return maps[idx].get(key);
    }

    public int size() {
        int s = 0;
        for (LongMap map : maps)
            s += map.size;
        return s;
    }

    public boolean isEmpty() {
        return empty;
    }
}
