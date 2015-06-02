
#1) INSTALL NODE

Install ARM version of Node.js on raspberry pi with:

* wget http://node-arm.herokuapp.com/node_latest_armhf.deb 
* sudo dpkg -i node_latest_armhf.deb

Test installation with:
* node -v
* npm -v
Both should return a version number.



#2) INSTALL DEPENDENCIES

Navigate to the folder with the node server:

* cd /android_lab_assignment4/plugserver/

Run:
* npm install




#3) RUN SERVER

* sudo node plugserver.js

we need sudo because of GPIO access, which required by rspimodulator native module
