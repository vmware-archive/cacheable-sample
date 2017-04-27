# Runtime Configurable Region Names for Spring Data GemFire @Cacheable Support #

This project demonstrates an approach for configuring the names of the GemFire
regions that back the @Cacheable annotation at runtime.

# Walk Through #
- Start a GemFire 8.2.x cluster.  The _local-cluster_ directory contains Python
scripts for starting a local cluster.  Instructions for using these can be found
in the "Local Cluster Instructions" section below.  However, any GemFire 8.2
cluster can be used, regardless of how it is started.
- Create a region called "dev_Rates" using gfsh:
```
gfsh
gfsh> connect --locator=host[port]
gfsh> create region --name=dev_Rates --type=PARTITION_REDUNDANT
```
- Build the project: `mvn package`
- Review the source file: _src/main/java/io/pivotal/pde/sample/cacheable/DummyExchangeRageProvider.java_
Note the @Cacheable annotation.
```java
@Cacheable(cacheNames="Rates", key="#from.concat('|').concat(#to)")
public double getExchangeRate(String from, String to){
```
- Review the configuration file: _src/main/resources/context.xml_. Note the
alternative cache manager, _PrefixGemFireCacheManager_ and the _prefix_
parameter, which is contains a property placeholder. This cache manager
implementation will map the cache name, _MyCache_ to the GemFire region
_prefixMyCache_ where the actual prefix is provided at startup time through the
usual Spring property placeholder mechanism.  Note that this allows the same
code, and in particular, the same @Cacheable method to target different GemFire
regions based on
```xml
<bean id="cacheManager" class="io.pivotal.pde.sample.cacheable.PrefixGemFireCacheManager">
  <property name="prefix" value="${gemfire.cache.prefix}" />
</bean>
```
- Edit the python script _exchangerate.py_.  Set the _locator\_host_,
_locator\_port_ and _prefix_ variables to point to the GemFire cluster you have
started.  For this walk through, the prefix should be "dev_".  The python script
is only used to set up the java command line and run the java program.
```python
locator_host = 'localhost'
locator_port = 10000
prefix = 'dev_'
```
- Invoke the _exchangerate.py_ program.
```
python exchangerate.py EUR USD
```
- Use a gfsh query command to verify that the exchange rate was cached in the
_dev\_Rates_ region.

```
gfsh>connect --locator=localhost[10000]
Connecting to Locator at [host=localhost, port=10000] ..
Connecting to Manager at [host=192.168.1.115, port=11099] ..
Successfully connected to: [host=192.168.1.115, port=11099]

Cluster-1 gfsh>query --query="select key, value from /dev_Rates.entries"

Result     : true
startCount : 0
endCount   : 20
Rows       : 2

  key   | value
------- | ------------------
EUR|USD | 1.0858647781171287
EUR|AUD | 1.546875214043799

NEXT_STEP_NAME : END
```

# Local Cluster Instructions #

These instructions assume that the current directory is _local-cluster_

The scripts require that python3 be installed.

- edit _cluster.json_, set the _cluster\_home_ global property to a working
directory where the cluster will put all log and data files.
```json
{
    "global-properties":{
        "gemfire": "${GEMFIRE}",
        "java-home" : "${JAVA_HOME}",
        "locators" : "localhost[10000]",
        "cluster-home" : "/Users/me/gemcluster/sandbox",
        "distributed-system-id": 1
```
- set the GEMFIRE and JAVA_HOME environment variables
- to start the cluster, run the following command.  By default the locator
will be listening on port 10000 and Pulse will be runnning on port 17070. You
can change these settings in the cluster.json file.
```
python cluster.py start
```
- to stop the cluster, run the following commands.
```
python cluster.py stop
python cluster.py stop locator
```
