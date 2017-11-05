package com.xfj.lab.lab;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
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
    public static void main(String agrs[]) {
        Model model = getModelFromFile();
        HashMap<String, Long> frequentVertexMap = filterInfrequentVertex(model);
        HashMap<String, Long> frequentEdgeMap = filterInfrequentEdge(model);
        List<String> sortedVertexList = sortVertexAndEdge(frequentVertexMap, frequentEdgeMap);
        // TODO: 2017/11/5 test
        sortedVertexList.get(0);
        sortedVertexList.get(0);

        frequentVertexMap.clear();
        frequentEdgeMap.clear();
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
