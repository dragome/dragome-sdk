# How to build the SDK

## Requirements

 - JDK 1.8
     - It is only required just for compiling JRE-js and java8 examples, but it is not required for executing Dragome applications because specific Java 8 features are converted to Java <=7.
 - Maven 2 or 3


## Step 1: clone SDK from GitHub

``` bash
git clone https://github.com/dragome/dragome-sdk.git
```

## Step 2: execute maven build process

``` bash
mvn clean install
```



# How to build the Examples

## Requirements
Same requirements than SDK

## Same steps
``` bash
git clone https://github.com/dragome/dragome-examples.git
mvn clean install
```

## Starting any example

Located in specific example folder, startup jetty server like this:
``` bash
mvn jetty:run
```
