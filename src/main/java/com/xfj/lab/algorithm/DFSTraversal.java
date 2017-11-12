package com.xfj.lab.algorithm;

import com.xfj.lab.data.DFSstack;
import com.xfj.lab.data.Vertex;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created By xfj on 2017/11/12
 */
public class DFSTraversal {
    DFSstack dfsSack;
    UndirectedSparseGraph<Integer ,Integer> g;
    Integer dfsCode;
    public DFSTraversal(UndirectedSparseGraph<Integer ,Integer> g){
        this.g=g;
        dfsSack = new DFSstack();
        Integer dfsCode=0;
    }
    public void traversal(HashMap<Integer,Boolean> isVisitedVertex,HashMap<Integer,Vertex> vertexMap){
        dfsSack.push(0);
        while(!dfsSack.isEmpty()) {
            doTraversal(isVisitedVertex, vertexMap);
        }
    }

    private void doTraversal(HashMap<Integer, Boolean> isVisitedVertex, HashMap<Integer, Vertex> vertexMap) {
        Integer top = dfsSack.getTop();
        Integer nextVertex = getNextVertex(isVisitedVertex, top);
        getDFSCode(vertexMap, nextVertex);
    }

    private void getDFSCode(HashMap<Integer, Vertex> vertexMap, Integer nextVertex) {
        if (nextVertex == -1) {
            dfsSack.pop();
        } else {
            Vertex vertex = vertexMap.get(nextVertex);
            vertex.setIsvisitied(true);
            vertex.setDfsCode(dfsCode);
            dfsCode++;
            dfsSack.push(vertex.getId());
        }
    }

    private Integer getNextVertex(HashMap<Integer, Boolean> isVisitedVertex, Integer top) {
        Integer nextVertex=-1;
        ArrayList<Integer> neighbors = new ArrayList<>(g.getNeighbors(top));
        for(Integer id:neighbors){
            if(!isVisitedVertex.get(id)){
                nextVertex=id;
                break;
            }
        }
        return nextVertex;
    }

}
