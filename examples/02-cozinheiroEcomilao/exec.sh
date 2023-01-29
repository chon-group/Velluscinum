#! /bin/bash
zip -d lib/jason-bigchaindb-driver.jar 'META-INF/*DSA' 'META-INF/*SF'
/usr/bin/java -classpath ../../lib/jason-3.1/libs/ant-launcher-1.10.5.jar org.apache.tools.ant.launch.Launcher -e -f bin/build.xml run