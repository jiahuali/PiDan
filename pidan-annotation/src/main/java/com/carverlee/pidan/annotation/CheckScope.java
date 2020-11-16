package com.carverlee.pidan.annotation;

/**
 * @author carverLee
 * 2019/9/26 11:32
 * priority：GLOBAL>PACKAGE>CLASS
 * when GLOBAL scanned,print log global
 * when PACKAGE scanned,Class not belong to PACKAGE,print together
 */
public enum CheckScope {
    GLOBAL, CLASS, PACKAGE
}
