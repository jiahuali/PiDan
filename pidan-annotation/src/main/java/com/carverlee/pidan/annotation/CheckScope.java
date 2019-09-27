package com.carverlee.pidan.annotation;

/**
 * @author carverLee
 * 2019/9/26 11:32
 * 优先级顺序：GLOBAL>PACKAGE>CLASS
 * 即扫描到GLOBAL时，全局打印日志
 * 扫描到PACKAGE时，如果CLASS不再PACKAGE内，则同时打印
 */
public enum CheckScope {
    GLOBAL, CLASS, PACKAGE
}
