# JUPnP
这是基于[cling](https://github.com/4thline/cling)库的修改和优化

- 将maven项目转为gradle项目
- cling-android解决Could not parse service descriptor问题
- 增加webserver等库的封装
- 提供示例demo

## 如何依赖使用
在根项目build.gradle中添加maven厂库地址

```
buildscript {

    repositories {
        google()
        jcenter()
        maven { url 'https://dl.bintray.com/feijeff0486/JUPnP/' }
    }

    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.3'
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://dl.bintray.com/feijeff0486/JUPnP/' }
    }
}

```
在要使用该库的module下的build.gradle中添加依赖
```
implementation 'com.jeff.jupnp:cling-core:2.1.2'

implementation 'com.jeff.jupnp:cling-android:2.1.2'

```
```
implementation 'com.jeff.jupnp:cling-support:2.1.2'

```