package com.xfj.lab.data;

import java.util.ArrayList;

/**
 * Created By xfj on 2017/11/12
 */
public class DFSstack {
    ArrayList<Integer> stackList=new ArrayList<>();

    public void push(Integer e){
        stackList.add(e);
    }

    public Integer pop(){
        int size= stackList.size();
        int e=stackList.get(size-1);
        stackList.remove(size-1);
        return e;
    }

    public Integer getTop(){
        int size= stackList.size();
        return stackList.get(size-1);
    }

    public boolean isEmpty(){
        return stackList.isEmpty();
    }
}
