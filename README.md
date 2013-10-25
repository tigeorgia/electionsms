Election SMS
============

Web application sending bulk SMS to a group of election observers, via Magti web services.

#### How to package the application

Once you have cloned the application onto your machine, go throught the following steps:
1. Make sure you have Maven install on your machine.
⋅⋅* Type the following command to check if it is already installed:
`mvn --version`
if should display something like
`Apache Maven X.X.X`
⋅⋅* If it is not installed, please follow instructions here: http://maven.apache.org/download.cgi 
2. Go to the project root.
3. Type the following command:
`mvn clean package`
This will create a WAR file in ./target folder. We can now copy this file onto Tomcat

### How to deploy the application in Tomcat
4. Make sure Tomcat is set up on your server (if you're using Ubuntu, you'll find the info here: https://help.ubuntu.com/13.04/serverguide/tomcat.html). Also, make sure you create a private instance.
5. Before copying the WAR file, shutdown your Tomcat instance.
6. Copy the newly created WAR file in Tomcat, in the 'webapps' folder.
7. Restart Tomcat. After it finishes to restart, you'll be able to reach the home page at http://localhost:8080/electionsms

