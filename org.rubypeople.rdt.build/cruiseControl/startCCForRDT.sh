#!/bin/bash
Xvfb :1 &
export DISPLAY=localhost:1
~/cruisecontrol/cruisecontrol.sh -webport 8080 -jmxport 8000 $@
