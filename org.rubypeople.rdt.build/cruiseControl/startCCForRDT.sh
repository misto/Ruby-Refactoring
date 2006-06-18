#! /bin/sh
Xvfb :1 &
export DISPLAY=localhost:1
#cd ~/cruisecontrol-bin-2.5
sh ../cruisecontrol-bin-2.5/cruisecontrol.sh -webport 8090 $@
