package edu.isu.isuese.detstrat.impl;

import com.google.common.collect.*;
import edu.isu.isuese.datamodel.*;
import edu.montana.gsoc.msusel.rbml.model.*;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

public class ModularGrimeDetector
  extends edu.isu.isuese.detstrat.impl.AbstractGrimeDetector {
;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  com.google.common.collect.Table<edu.isu.isuese.detstrat.impl.Relationship, java.lang.String, java.lang.Integer> getMetrics() { return (com.google.common.collect.Table<edu.isu.isuese.detstrat.impl.Relationship, java.lang.String, java.lang.Integer>)null;}
public  void setMetrics(com.google.common.collect.Table<edu.isu.isuese.detstrat.impl.Relationship, java.lang.String, java.lang.Integer> value) { }
public  com.google.common.collect.BiMap<edu.isu.isuese.datamodel.Type, edu.isu.isuese.detstrat.impl.Node> getNodes() { return (com.google.common.collect.BiMap<edu.isu.isuese.datamodel.Type, edu.isu.isuese.detstrat.impl.Node>)null;}
public  void setNodes(com.google.common.collect.BiMap<edu.isu.isuese.datamodel.Type, edu.isu.isuese.detstrat.impl.Node> value) { }
@java.lang.Override() public  java.util.List<edu.isu.isuese.datamodel.Finding> detect(edu.isu.isuese.datamodel.PatternInstance pattern) { return (java.util.List<edu.isu.isuese.datamodel.Finding>)null;}
protected  com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> constructPatternGraph(edu.isu.isuese.datamodel.PatternInstance pattern) { return (com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship>)null;}
protected  void handleCreatingRelationships(edu.isu.isuese.datamodel.Type towards, edu.isu.isuese.datamodel.Type type, com.google.common.graph.MutableNetwork<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, edu.isu.isuese.detstrat.impl.RelationshipType reltype) { }
protected  void createRelationship(edu.isu.isuese.datamodel.Type towards, edu.isu.isuese.datamodel.Type type, com.google.common.graph.MutableNetwork<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, edu.isu.isuese.detstrat.impl.RelationshipType relationshipType) { }
protected  void createNodeAndRelationship(edu.isu.isuese.datamodel.Type towards, edu.isu.isuese.datamodel.Type type, com.google.common.graph.MutableNetwork<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, edu.isu.isuese.detstrat.impl.RelationshipType relationshipType) { }
protected  void markInternalOrExternal(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, edu.isu.isuese.datamodel.PatternInstance pattern) { }
protected  void markTemporaryOrPersistent(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { }
protected  void markValidOrInvalid(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, edu.isu.isuese.datamodel.PatternInstance pattern) { }
protected  java.util.Map<edu.montana.gsoc.msusel.rbml.model.Role, java.util.List<edu.isu.isuese.datamodel.Type>> createBindingsMap(edu.montana.gsoc.msusel.rbml.model.SPS sps, edu.isu.isuese.datamodel.PatternInstance pattern) { return (java.util.Map<edu.montana.gsoc.msusel.rbml.model.Role, java.util.List<edu.isu.isuese.datamodel.Type>>)null;}
protected  void validateRoleMatchingRelationships(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, edu.montana.gsoc.msusel.rbml.model.SPS sps, java.util.Map<edu.montana.gsoc.msusel.rbml.model.Role, java.util.List<edu.isu.isuese.datamodel.Type>> bindings) { }
protected  void markValidOrInvalidGenHierRelationships(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, edu.montana.gsoc.msusel.rbml.model.SPS sps, java.util.Map<edu.montana.gsoc.msusel.rbml.model.Role, java.util.List<edu.isu.isuese.datamodel.Type>> bindings) { }
protected  void validateCorrectInheritanceRelations(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, java.util.List<edu.isu.isuese.datamodel.Type> superTypes, java.util.List<edu.isu.isuese.datamodel.Type> subTypes) { }
protected  void invalidateSuperToSubtypeRelations(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, java.util.List<edu.isu.isuese.datamodel.Type> superTypes, java.util.List<edu.isu.isuese.datamodel.Type> subTypes) { }
protected  void invalidateUnmarkedIncomingInheritanceRelations(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, java.util.List<edu.isu.isuese.datamodel.Type> types) { }
protected  void invalidateUnmarkedIncomingRelations(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, java.util.List<edu.isu.isuese.datamodel.Type> types) { }
protected  boolean relationMatch(edu.montana.gsoc.msusel.rbml.model.Relationship rel, edu.isu.isuese.detstrat.impl.Relationship edge) { return false;}
protected  boolean validateRemainingUnMarkedEdges(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { return false;}
@java.lang.Override() public  void calculateDeltas(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { }
protected  int afferentCoupling(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, edu.isu.isuese.detstrat.impl.Relationship rel) { return (int)0;}
protected  int efferentCoupling(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph, edu.isu.isuese.detstrat.impl.Relationship rel) { return (int)0;}
@java.lang.Override() public  java.util.List<edu.isu.isuese.datamodel.Finding> detectGrime(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph) { return (java.util.List<edu.isu.isuese.datamodel.Finding>)null;}
}
