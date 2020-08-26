# JUPnP
这是基于[cling](https://github.com/4thline/cling)库的修改和优化

- 将maven项目转为gradle项目
- cling-android解决Could not parse service descriptor问题
- 增加webserver等库的封装
- 提供示例demo

## 如何依赖使用
在要使用该库的module下的build.gradle中添加依赖

依赖cling-core
```
implementation 'com.jeff.jupnp:cling-core:2.1.2'

```
依赖cling-android(使用场景：Android项目中)
```
implementation 'com.jeff.jupnp:cling-android:2.1.2'

```
依赖cling-support

```
implementation 'com.jeff.jupnp:cling-support:2.1.2'

```