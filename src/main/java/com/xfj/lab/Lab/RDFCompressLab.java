package GraphCompress;
import java.io.InputStream;
import java.util.*;

import com.xfj.lab.algorithm.CompressAlgorithm;
import com.xfj.lab.util.URIUtil;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.EdgeType;
public class RDFCompressLab{
    //public static String inputFileName = "swetodblp_april_2008.rdf";
    public static String inputFileName = "peel.rdf";
    //public static String inputFileName = "peel.rdf";
    @SuppressWarnings("unchecked")
    public static void main(String agrs[])
    {
        Model model = ModelFactory.createDefaultModel();


	        /*
	         *  使用 FileManager 查找文件
	         */
        InputStream in = FileManager.get().open( inputFileName );
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + inputFileName + " not found");
        }



	        /*
	         * 读取RDF/XML 文件
	         */
        model.read(in, null);
        StmtIterator iter = model.listStatements();
        Set<Resource> resSet=new HashSet<Resource>(); //记录资源信息
        Set<String> preSet=new HashSet<String>();
        DirectedSparseGraph<String ,String> g=new DirectedSparseGraph<String, String>();
        DirectedSparseGraph<String ,String> g1=new DirectedSparseGraph<String, String>();
        DirectedSparseGraph<String ,String> g2=new DirectedSparseGraph<String, String>();
        DirectedSparseGraph<String ,String> g3=new DirectedSparseGraph<String, String>();



        //定义相关试验数据
        int triple=0;//所有三元组的个数
        Double link=0.0;//去除掉不符合TOG定义后的边，剩余的边的个数
        int rel;//边的种类数
        int objnum;//图中主语和宾语的个数（节点数）
        double diameter=0;//图的半径
        String type=null;


	        /*
	         * 向resSet中加入所有subject和object的uri
	         */
        while(iter.hasNext()){

            Statement stmt=iter.nextStatement();
            triple=triple+1;//每一个statement代表一个triple
            Resource subject=stmt.getSubject();
            if(subject instanceof Resource)
            {
                type= URIUtil.getType(model, subject);//获取主语的类型

            }
            else
            {
	            	/*
	            	 * 主语不是resource
	            	 */

                type=null;
                continue;
            }
            if(type!=null)
            {
                //关系中含有rdf，rdfs，owl的一般不是实例之间的关系
                if(stmt.getPredicate().toString().contains("owl")||stmt.getPredicate().toString().contains("rdf")||stmt.getPredicate().toString().contains("rdfs"))
                {
                    type=null;
                    continue;
                }
            }
            else
            {
                continue;
            }

            RDFNode object = stmt.getObject();
            if(object instanceof Resource)
            {
	            	/*
	            	 * 获取object的类型
	            	 */
                type=URIUtil.getType(model, (Resource)object);

            }
            else
            {
                type=null;
                continue;
            }
            if(type!=null)
            {
	            	/*
	            	 * preset将predicate去重，size为rel值
	            	 */
                preSet.add(stmt.getPredicate().toString());
                if(!g.containsVertex(subject.toString()))
                {
	            		/*
	            		 * 如果不包含subject，向resset中添加subject
	            		 */
                    g.addVertex(subject.toString());
                    resSet.add(subject);

                }
                if(!g.containsVertex(object.toString()))
                {
                    g.addVertex(object.toString());

                    resSet.add((Resource) object);

                    g.addEdge(subject.toString()+"_"+object.toString(),subject.toString(),object.toString(),EdgeType.DIRECTED);

                }
                else
                {
                    if(!g.containsVertex(subject.toString()))
                    {
                        g.addVertex(subject.toString());

                        resSet.add(subject);

                    }
                    type=null;
                    g.addEdge(subject.toString()+"_"+object.toString(),subject.toString(),object.toString(),EdgeType.DIRECTED);


                }

            }

        }
	        /*
	         * 输出图的统计信息
	         */
        link=(double) g.getEdgeCount();
        rel=preSet.size();
        objnum=g.getVertexCount();

        System.out.println("triple:"+triple);
        System.out.println("link:"+link);
        CompressAlgorithm.getLinkNum(resSet, g);
        System.out.println("rel:"+rel);
        System.out.println("objnum:"+objnum);
        System.out.println("avgdegree:"+(link*2/objnum));
        System.gc();

        //计算diameter速度较慢
        //System.out.println("diameter:"+DistanceStatistics.diameter(g,new UnweightedShortestPath<String, String>(g), true));

        //计算BetweenessCentrality速度较慢
        //BetweennessCentrality ranker = new BetweennessCentrality(g);
        //ranker.evaluate();

        g1=(DirectedSparseGraph<String, String>) CompressAlgorithm.EquCompress(resSet, g, model);
        System.out.println("afterEcompress:");
        System.out.println("link:"+g1.getEdgeCount());
        System.out.println("objnum:"+g1.getVertexCount());
        System.gc();

        g2=(DirectedSparseGraph<String, String>) CompressAlgorithm.DepCompress(resSet, g, model);
        System.out.println("afterDcompress:");
        System.out.println("link:"+g2.getEdgeCount());
        System.out.println("objnum:"+g2.getVertexCount());
        System.gc();

        g3=(DirectedSparseGraph<String, String>) CompressAlgorithm.HybCompress(resSet, g, model);
        System.out.println("afterHcompress:");
        System.out.println("link:"+g3.getEdgeCount());
        System.out.println("objnum:"+g3.getVertexCount());
        System.gc();



    }
}