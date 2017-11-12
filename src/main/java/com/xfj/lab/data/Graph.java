package com.xfj.lab.data;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created By xfj on 2017/11/12
 */
@Data
@AllArgsConstructor
public class Graph {
    DirectedSparseGraph<String ,String> g;

    public Graph(){
        g=new DirectedSparseGraph<String, String>();
    }

    public void addEdge(String edge,Vertex v1,Vertex v2){
        g.addEdge(edge,v1.name,v2.name);
    }

    public void addVertex(Vertex v){
        g.addVertex(v.name);
    }
}
