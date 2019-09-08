#!/bin/sh

nohup java -Xmx4g -Xserver -cp ./classes:./lib/*:$JAVA_HOME/lib/tools.jar org.dodo.benchmark.server.BenchmarkServer > /dev/null 2>&1  &
