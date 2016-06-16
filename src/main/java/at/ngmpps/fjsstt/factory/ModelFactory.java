package at.ngmpps.fjsstt.factory;

import at.ngmpps.fjsstt.model.MyBean;

import java.net.URI;
import java.util.*;


public class ModelFactory {
    private static final Map<Integer, Float> myMap;

    static {
        Map<Integer, Float> aMap = new HashMap<>();
        aMap.put(1, 1.1f);
        aMap.put(2, 1.2f);
        myMap = Collections.unmodifiableMap(aMap);
    }

    private static final List<String> myList;

    static {
        List<String> aList = new ArrayList<>();
        aList.add("entry1");
        aList.add("entry2");
        myList = Collections.unmodifiableList(aList);
    }

    public static MyBean createBean(String uri) {
        final MyBean myBean = new MyBean(URI.create(uri), "mybean", "mydata", myList, myMap);
        return myBean;
    }
}
