Election SMS
============

Web application sending bulk SMS to a group of election observers, via Magti web services.

Prerequisites
-------------

### Fill up application.properties with Magti WS details
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

How to configure your recipient lists and send messages
-------------------------------------------------------

With ELection SMS, you can send messages to 2 types of groups: Parliament-related groups of people, and Election-related groups of people. Before being able to send messages, you need to define these lists.

### Get a list of observers
1. Go to the "Show Recipients" page. (link available on the left hand side, once you logged in)
2. From this screen, you can upload a list of contact, one group type at a time.
The list should be a CSV file, having the following format:
`name,language,phone number,group`.
Select the file, choose the group type (Parliament or Election), and upload the list.
3. Once the upload is finished, you will see the contacts being rendered in a table on that same page, below the upload section.

### Send a message.
1. Go to the "Send message" page.
2. Write the message you want to send (160 characters max.)
3. Select the language you're using in your message. This will filter the recipients that are tied to that language.
4. Choose the groups of people you want to the send the message to. This will reduce even more the number of recipients. 
5. Send message.
6. After you receive the confirmation message (top of the page), go to the "Logging page", in order to see the status of your sending. 
If you've received an error message, you will see, on the logging page, the name and phone number of the people who didn't receive the message.
