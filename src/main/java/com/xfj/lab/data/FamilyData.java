package com.xfj.lab.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.jena.rdf.model.Resource;

import java.util.Set;

/**
 * Created by xfj on 2017/9/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyData {
    String family=null;
    Set<Resource> resourceSet=null;
    public void addResource(Resource resource)
    {
        resourceSet.add(resource);
    }
}

