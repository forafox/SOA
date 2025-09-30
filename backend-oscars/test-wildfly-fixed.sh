#!/bin/bash

# Test script for WildFly with fixed configuration
echo "Testing WildFly configuration..."

# Check if WildFly is already running
if pgrep -f wildfly > /dev/null; then
    echo "WildFly is already running. Stopping it first..."
    pkill -f wildfly
    sleep 3
fi

# Set environment variables
export JBOSS_HOME=/home/studs/s367268/wildfly-37.0.1.Final
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk

# Navigate to WildFly bin directory
cd $JBOSS_HOME/bin

# Start WildFly with the fixed configuration
echo "Starting WildFly with standalone-oscars-minimal.xml..."
./standalone.sh -c standalone-oscars-minimal.xml -b 0.0.0.0 -bmanagement 0.0.0.0

echo "WildFly startup completed."
