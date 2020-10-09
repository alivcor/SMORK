
<p align="center">
<img src="https://github.com/alivcor/SMORK/raw/master/static/smork_white_350.png" style="max-width:50%;"/>
</p>

# SMORK
## SMOTE in Spark

[![Open Source Love](https://badges.frapsoft.com/os/v1/open-source.png?v=103)](https://github.com/alivcor/SMORK)

[![Build Status](https://travis-ci.org/alivcor/megaclite.svg?branch=master)](https://travis-ci.org/alivcor/SMORK)

<p align="center">
<img src="https://github.com/alivcor/SMORK/raw/master/static/smork_350.png" />
</p>

Implementation of SMOTE - Synthetic Minority Over-sampling Technique in SparkML / MLLib

<a href="https://github.com/alivcor/SMORK">:octocat: Link to GitHub Repo</a>

## Getting Started

This is a very basic implementation of SMOTE Algorithm in SparkML. This is the only available implementation which plugs in to Spark Pipelines.


### Prerequisites

 - Spark 2.3.0 + 

### Installation

#### 1. Build The Jar

```bash
      sbt clean package
```

#### 2. Add The Jar to your Spark Application

Linux

```bash
      --conf "spark.driver.extraClassPath=/path/to/smork-0.0.1.jar"
```

#### 3. Use it normally as you would use any Estimator in Spark. 

##### - Import 
```scala
      import com.iresium.ml.SMOTE
```

##### - Initialize & Fit
```scala
    val smote = new SMOTE()
    smote.setfeatureCol("myFeatures").setlabelCol("myLabel").setbucketLength(100)

    val smoteModel = smote.fit(df)

```

##### - Transform
```scala
    val newDF = smoteModel.transform(df)
```

You can also see and run an example in `src/main/scala/SMORKApp.scala`

### Coming Soon


- [ ] PySMORK - Python Wrapper for SMORK - allows you to use SMOTE in PySpark
- [ ] Support for categorical attributes

<!-- #### Python Package Index

SMORK is now available at https://pypi.python.org/pypi/smork/0.1



```
1. Download the tar/zip from https://pypi.python.org/pypi/smork/0.1
2. Move the package to your desired location / python version, and unzip the archive.
Optionally, if you have a linux-based machine (Ubuntu/OSX):
      tar xvzf smork-0.x.tar.gz -C /path/to/desireddirectory
3. Migrate to the smork folder, and run
      python setup.py install
``` -->

<!-- #### Using pip

```
pip install smork
```

To upgrade,

```
pip install --upgrade smork
``` -->

<!-- 
## Using SMORK

 -->


<!-- ## See the magic unleash

<p align="center">
<img src="megaclite_demo.png" />
</p>
 -->

## Contributing

Looking for contributors ! You are welcome to raise issues / send a pull-request.


## Authors

* **Abhinandan Dubey** - *@alivcor*

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

[![forthebadge](http://forthebadge.com/images/badges/makes-people-smile.svg)](https://github.com/alivcor/SMORK/#)
<script type="text/javascript" src="https://cdnjs.buymeacoffee.com/1.0.0/button.prod.min.js" data-name="bmc-button" data-slug="abhinandandubey" data-color="#FF813F" data-emoji=""  data-font="Cookie" data-text="Buy me a coffee" data-outline-color="#000" data-font-color="#fff" data-coffee-color="#fd0" ></script>
