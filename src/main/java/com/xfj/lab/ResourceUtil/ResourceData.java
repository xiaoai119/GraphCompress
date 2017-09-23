package com.xfj.lab.ResourceUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.jena.rdf.model.Resource;

import java.util.HashSet;
import java.util.Set;
@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * Created by xfj on 2017/9/24
 */

public class ResourceData {

    Resource resource;
    String uri;
    Integer depend=0;//如果满足依赖压缩条件则为1，默认为0
    String type;
    Set<String> family;
    Integer hyper=0;
}
