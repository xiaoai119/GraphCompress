package com.xfj.lab.lab;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.xfj.lab.algorithm.DFSTraversal;
import com.xfj.lab.data.Vertex;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created By xfj on 2017/11/5
 */
public class GSpanLab {
    public static String inputFileName = "peel.rdf";
    //顶点频繁阈值
    public static int frequentVertexShreshold=100;
    //边频繁阈值
    public static int frequentEdgeShreshold=50;
    public static UndirectedSparseGraph<Integer ,Integer> graph;
    public static Integer vertexId=0;
    public static Integer edgeId=0;
    public static HashMap<Integer,Vertex> vertexMap;
    public static HashMap<String,Integer> vertexIdMap;
    public static HashMap<String,Integer> edgeIdMap;
    public static HashMap<Integer, Boolean> isVisitedVertex;
    public static HashSet<String> vertexSet;
    public static HashSet<String> edgeSet;

    public static void main(String agrs[]) {
        Model model = getModelFromFile();
        HashMap<String, Long> frequentVertexMap = filterInfrequentVertex(model);
        HashMap<String, Long> frequentEdgeMap = filterInfrequentEdge(model);
        List<String> sortedVertexList = sortVertexAndEdge(frequentVertexMap, frequentEdgeMap);
        frequentVertexMap.clear();
        frequentEdgeMap.clear();
        initGraph(model);
        DFSTraversal dfsTraversal = new DFSTraversal(graph);
        dfsTraversal.traversal(isVisitedVertex,vertexMap);
    }

    private static void initGraph(Model model) {
        StmtIterator iter = model.listStatements();
        while(iter.hasNext()){
            getVertexSet(iter);
            getVertexMap();
            isVisitedVertex = vertexMap.entrySet().stream().map(entry -> Maps.immutableEntry(entry.getValue().getId(), false)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, HashMap::new));
            vertexIdMap = vertexMap.entrySet().stream().map(entry -> Maps.immutableEntry( entry.getValue().getName(),entry.getValue().getId())).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, HashMap::new));
        }
        iter.close();

        StmtIterator iter1 = model.listStatements();
        while(iter1.hasNext()){
            Statement statement = iter.nextStatement();
            Property predicate = statement.getPredicate();
            Resource subject = statement.getSubject();
            RDFNode object = statement.getObject();
            if(object instanceof Resource){
                Integer sub = vertexIdMap.get(subject.toString());
                Integer obj = vertexIdMap.get(object.toString());
                graph.addEdge(edgeIdMap.get(predicate.toString()),sub,obj);
            }
        }
    }

    private static void getVertexMap() {
        vertexSet.forEach(resource->{
            Vertex temp=Vertex.builder().
                        id(vertexId).
                        isvisitied(false).
                        name(resource).
                        build();
            vertexMap.put(vertexId,temp);
            graph.addVertex(vertexId);
            vertexId++;
            }
        );
        vertexSet.clear();
    }

    private static void getVertexSet(StmtIterator iter) {
        Statement statement = iter.nextStatement();
        Resource subject = statement.getSubject();
        RDFNode object = statement.getObject();
        Property predicate = statement.getPredicate();
        if(!edgeIdMap.containsKey(predicate.toString())){
            edgeIdMap.put(predicate.toString(),edgeId);
            edgeId++;
        }
        vertexSet.add(subject.toString());
        if(object instanceof Resource){
            vertexSet.add(object.toString());
        }
    }

    private static List<String> sortVertexAndEdge(HashMap<String, Long> frequentVertexMap, HashMap<String, Long> frequentEdgeMap) {
        List<String> vertexList=frequentVertexMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
        List<String> edgeList=frequentEdgeMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
        List<String> sortedVertexList = vertexList.stream().
                sorted((v1, v2) -> Long.compare(frequentVertexMap.get(v2), frequentVertexMap.get(v1))).
                collect(Collectors.toList());
        List<String> sortedEdgeList = edgeList.stream().
                sorted((v1, v2) -> Long.compare(frequentEdgeMap.get(v2), frequentEdgeMap.get(v1))).
                collect(Collectors.toList());
        return sortedVertexList;
    }

    private static HashMap<String, Long> filterInfrequentVertex(Model model) {
        StmtIterator iter = model.listStatements();
        HashMultiset<String> multiSet = HashMultiset.create();
        while (iter.hasNext()){
            Statement statement = iter.nextStatement();
            Resource subject=statement.getSubject();
            RDFNode object = statement.getObject();
            if(object instanceof Resource){
                multiSet.add(object.toString());
                multiSet.add(subject.toString());
            }
        }
        return multiSet.entrySet().stream().filter(entry -> entry.getCount() > frequentVertexShreshold).
                map(entry -> Maps.immutableEntry(entry.getElement(), (long) entry.getCount())).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, HashMap::new));
    }

    private static HashMap<String, Long> filterInfrequentEdge(Model model) {
        StmtIterator iter = model.listStatements();
        HashMultiset<String> multiSet = HashMultiset.create();
        while (iter.hasNext()){
            Statement statement = iter.nextStatement();
            Property predicate = statement.getPredicate();
            multiSet.add(predicate.toString());
        }
        return multiSet.entrySet().stream().filter(entry -> entry.getCount() > frequentEdgeShreshold).
                map(entry -> Maps.immutableEntry(entry.getElement(), (long) entry.getCount())).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, HashMap::new));
    }

    private static Model getModelFromFile() {
        Model model = ModelFactory.createDefaultModel();
	        /*
	         *  使用 FileManager 查找文件
	         */
        InputStream in = FileManager.get().open(inputFileName);
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + inputFileName + " not found");
        }
	        /*
	         * 读取RDF/XML 文件
	         */
        model.read(in, null);
        return model;
    }
}
