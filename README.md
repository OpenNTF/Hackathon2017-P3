# Social Notifier: Send To Watson Workspace

### Brief Description

Domino DDM sends an alert to Watson Workspace, which triggers subsequent notifications, including to Slack, and to other platforms as desired, including Twitter etc.

### Actors

Notifications won’t be user generated, these will be automated based on time interval DDM lookups.

### Preconditions

Project has to be configured in the IBM Domino and Watson environments:

##### Watson Workspace Configuration

- Create an account on Watson Workspace
- Create a Workspace for the team
    - Find out the '**Space id**' which is the part of the URL when your are in your space: 
      - `https://workspace.ibm.com/space/<space-id>`
- Create an Application on Watson Workspace
    - Go to https://developer.watsonwork.ibm.com/apps/
    - Click '*Create new app*' 
    - Note your '**App ID**' and '**App secret**'  and keep them secure.
- Add the new app into your space.
    - Go to your space settings
    - In Apps, you will see your newly created application.
    - Click '*Add to space*'.


##### IBM Domino environment configuration:

- Create a new NSF configuration database for every different workspace.
  - Use [NTF template](_deploy/snotifier.ntf) provided.
  - Open the new database created and click '*Create\config*' to create the configuration
  - Fill in the fields for '*Space Id*', '*AppId*' and '*AppKey*' from the previous steps.
  - Save and exit
- Add notes.ini setting for configuration document(s)
  - On Domino server, add a new notes.ini parameter named `DT_Databases` listing configuration database file names.
  - Multiple databases can be listed using delimiter `;` 
    - e.g. DT_Databases=tools\team1alert.nsf;tools\team2alert.nsf
- Add Plugin to your Domino server
  - Make sure you have IBM Domino 9.0.x environment with OpenSocial Component installed (needed for DOTS)
  - Download the recent JAR file [from the '_deploy' folder](_deploy/) and save it into the following folder on your Domino server;
    - *{Domino Data Folder}*\domino\workspace-dots\applications\eclipse\plugins
  - If DOTS is not running, add DOTS to `ServerTasks` parameter in the notes.ini file. 


##### Usage

Now you can use event handlers on Monitoring Configurations

- Select '**Log to a database**' as a notification method
- Database file name will be the NSF file names you defined in the previous steps.








####OPENNTF
    This project is an OpenNTF project, and is available under the Apache License V2.0.  
    All other aspects of the project, including contributions, defect reports, discussions, 
    feature requests and reviews are subject to the OpenNTF Terms of Use - available at 
    http://openntf.org/Internal/home.nsf/dx/Terms_of_Use.