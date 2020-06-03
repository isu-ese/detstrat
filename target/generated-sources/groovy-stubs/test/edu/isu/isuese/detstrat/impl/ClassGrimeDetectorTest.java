package edu.isu.isuese.detstrat.impl;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@org.junit.runner.RunWith(value=junitparams.JUnitParamsRunner.class) public class ClassGrimeDetectorTest
  extends org.javalite.activejdbc.test.DBSpec  implements
    groovy.lang.GroovyObject {
;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  edu.isu.isuese.detstrat.impl.ClassGrimeDetector getFixture() { return (edu.isu.isuese.detstrat.impl.ClassGrimeDetector)null;}
public  void setFixture(edu.isu.isuese.detstrat.impl.ClassGrimeDetector value) { }
public  edu.isu.isuese.datamodel.PatternInstance getInst() { return (edu.isu.isuese.datamodel.PatternInstance)null;}
public  void setInst(edu.isu.isuese.datamodel.PatternInstance value) { }
public  edu.isu.isuese.datamodel.System getSys() { return (edu.isu.isuese.datamodel.System)null;}
public  void setSys(edu.isu.isuese.datamodel.System value) { }
public  edu.isu.isuese.datamodel.Project getProj() { return (edu.isu.isuese.datamodel.Project)null;}
public  void setProj(edu.isu.isuese.datamodel.Project value) { }
public  edu.isu.isuese.datamodel.Namespace getNs1() { return (edu.isu.isuese.datamodel.Namespace)null;}
public  void setNs1(edu.isu.isuese.datamodel.Namespace value) { }
public  edu.isu.isuese.datamodel.Namespace getNs2() { return (edu.isu.isuese.datamodel.Namespace)null;}
public  void setNs2(edu.isu.isuese.datamodel.Namespace value) { }
public  edu.isu.isuese.datamodel.Namespace getNs3() { return (edu.isu.isuese.datamodel.Namespace)null;}
public  void setNs3(edu.isu.isuese.datamodel.Namespace value) { }
public  edu.isu.isuese.datamodel.Namespace getNs4() { return (edu.isu.isuese.datamodel.Namespace)null;}
public  void setNs4(edu.isu.isuese.datamodel.Namespace value) { }
public  edu.isu.isuese.datamodel.Namespace getNs5() { return (edu.isu.isuese.datamodel.Namespace)null;}
public  void setNs5(edu.isu.isuese.datamodel.Namespace value) { }
public  edu.isu.isuese.datamodel.Namespace getNs6() { return (edu.isu.isuese.datamodel.Namespace)null;}
public  void setNs6(edu.isu.isuese.datamodel.Namespace value) { }
public  edu.isu.isuese.datamodel.Type getTypeA() { return (edu.isu.isuese.datamodel.Type)null;}
public  void setTypeA(edu.isu.isuese.datamodel.Type value) { }
public  edu.isu.isuese.datamodel.Type getTypeB() { return (edu.isu.isuese.datamodel.Type)null;}
public  void setTypeB(edu.isu.isuese.datamodel.Type value) { }
public  edu.isu.isuese.datamodel.Type getTypeC() { return (edu.isu.isuese.datamodel.Type)null;}
public  void setTypeC(edu.isu.isuese.datamodel.Type value) { }
public  edu.isu.isuese.datamodel.Type getTypeD() { return (edu.isu.isuese.datamodel.Type)null;}
public  void setTypeD(edu.isu.isuese.datamodel.Type value) { }
public  edu.isu.isuese.datamodel.Type getTypeE() { return (edu.isu.isuese.datamodel.Type)null;}
public  void setTypeE(edu.isu.isuese.datamodel.Type value) { }
public  edu.isu.isuese.datamodel.Type getTypeF() { return (edu.isu.isuese.datamodel.Type)null;}
public  void setTypeF(edu.isu.isuese.datamodel.Type value) { }
public  edu.isu.isuese.datamodel.Type getTypeG() { return (edu.isu.isuese.datamodel.Type)null;}
public  void setTypeG(edu.isu.isuese.datamodel.Type value) { }
public  edu.isu.isuese.datamodel.Type getTypeH() { return (edu.isu.isuese.datamodel.Type)null;}
public  void setTypeH(edu.isu.isuese.datamodel.Type value) { }
public  edu.isu.isuese.datamodel.Type getTypeI() { return (edu.isu.isuese.datamodel.Type)null;}
public  void setTypeI(edu.isu.isuese.datamodel.Type value) { }
public  edu.isu.isuese.datamodel.Field getFld1() { return (edu.isu.isuese.datamodel.Field)null;}
public  void setFld1(edu.isu.isuese.datamodel.Field value) { }
public  edu.isu.isuese.datamodel.Field getFld2() { return (edu.isu.isuese.datamodel.Field)null;}
public  void setFld2(edu.isu.isuese.datamodel.Field value) { }
public  edu.isu.isuese.datamodel.Field getFld3() { return (edu.isu.isuese.datamodel.Field)null;}
public  void setFld3(edu.isu.isuese.datamodel.Field value) { }
public  edu.isu.isuese.datamodel.Method getMeth1() { return (edu.isu.isuese.datamodel.Method)null;}
public  void setMeth1(edu.isu.isuese.datamodel.Method value) { }
public  edu.isu.isuese.datamodel.Method getMeth2() { return (edu.isu.isuese.datamodel.Method)null;}
public  void setMeth2(edu.isu.isuese.datamodel.Method value) { }
public  edu.isu.isuese.datamodel.Method getMeth3() { return (edu.isu.isuese.datamodel.Method)null;}
public  void setMeth3(edu.isu.isuese.datamodel.Method value) { }
public  edu.isu.isuese.datamodel.Method getMeth4() { return (edu.isu.isuese.datamodel.Method)null;}
public  void setMeth4(edu.isu.isuese.datamodel.Method value) { }
public  edu.isu.isuese.datamodel.Method getGetter() { return (edu.isu.isuese.datamodel.Method)null;}
public  void setGetter(edu.isu.isuese.datamodel.Method value) { }
public  edu.isu.isuese.detstrat.impl.Node getA1() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setA1(edu.isu.isuese.detstrat.impl.Node value) { }
public  edu.isu.isuese.detstrat.impl.Node getA2() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setA2(edu.isu.isuese.detstrat.impl.Node value) { }
public  edu.isu.isuese.detstrat.impl.Node getA3() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setA3(edu.isu.isuese.detstrat.impl.Node value) { }
public  edu.isu.isuese.detstrat.impl.Node getM1() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setM1(edu.isu.isuese.detstrat.impl.Node value) { }
public  edu.isu.isuese.detstrat.impl.Node getM2() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setM2(edu.isu.isuese.detstrat.impl.Node value) { }
public  edu.isu.isuese.detstrat.impl.Node getM3() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setM3(edu.isu.isuese.detstrat.impl.Node value) { }
public  edu.isu.isuese.detstrat.impl.Node getM4() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setM4(edu.isu.isuese.detstrat.impl.Node value) { }
public  edu.isu.isuese.detstrat.impl.Node getGet() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setGet(edu.isu.isuese.detstrat.impl.Node value) { }
public  com.google.common.graph.MutableNetwork<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> getGraph() { return (com.google.common.graph.MutableNetwork<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship>)null;}
public  void setGraph(com.google.common.graph.MutableNetwork<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> value) { }
@org.junit.Before() public  void setup() { }
@org.junit.Test() public  void detect() { }
@org.junit.Test() public  void detectGrime() { }
@org.junit.Test() public  void detectPairMethods() { }
@org.junit.Test() public  void detectIndividualMethods() { }
public  void createModelComponents() { }
}
