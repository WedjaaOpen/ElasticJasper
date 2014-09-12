ElasticJasper
=============

This is an Eclipse Project to create a plugin for Jaspersoft Studio that allows to create 
reports with data from an ElasticSearch cluster. This plugin has been used internally to 
allow some of our clients to migrate to ElasticSearch without loosing the reporting
capabilities of Jasper Reports and their integration with Jaspersoft Server.

If you want to take the plugin for a spin you can install it without compiling it in 
Jaspersoft Studio 5.6.0 and later versions by adding the update site for this plugin:

    https://github.com/WedjaaOpen/ElasticJasperSite/raw/master/

Instructions on how to use the plugin are coming and will be published as soon as we are
done releasing all the sources to compile the plugin and the server side adapter.

The adapter - which will be compiled in **lib/es-adapter.jar** can be used directly
from a Java application that uses Jasper Reports.

How To Compile
--------------

Once you have cloned this project and opened it in Eclipse you will need another couple
of pieces in your workspace.

From an installation of Jaspersoft Studio you will need to import as plug-in/fragments
the following jars:

  - com.jaspersoft.studio.data_5.6.*x*.final.jar
  - com.jaspersoft.studio_5.6.*x*.final.jar
  - net.sf.jasperreports_5.6.*x*.final.jar

where *x* stands for whatever minor version your installation is.

Then you should follow the instructions on the 
[ElasticSearchOSGI Repository][https://github.com/WedjaaOpen/ElasticSearchOSGI] to get 
yourself a *org.elasticsearch.osgi-1.1.2-bundle.jar* that you also need to import into
your eclipse workspace.

Once you have all this pieces you're ready to compile the plugin and then you need
to create a *feature* and an *update site* in Eclipse that will allow you to install
the plugin in Jaspersoft Studio.

The source contains a -ESSearchTester.java- runnable piece. This is to test the
inner workings of the plugin and to test if things you have changed have broken
something.

Support
-------

This software is released *as is*. We make no claim that it will do anything useful and
it may potentially do harm to your self confidence. We will however keep an eye on the
issues you open on GitHub and try an fix whatever it's broken.

We do offer professional services and support in case you need.
