Election SMS
============

Web application sending bulk SMS to a group of election observers, via Magti web services.

### Fill up application.propertie with Magti WS details
Before packaging and deploying the app, you must specify Magti's webservice details:

1. Open /src/main/resources/application.properties
2. For each line, replace \*\*** with the appropriate information Magti provided to you.

### How to package the application

Once you have cloned the application onto your machine, go throught the following steps:

1. Make sure you have Maven install on your machine.
Type the following command to check if it is already installed:
`mvn --version`. It should display something like
`Apache Maven X.X.X`.
If it is not installed, please follow instructions here: [http://maven.apache.org/download.cgi ](http://maven.apache.org/download.cgi) 
2. Go to the project root.
3. Type the following command:
`mvn clean package`.
This will create a WAR file in ./target folder. We can now copy this file onto Tomcat

### How to deploy the application in Tomcat
1. Make sure Tomcat is set up on your server (if you're using Ubuntu 13.04 or newer, you'll find the info here: [https://help.ubuntu.com/13.04/serverguide/tomcat.html](https://help.ubuntu.com/13.04/serverguide/tomcat.html). Also, make sure you create a private instance.
2. Before copying the WAR file, shutdown your Tomcat instance.
3. Copy the newly created WAR file in Tomcat, in the 'webapps' folder.
4. Restart Tomcat. After it finishes to restart, you'll be able to reach the home page at [http://localhost:8080/electionsms](http://localhost:8080/electionsms)

### Get a list of observers
1. Before sending messages, you will need the list of observers, that are supposed to receive SMS.
The list should be a CSV file, having the following format:
`name,phone number,group`.
2. Once you have a CSV file, make sure that its name is "PhoneNumberList.csv", and upload/copy it to the /tmp folder of the machine you just deployed the app on.
3. You will now see the observers in [http://localhost:8080/electionsms/showlist](http://localhost:8080/electionsms/showlist).
