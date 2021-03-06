#Installation manual for iutools-apps

## Requirements

First, make sure you have installed the following requirements:

- Java JDK 1.8
- Tomcat 

In what remains, `[catalina_home]` refers to the path of the root of the Tomcat installation

## Create the configuration files (`ca_nrc.properties` and `log4j.properties`)

Configuraiton of _iutools-apps` is done through two files:
- `ca_nrc.properties`: Configuration properties for _iutools-apps_ proper.
- `log4j.properties`: Configuration properties for _log4j_ logging.

We will provide more details in subsequent sections about what to put in those two files, but 
for now, just create those two files in locations for which Tomcat has read-access.

Next, create (or edit) file `[catalina_home]/bin/setenv.sh` and put the following lines in it:

In `setenv.sh`, aßdd the following line:

    CATALINA_OPTS="-Dlog4j.configuration=file:/path/to/your/log4j.properties -Dca_nrc=/path/to/your/ca_nrc.properties  -Xss515m"

## Edit the _iutools-apps_ configuration properties (`ca_nrc.properties` file)

The `ca_nrc.properties` file supports the following configuration properties:

- `ca.nrc.iutools.datapath`: Path to the root of your `iutools-data` project.
- `ca.nrc.javautils.bingKey` (OPTIONAL): For the Inuktut Search Engine to work, 
     you need to set this to a valid Microsoft Azure Cognitive Service Bing key.
 
##Install the corpora files

IUTools requires a number of large corpora files. Because of their size, those 
files cannot be put under Git. Instead, we put them on cyclosa.web.net, under 
the path:

    /var/iutools/data/data
    
All the files contained in that directory must be transfered to your 
installation machine under iutoools-data/data. As of May 2020, this directory 
contains the following files and directories:
- LanguageData		
- compiled-corpuses	
- tries
- NunHanSearch		
- numericTermsCorpus*.json
     
## Deploying the app

To deploy (or redeploy) the apps, open a Terminal window and type the following commands

     rm -r [catalina_home]/webapps/iutools
     cp iutools-apps-N.N.N-SNAPSHOT.war [catalina_home]/webapps/iutools.war
          # Where N.N.N is the version number
     sh [catalina_home]/shutdown.sh # May cause an error if Tomcat not alredy running
     sh [catalina_home]/startup.sh

__FOR DEVELOPERS__

If you have made some changes to the JS files and want to redploy them, you can use a faster
  method:
  
     rm -r [catalina_home]/webapps/iutools/*
     cp -pr [webapp_sources]/* [catalina_home]/webapps/.
     
Where `[webapps_sources]` is the root of the `webapps` directory in your dev project.

Note that you may need to reload the HTML pages for changes to take effect.  

## Testing the app

- Open your browser at http://localhost:8080/iutools/search.html
- Enter a query term in syllabic or latin Inuktut, then hit _Search_
     

