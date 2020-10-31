package test.droolsTest;

import weka.core.Instances;

import java.util.HashMap;

class Pair {
    private Instances key;
    private HashMap<String, Class> value;

    public Pair() {
    }

    public Pair(Instances key, HashMap<String, Class> value) {
        this.key = key;
        this.value = value;
    }

    public Instances getKey() {
        return key;
    }

    public void setKey(Instances key) {
        this.key = key;
    }

    public HashMap<String, Class> getValue() {
        return value;
    }

    public void setValue(HashMap<String, Class> value) {
        this.value = value;
    }
}