package edu.isu.isuese.detstrat;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

public interface Detector
 {
;
 java.util.List<edu.isu.isuese.datamodel.Finding> detect(edu.isu.isuese.datamodel.Project project);
}
