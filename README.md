ElasticJasper
=============

This is an Eclipse Project to create a plugin for Jaspersoft Studio that allows to create 
reports with data from an ElasticSearch cluster. This plugin has been used internally to 
allow some of our clients to migrate to ElasticSearch without loosing the reporting
capabilities of Jasper Reports and their integration with Jaspersoft Server.

If you want to take the plugin for a spin you can install it without compiling it in 
Jaspersoft Studio 6.0.0 and later versions by adding the update site for this plugin:

    https://github.com/WedjaaOpen/ElasticJasperSite/raw/master/

Instructions on how to use the plugin can be found on our [Official Blog](http://blog.wedjaa.net/elasticjasper-one/ "Wedjaa - The Blog").

The adapter can be used directly from a Java application that uses Jasper Reports.

How To Compile
--------------

Once you have cloned this project and opened it in Eclipse you will need another couple
of pieces in your workspace.

From an installation of Jaspersoft Studio you will need to import as plug-in/fragments
the following jars:

  - com.jaspersoft.studio.data_6.0.*x*.final.jar
  - com.jaspersoft.studio_6.0.*x*.final.jar
  - net.sf.jasperreports_6.0.*x*.final.jar

where *x* stands for whatever minor version your installation is.

Then you should follow the instructions on the 
[ElasticSearchOSGI Repository](https://github.com/WedjaaOpen/ElasticSearchOSGI "ElasticSearchOSGI Repository") to get 
yourself a *org.elasticsearch.osgi-1.4.2-bundle.jar* that you also need to import into
your eclipse workspace.

Once you have all this pieces you're ready to compile the plugin and then you need
to create a *feature* and an *update site* in Eclipse that will allow you to install
the plugin in Jaspersoft Studio.

Support
-------

This software is released *as is*. We make no claim that it will do anything useful and
it may potentially do harm to your self confidence. We will however keep an eye on the
issues you open on GitHub and try an fix whatever it's broken.

We do offer professional services and support in case you need.
