package edu.isu.isuese.detstrat.impl;

import edu.isu.isuese.datamodel.*;
import edu.montana.gsoc.msusel.rbml.model.*;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@org.junit.runner.RunWith(value=junitparams.JUnitParamsRunner.class) public class ModularGrimeDetectorTest
  extends org.javalite.activejdbc.test.DBSpec  implements
    groovy.lang.GroovyObject {
;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  edu.isu.isuese.detstrat.impl.ModularGrimeDetector getFixture() { return (edu.isu.isuese.detstrat.impl.ModularGrimeDetector)null;}
public  void setFixture(edu.isu.isuese.detstrat.impl.ModularGrimeDetector value) { }
public  edu.isu.isuese.detstrat.impl.Node getA() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setA(edu.isu.isuese.detstrat.impl.Node value) { }
public  edu.isu.isuese.detstrat.impl.Node getB() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setB(edu.isu.isuese.detstrat.impl.Node value) { }
public  edu.isu.isuese.detstrat.impl.Node getC() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setC(edu.isu.isuese.detstrat.impl.Node value) { }
public  edu.isu.isuese.detstrat.impl.Node getD() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setD(edu.isu.isuese.detstrat.impl.Node value) { }
public  edu.isu.isuese.detstrat.impl.Node getE() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setE(edu.isu.isuese.detstrat.impl.Node value) { }
public  edu.isu.isuese.detstrat.impl.Node getF() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setF(edu.isu.isuese.detstrat.impl.Node value) { }
public  edu.isu.isuese.detstrat.impl.Node getG() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setG(edu.isu.isuese.detstrat.impl.Node value) { }
public  edu.isu.isuese.detstrat.impl.Node getH() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setH(edu.isu.isuese.detstrat.impl.Node value) { }
public  edu.isu.isuese.detstrat.impl.Node getI() { return (edu.isu.isuese.detstrat.impl.Node)null;}
public  void setI(edu.isu.isuese.detstrat.impl.Node value) { }
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
public  edu.isu.isuese.datamodel.PatternInstance getInst() { return (edu.isu.isuese.datamodel.PatternInstance)null;}
public  void setInst(edu.isu.isuese.datamodel.PatternInstance value) { }
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
@org.junit.Before() public  void setup() { }
@org.junit.After() public  void cleanup() { }
@org.junit.Test() @junitparams.Parameters(value={"true,  true,  true,  true,  0, 0, isuese:grime:modular-grime:pig","true,  true,  true,  false, 0, 0, isuese:grime:modular-grime:tig","false, true,  true,  true,  1, 0, isuese:grime:modular-grime:peag","true,  false, true,  true,  0, 1, isuese:grime:modular-grime:peeg","false, true,  true,  false, 1, 0, isuese:grime:modular-grime:teag","true,  false, true,  false, 0, 1, isuese:grime:modular-grime:teeg","true,  false, true,  true,  1, 0, ","false, true,  true,  true,  0, 1, ","true,  false, true,  false, 1, 0, ","false, true,  true,  false, 0, 1, ","false, false, true,  true,  1, 0, ","false, false, true,  true,  0, 1, ","false, false, true,  false, 1, 0, ","false, false, true,  false, 0, 1, ","false, false, false, true,  1, 0, ","false, false, false, true,  0, 1, ","false, false, false, false, 1, 0, ","false, false, false, false, 0, 1, "}) public  void detectGrime(boolean srcInternal, boolean destInternal, boolean invalid, boolean persistent, int ca, int ce, java.lang.String key) { }
@org.junit.Test() @junitparams.Parameters() public  void relationMatch(edu.montana.gsoc.msusel.rbml.model.Relationship rel, edu.isu.isuese.detstrat.impl.Relationship edge, boolean expected) { }
public  void createModelComponents() { }
}
