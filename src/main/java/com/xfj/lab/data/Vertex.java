package com.xfj.lab.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by xfj on 2017/9/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphNode {
    String name;
    Integer id;
    Integer dfsCode;
    boolean isvisitied;
}
