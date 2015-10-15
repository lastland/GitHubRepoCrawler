package com.liyaos.concurrent.factorial;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

/**
 * Created by lastland on 15/10/9.
 */
public class MyBag {
    private Bag<Object> bag;

    public MyBag() {
        bag = new HashBag<>();
    }

    public int getObject(Object ob) {
        return bag.getCount(ob);
    }

    public void addObject(Object ob) {
        bag.add(ob);
    }
}
