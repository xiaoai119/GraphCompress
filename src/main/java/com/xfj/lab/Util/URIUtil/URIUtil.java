package com.xfj.lab.Util.URIUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.xfj.lab.Util.DBUtil.DBConnectionUtil;
import com.xfj.lab.Util.DBUtil.DBOperationUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
/**
 * Created by xfj on 2017/9/24
 */
public class URIUtil {

    public static String getType(Model model,Resource subject)
    {			Property Type=model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        StmtIterator it1=model.listStatements(subject, Type, (RDFNode) null);
        while(it1.hasNext())
        {
            Statement stmt1=it1.next();
            return stmt1.getObject().toString();

        }
        return null;
    }


    public static int getId(Resource res,String tableName) throws SQLException
    {
        Connection con = DBConnectionUtil.getConn();
        java.sql.Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT id FROM "+tableName+" WHERE resource='"+res.toString()+"'");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //ResultSet rs=DBUtil.query("SELECT id FROM "+tableName+" WHERE resource='"+res.toString()+"'");
        if(rs.next())
        {
            int result=Integer.parseInt(rs.getString("id"));
            DBConnectionUtil.close(con, stmt, null);
            return result;

        }
        return (Integer) null;
    }

    public static void Write(Model model)
    {
        DBConnectionUtil.getConn();
        Property Type=model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        ResIterator resit=model.listSubjectsWithProperty(Type);
        Resource typedResource=null;
        int index=0;
        while(resit.hasNext())
        {
            typedResource=resit.next();
            DBOperationUtil.excute("INSERT INTO jamendo (id, resource) VALUES ('"+index+"','"+typedResource.toString()+"')");
            index++;
        }
    }

    public static String getLocalName(String uri)
    {
        Integer div=uri.lastIndexOf("/");
        return uri.substring(div+1, uri.length());
    }

    public static Graph<String, String> EquCompress(Set<Resource> resSet,Graph<String,String> g,Model model)
    {
        Set<String> familySet=new HashSet<String>();
        Set<String> family=new HashSet<String>();
        //Map<String,ArrayList<String>>familyMap=new HashMap<String,ArrayList<String>>();
        Iterator<Resource> resIt = resSet.iterator();
        while(resIt.hasNext())
        {
            family.clear();
            Resource resource=resIt.next();
            //当resource为subject时
            StmtIterator it1=model.listStatements(resource, null, (RDFNode) null);
            family.add(URIUtil.getType(model, resource));
            while(it1.hasNext())
            {
                Statement stmt=it1.next();
                Resource subject=stmt.getSubject();
                Property predicate=stmt.getPredicate();
                RDFNode object = stmt.getObject();
                if(predicate.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))
                {
                    continue;
                }
                if(!(object instanceof Resource))
                {
                    continue;
                }
                family.add("<"+predicate.toString()+","+object.toString()+">");
            }

            //当resource作为objcet时
            StmtIterator it2=model.listStatements(null, null, resource);
            while(it2.hasNext())
            {
                Statement stmt=it2.next();
                Resource subject=stmt.getSubject();
                Property predicate=stmt.getPredicate();
                RDFNode object = stmt.getObject();
                if(predicate.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))
                {
                    continue;
                }
                if(!(object instanceof Resource))
                {
                    continue;
                }
                family.add("<"+subject.toString()+","+predicate.toString()+">");
            }



            if(familySet.contains(family.toString()))
            {

                g.removeVertex(resource.toString());

                //System.out.println(resource.toString());

                //System.out.println(family.toString());
            }
            else
            {
                familySet.add(family.toString());
            }

        }
        return g;
    }

    public static Graph<String, String> DepCompress(Set<Resource> resSet,Graph<String,String> g,Model model)
    {
        Iterator<Resource> resourceIt=null;
        while(true)
        {
            Integer dependflag=0;
            resourceIt=resSet.iterator();

            while(resourceIt.hasNext())
            {
                Integer children=0;
                Integer dependNum=0;
                Resource resource=resourceIt.next();
                //若已经确认该点
                if(!g.containsVertex(resource.toString()))
                {
                    continue;
                }
                if(URIUtil.getType(model, resource)==null)
                {
                    continue;
                }
                //若有多个父节点，跳过
                StmtIterator subit=model.listStatements(null,null,resource);
                if(!subit.hasNext())
                {
                    continue;
                }
                while(subit.hasNext())
                {
                    Statement st=subit.next();
                    Resource suburi=st.getSubject();
                    if(suburi instanceof Resource&&(URIUtil.getType(model, suburi)!=null))
                    {
                        break;
                    }
                    if(suburi==null)
                    {
                        continue;
                    }

                }
                int b=0;
                while(subit.hasNext())
                {
                    Statement st=subit.next();
                    Resource suburi=st.getSubject();
                    if(suburi instanceof Resource&&(URIUtil.getType(model, suburi)!=null))
                    {
                        b=1;
                        break;
                    }
                    if(suburi==null)
                    {
                        continue;
                    }

                }
                if(b==1)
                {
                    b=0;
                    continue;
                }
                StmtIterator it=model.listStatements(resource, null, (RDFNode) null);
                while(it.hasNext())
                {

                    String type=null;
                    Statement st=it.next();
                    RDFNode object=st.getObject();

                    if(st.getPredicate().toString().contains("owl")||st.getPredicate().toString().contains("rdf")||st.getPredicate().toString().contains("rdfs"))
                    {

                        continue;
                    }
                    if(!(object instanceof Resource))
                    {
                        continue;
                    }
                    if(URIUtil.getType(model, (Resource)object)==null)
                    {
                        continue;
                    }
                    children=children+1;
                    if(!g.containsVertex(object.toString()))
                    {
                        dependNum=dependNum+1;
                    }

                }
                if(children==dependNum)
                {
                    //System.out.println(resource.toString());
                    g.removeVertex(resource.toString());
                    dependflag=1;
                }
            }

            if(dependflag==0)
            {
                break;
            }

        }
        return g;

    }


    public static Graph<String, String> HybCompress(Set<Resource> resSet,Graph<String,String> g,Model model)
    {
        int vnum=g.getVertexCount();
        int ednum=g.getEdgeCount();
        int agg=0;
        g=(DirectedSparseGraph<String, String>) URIUtil.EquCompress(resSet, g, model);
        agg=agg+1;
        System.out.println("agg:"+agg);
        System.out.println("link:"+g.getEdgeCount());
        System.out.println("objnum:"+g.getVertexCount());
        Iterator<Resource> resourceIt=null;
        while(true)
        {
            if(vnum==g.getVertexCount()&&ednum==g.getEdgeCount())
            {
                break;
            }

            else
            {
                resourceIt=resSet.iterator();
                while(resourceIt.hasNext())
                {
                    Resource resource=resourceIt.next();
                    if(!g.containsVertex(resource.toString()))
                    {
                        resSet.remove(resource.toString());
                    }
                }
                if(agg%2==0)
                {
                    vnum=g.getVertexCount();
                    ednum=g.getEdgeCount();
                    g=(DirectedSparseGraph<String, String>) URIUtil.EquCompress(resSet, g, model);
                    agg=agg+1;
                    System.out.println("agg:"+agg);
                    System.out.println("link:"+g.getEdgeCount());
                    System.out.println("objnum:"+g.getVertexCount());
                }
                else
                {
                    vnum=g.getVertexCount();
                    ednum=g.getEdgeCount();
                    g=(DirectedSparseGraph<String, String>) URIUtil.DepCompress(resSet, g, model);
                    agg=agg+1;
                    System.out.println("agg:"+agg);
                    System.out.println("link:"+g.getEdgeCount());
                    System.out.println("objnum:"+g.getVertexCount());

                }
            }
        }
        System.out.println("agg:"+(agg-1));
        return g;

    }

    public static void getLinkNum(Set<Resource> resSet,Graph<String,String> g)
    {
        Iterator it=resSet.iterator();
        int link0=0;
        int link1=0;
        int link2=0;
        int link3=0;
        int link4=0;
        int link5=0;
        while(it.hasNext())
        {
            String resource=it.next().toString();
            int link=g.getOutEdges(resource).size()+g.getInEdges(resource).size();
            if(link>=1)
            {
                link0=link0+1;
            }
            if(link>=10)
            {
                link1=link+1;
            }
            if(link>=100)
            {
                link2=link2+1;
            }
            if(link>=1000)
            {
                link3=link3+1;
            }
            if(link>=10000)
            {
                link4=link4+1;
            }
            if(link>=100000)
            {
                link5=link5+1;
            }

        }
        System.out.println("link0:"+link0);
        System.out.println("link1:"+link1);
        System.out.println("link2:"+link2);
        System.out.println("link3:"+link3);
        System.out.println("link4:"+link4);
        System.out.println("link5:"+link5);
    }


}


