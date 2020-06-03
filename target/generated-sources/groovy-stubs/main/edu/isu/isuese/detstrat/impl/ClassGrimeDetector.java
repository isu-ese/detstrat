package edu.isu.isuese.detstrat.impl;

import com.google.common.graph.*;
import edu.isu.isuese.datamodel.*;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

public class ClassGrimeDetector
  extends edu.isu.isuese.detstrat.impl.AbstractGrimeDetector {
;
protected  java.lang.Object calculateTCC(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, edu.isu.isuese.datamodel.Method excludedMethod) { return null;}
protected  java.lang.Object calculateTCC(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { return null;}
protected  java.lang.Object calculateRCI(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, edu.isu.isuese.datamodel.Method exclude) { return null;}
protected  java.lang.Object calculateRCI(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { return null;}
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  com.google.common.collect.BiMap<edu.isu.isuese.datamodel.Field, edu.isu.isuese.detstrat.impl.Node> getFieldBiMap() { return (com.google.common.collect.BiMap<edu.isu.isuese.datamodel.Field, edu.isu.isuese.detstrat.impl.Node>)null;}
public  void setFieldBiMap(com.google.common.collect.BiMap<edu.isu.isuese.datamodel.Field, edu.isu.isuese.detstrat.impl.Node> value) { }
public  com.google.common.collect.BiMap<edu.isu.isuese.datamodel.Method, edu.isu.isuese.detstrat.impl.Node> getMethodBiMap() { return (com.google.common.collect.BiMap<edu.isu.isuese.datamodel.Method, edu.isu.isuese.detstrat.impl.Node>)null;}
public  void setMethodBiMap(com.google.common.collect.BiMap<edu.isu.isuese.datamodel.Method, edu.isu.isuese.detstrat.impl.Node> value) { }
public  java.util.Map<org.apache.commons.lang3.tuple.Triple<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node>, java.lang.Boolean> getMethodPairs() { return (java.util.Map<org.apache.commons.lang3.tuple.Triple<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node>, java.lang.Boolean>)null;}
public  void setMethodPairs(java.util.Map<org.apache.commons.lang3.tuple.Triple<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node>, java.lang.Boolean> value) { }
public  java.util.Map<org.apache.commons.lang3.tuple.Triple<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node>, org.apache.commons.lang3.tuple.Pair<java.lang.Boolean, java.lang.Boolean>> getMethodPairsIndirect() { return (java.util.Map<org.apache.commons.lang3.tuple.Triple<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node>, org.apache.commons.lang3.tuple.Pair<java.lang.Boolean, java.lang.Boolean>>)null;}
public  void setMethodPairsIndirect(java.util.Map<org.apache.commons.lang3.tuple.Triple<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node>, org.apache.commons.lang3.tuple.Pair<java.lang.Boolean, java.lang.Boolean>> value) { }
public  com.google.common.collect.Table<edu.isu.isuese.detstrat.impl.Node, java.lang.String, java.lang.Double> getMethodDeltas() { return (com.google.common.collect.Table<edu.isu.isuese.detstrat.impl.Node, java.lang.String, java.lang.Double>)null;}
public  void setMethodDeltas(com.google.common.collect.Table<edu.isu.isuese.detstrat.impl.Node, java.lang.String, java.lang.Double> value) { }
public  com.google.common.collect.Table<org.apache.commons.lang3.tuple.Pair<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node>, java.lang.String, java.lang.Double> getPairDeltas() { return (com.google.common.collect.Table<org.apache.commons.lang3.tuple.Pair<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node>, java.lang.String, java.lang.Double>)null;}
public  void setPairDeltas(com.google.common.collect.Table<org.apache.commons.lang3.tuple.Pair<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node>, java.lang.String, java.lang.Double> value) { }
public  edu.isu.isuese.datamodel.Type getCurrent() { return (edu.isu.isuese.datamodel.Type)null;}
public  void setCurrent(edu.isu.isuese.datamodel.Type value) { }
@java.lang.Override() public  java.util.List<edu.isu.isuese.datamodel.Finding> detect(edu.isu.isuese.datamodel.PatternInstance pattern) { return (java.util.List<edu.isu.isuese.datamodel.Finding>)null;}
protected  com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> constructGraph(edu.isu.isuese.datamodel.Type c) { return (com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship>)null;}
protected  void connectMethodToField(edu.isu.isuese.datamodel.Field x, edu.isu.isuese.detstrat.impl.GraphElementFactory factory, com.google.common.graph.MutableNetwork<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, edu.isu.isuese.detstrat.impl.Node n, boolean indirect) { }
protected  void markMethods(edu.isu.isuese.datamodel.PatternInstance inst) { }
protected  void combinationUtil(edu.isu.isuese.detstrat.impl.Node field, java.util.List<edu.isu.isuese.detstrat.impl.Node> arr, java.util.List<edu.isu.isuese.detstrat.impl.Node> data, int start, int end, int index, com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { }
protected  void constructMethodPairs(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { }
protected  void markMethodPairs(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { }
protected  boolean isInternal(edu.isu.isuese.detstrat.impl.Node m1, edu.isu.isuese.detstrat.impl.Node m2, edu.isu.isuese.detstrat.impl.Node field) { return false;}
protected  boolean isInPairDeltas(edu.isu.isuese.detstrat.impl.Node m1, edu.isu.isuese.detstrat.impl.Node m2) { return false;}
@java.lang.Override() public  void calculateDeltas(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { }
public  void calculateMethodDeltas(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { }
protected  void calculateMethodPairDeltas(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { }
protected  java.lang.Object calculateTCC(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, edu.isu.isuese.datamodel.Method excludedMethod, org.apache.commons.lang3.tuple.Pair<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node> excludedPair) { return null;}
protected  java.lang.Object calculateRCI(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, edu.isu.isuese.datamodel.Method exclude, org.apache.commons.lang3.tuple.Pair<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Node> excludedPair) { return null;}
@java.lang.Override() public  java.util.List<edu.isu.isuese.datamodel.Finding> detectGrime(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { return (java.util.List<edu.isu.isuese.datamodel.Finding>)null;}
protected  java.util.List<edu.isu.isuese.datamodel.Finding> detectPairMethods(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { return (java.util.List<edu.isu.isuese.datamodel.Finding>)null;}
protected  java.util.List<edu.isu.isuese.datamodel.Finding> detectIndividualMethods(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { return (java.util.List<edu.isu.isuese.datamodel.Finding>)null;}
}
