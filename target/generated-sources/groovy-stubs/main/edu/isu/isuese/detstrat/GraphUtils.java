package edu.isu.isuese.detstrat;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@groovy.lang.Singleton() public class GraphUtils
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> spanningTree(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, ? extends edu.isu.isuese.detstrat.impl.Relationship> network) { return (com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship>)null;}
public  java.util.List<edu.isu.isuese.detstrat.impl.Node> topologicalSort(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, ? extends edu.isu.isuese.detstrat.impl.Relationship> network) { return (java.util.List<edu.isu.isuese.detstrat.impl.Node>)null;}
public  boolean hasCycle(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, ? extends edu.isu.isuese.detstrat.impl.Relationship> network) { return false;}
public  boolean isCyclicUtil(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, ? extends edu.isu.isuese.detstrat.impl.Relationship> network, edu.isu.isuese.detstrat.impl.Node n, java.util.List<edu.isu.isuese.detstrat.impl.Node> visited, java.util.Stack<edu.isu.isuese.detstrat.impl.Node> recStack) { return false;}
public  void reorderNodes(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, ? extends edu.isu.isuese.detstrat.impl.Relationship> network) { }
public  java.lang.Object markCycles(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, ? extends edu.isu.isuese.detstrat.impl.Relationship> graph) { return null;}
}
