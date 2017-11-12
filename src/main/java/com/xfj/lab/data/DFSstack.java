package com.xfj.lab.algorithm;

import java.util.ArrayList;

/**
 * Created By xfj on 2017/11/12
 */
public class DFSstack {
    ArrayList<Integer> stackList=new ArrayList<>();

    private void push(Integer e){
        stackList.add(e);
    }

    private Integer pop(){
        int size= stackList.size();
        int e=stackList.get(size-1);
        stackList.remove(size-1);
        return e;
    }

    public boolean isEmpty(){
        return stackList.isEmpty();
    }
}
