package edu.isu.isuese.detstrat.impl;

import edu.isu.isuese.datamodel.*;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

public class OrgGrimeDetector
  extends edu.isu.isuese.detstrat.impl.AbstractGrimeDetector {
;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  com.google.common.graph.MutableNetwork<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.NamespaceRelation> getNsGraph() { return (com.google.common.graph.MutableNetwork<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.NamespaceRelation>)null;}
public  void setNsGraph(com.google.common.graph.MutableNetwork<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.NamespaceRelation> value) { }
public  com.google.common.graph.MutableNetwork<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> getTypeGraph() { return (com.google.common.graph.MutableNetwork<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship>)null;}
public  void setTypeGraph(com.google.common.graph.MutableNetwork<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> value) { }
public  com.google.common.collect.BiMap<edu.isu.isuese.datamodel.Type, edu.isu.isuese.detstrat.impl.Node> getTypeBiMap() { return (com.google.common.collect.BiMap<edu.isu.isuese.datamodel.Type, edu.isu.isuese.detstrat.impl.Node>)null;}
public  void setTypeBiMap(com.google.common.collect.BiMap<edu.isu.isuese.datamodel.Type, edu.isu.isuese.detstrat.impl.Node> value) { }
public  com.google.common.collect.BiMap<edu.isu.isuese.datamodel.Namespace, edu.isu.isuese.detstrat.impl.Node> getNsBiMap() { return (com.google.common.collect.BiMap<edu.isu.isuese.datamodel.Namespace, edu.isu.isuese.detstrat.impl.Node>)null;}
public  void setNsBiMap(com.google.common.collect.BiMap<edu.isu.isuese.datamodel.Namespace, edu.isu.isuese.detstrat.impl.Node> value) { }
public  java.util.Map<edu.isu.isuese.detstrat.impl.Node, java.util.List<edu.isu.isuese.detstrat.impl.Node>> getNsTypeMap() { return (java.util.Map<edu.isu.isuese.detstrat.impl.Node, java.util.List<edu.isu.isuese.detstrat.impl.Node>>)null;}
public  void setNsTypeMap(java.util.Map<edu.isu.isuese.detstrat.impl.Node, java.util.List<edu.isu.isuese.detstrat.impl.Node>> value) { }
public  com.google.common.collect.Table<edu.isu.isuese.detstrat.impl.Node, java.lang.String, java.lang.Double> getNsMetrics() { return (com.google.common.collect.Table<edu.isu.isuese.detstrat.impl.Node, java.lang.String, java.lang.Double>)null;}
public  void setNsMetrics(com.google.common.collect.Table<edu.isu.isuese.detstrat.impl.Node, java.lang.String, java.lang.Double> value) { }
public  com.google.common.collect.Table<edu.isu.isuese.detstrat.impl.Node, java.lang.String, java.lang.Double> getTypeMetrics() { return (com.google.common.collect.Table<edu.isu.isuese.detstrat.impl.Node, java.lang.String, java.lang.Double>)null;}
public  void setTypeMetrics(com.google.common.collect.Table<edu.isu.isuese.detstrat.impl.Node, java.lang.String, java.lang.Double> value) { }
@java.lang.Override() public  java.util.List<edu.isu.isuese.datamodel.Finding> detect(edu.isu.isuese.datamodel.PatternInstance pattern) { return (java.util.List<edu.isu.isuese.datamodel.Finding>)null;}
public  void mark(edu.isu.isuese.datamodel.PatternInstance p) { }
protected  void markNamespaces(java.util.List<edu.isu.isuese.datamodel.Namespace> list) { }
protected  void markTypes(edu.isu.isuese.datamodel.PatternInstance p) { }
@java.lang.Override() public  void calculateDeltas(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { }
@java.lang.Override() public  java.util.List<edu.isu.isuese.datamodel.Finding> detectGrime(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { return (java.util.List<edu.isu.isuese.datamodel.Finding>)null;}
public  edu.isu.isuese.datamodel.Finding createFinding(java.lang.String name, edu.isu.isuese.detstrat.impl.NamespaceRelation rel, com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.NamespaceRelation> graph) { return (edu.isu.isuese.datamodel.Finding)null;}
public  edu.isu.isuese.datamodel.Finding createFinding(java.lang.String name, edu.isu.isuese.detstrat.impl.Node node) { return (edu.isu.isuese.datamodel.Finding)null;}
public  java.lang.Object findPatternNamespaces(edu.isu.isuese.datamodel.PatternInstance p) { return null;}
public  java.lang.Object cycle(edu.isu.isuese.detstrat.impl.Node src, edu.isu.isuese.detstrat.impl.Node dest, edu.isu.isuese.detstrat.impl.NamespaceRelation rel) { return null;}
public  java.lang.Object measureCycleQuality(edu.isu.isuese.detstrat.impl.Node ns, edu.isu.isuese.detstrat.impl.NamespaceRelation rel) { return null;}
public  java.lang.Object dropInstability(edu.isu.isuese.detstrat.impl.NamespaceRelation rel) { return null;}
public  java.lang.Object measureCohesionQ() { return null;}
public  java.lang.Object cohesionQ(edu.isu.isuese.detstrat.impl.Node p, edu.isu.isuese.detstrat.impl.Node t) { return null;}
protected  double findPD(edu.isu.isuese.detstrat.impl.Node p, edu.isu.isuese.detstrat.impl.Node t, java.util.Set<edu.isu.isuese.detstrat.impl.Relationship> pintD) { return (double)0;}
protected  java.util.Set<edu.isu.isuese.detstrat.impl.Relationship> findPExtDSet(java.util.Set<edu.isu.isuese.detstrat.impl.NamespaceRelation> edges, edu.isu.isuese.detstrat.impl.Node t) { return (java.util.Set<edu.isu.isuese.detstrat.impl.Relationship>)null;}
protected  java.util.HashSet<edu.isu.isuese.detstrat.impl.Relationship> findPIntDSet(edu.isu.isuese.detstrat.impl.Node p, edu.isu.isuese.detstrat.impl.Node t) { return (java.util.HashSet<edu.isu.isuese.detstrat.impl.Relationship>)null;}
public  java.lang.Object measureCouplingQ() { return null;}
public  java.lang.Object couplingQ(edu.isu.isuese.detstrat.impl.Node p, edu.isu.isuese.detstrat.impl.Node t) { return null;}
protected  java.util.Set<edu.isu.isuese.detstrat.impl.Node> getPproP(edu.isu.isuese.detstrat.impl.Node p, edu.isu.isuese.detstrat.impl.Node t) { return (java.util.Set<edu.isu.isuese.detstrat.impl.Node>)null;}
protected  java.util.Set<edu.isu.isuese.detstrat.impl.Node> getPcliP(edu.isu.isuese.detstrat.impl.Node p, edu.isu.isuese.detstrat.impl.Node t) { return (java.util.Set<edu.isu.isuese.detstrat.impl.Node>)null;}
public  void constructGraphs(edu.isu.isuese.datamodel.PatternInstance inst) { }
protected  void addNodes(java.util.List<edu.isu.isuese.datamodel.Namespace> namespaces, edu.isu.isuese.detstrat.impl.GraphElementFactory factory) { }
protected  java.lang.Object createGraphs() { return null;}
protected <T> com.google.common.graph.MutableNetwork<edu.isu.isuese.detstrat.impl.Node, T> initializeGraph() { return (com.google.common.graph.MutableNetwork<edu.isu.isuese.detstrat.impl.Node, T>)null;}
protected  java.lang.Object addEdges(edu.isu.isuese.datamodel.Type type, edu.isu.isuese.detstrat.impl.Node node, java.util.Set<edu.isu.isuese.datamodel.Type> types, edu.isu.isuese.detstrat.impl.RelationshipType relType, edu.isu.isuese.detstrat.impl.GraphElementFactory factory) { return null;}
protected  boolean isPersistent(edu.isu.isuese.detstrat.impl.RelationshipType type) { return false;}
public  java.lang.Object measureI() { return null;}
public  java.lang.Object measureIWithoutRelation(edu.isu.isuese.detstrat.impl.Node p, edu.isu.isuese.detstrat.impl.NamespaceRelation rel) { return null;}
public  java.lang.Object measureCa() { return null;}
public  java.lang.Object measureCaWithoutRelation(edu.isu.isuese.detstrat.impl.Node p, edu.isu.isuese.detstrat.impl.NamespaceRelation rel) { return null;}
public  java.lang.Object measureCe() { return null;}
public  java.lang.Object measureCeWithoutRelation(edu.isu.isuese.detstrat.impl.Node p, edu.isu.isuese.detstrat.impl.NamespaceRelation rel) { return null;}
public  java.lang.Object measureA() { return null;}
public  java.lang.Object measureNa() { return null;}
public  java.lang.Object measureNc() { return null;}
protected  void measureD() { }
protected  double measureDWithoutRelation(edu.isu.isuese.detstrat.impl.Node ns, edu.isu.isuese.detstrat.impl.NamespaceRelation rel) { return (double)0;}
public  java.lang.Object markCycles() { return null;}
}
