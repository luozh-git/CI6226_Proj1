package ntu.ci6226;

import java.util.*;

/**
 * @author ley
 */
public class Person {
    private static Map personMap = new HashMap(600000);

    private String name;
    private int count;
    private int tmp;

    public Person(String n) {
        name = n;
        count = 0;
        personMap.put(name, this);
    }

    public void increment() {
        count++;

    }

    public String getName() {
        return name;
    }

    static public Person searchPerson(String name) {
        return (Person) personMap.get(name);
    }
}
