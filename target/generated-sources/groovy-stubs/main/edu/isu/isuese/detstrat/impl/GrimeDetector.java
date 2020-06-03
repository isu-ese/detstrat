package edu.isu.isuese.detstrat.impl;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

public interface GrimeDetector
 {
;
 java.util.List<edu.isu.isuese.datamodel.Finding> detect(edu.isu.isuese.datamodel.PatternInstance pattern);
 void calculateDeltas(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph);
 java.util.List<edu.isu.isuese.datamodel.Finding> detectGrime(com.google.common.graph.Network<edu.isu.isuese.detstrat.impl.Node, edu.isu.isuese.detstrat.impl.Relationship> graph);
}
