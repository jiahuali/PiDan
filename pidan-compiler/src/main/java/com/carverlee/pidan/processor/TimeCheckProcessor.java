package com.carverlee.pidan.processor;

import com.carverlee.pidan.annotation.CheckScope;
import com.carverlee.pidan.annotation.TimeCheck;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * @author carverLee
 * 2019/9/26 11:37
 */
@AutoService(Processor.class)
public class TimeCheckProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementsUtil;
    private ProcessingEnvironment processingEnv;
    private AopClassCreator classCreator;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elementsUtil = processingEnv.getElementUtils();
        this.processingEnv = processingEnv;
        classCreator = new AopClassCreator(messager, elementsUtil);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportSet = new HashSet<>();
        supportSet.add(TimeCheck.class.getCanonicalName());
        return supportSet;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(TimeCheck.class);
        if (elements == null) {
            return false;
        }
        Element globalTimeCheck = CollectionUtils.findItem(elements,
                (item) -> item.getAnnotation(TimeCheck.class).scope() == CheckScope.GLOBAL);

        if (globalTimeCheck != null) {
            messager.printMessage(Diagnostic.Kind.NOTE, "create global");
            classCreator.appendMethod(globalTimeCheck);
            try {
                classCreator.generateJavaCode(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        List<? extends Element> classElements = CollectionUtils.findItemList(elements,
                (item) -> item.getAnnotation(TimeCheck.class).scope() == CheckScope.CLASS);
        List<? extends Element> packageElements = CollectionUtils.findItemList(elements,
                (item) -> item.getAnnotation(TimeCheck.class).scope() == CheckScope.PACKAGE);
        try {
            mergeCheck(packageElements, classElements);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void mergeCheck(List<? extends Element> packageCheckList, List<? extends Element> classCheckList) throws IOException {
        if (packageCheckList.isEmpty()) {
            openCheckList(classCheckList);
            classCreator.generateJavaCode(processingEnv.getFiler());
            return;
        }
        packageCheckList.sort(Comparator.comparingInt(
                data -> data.getAnnotation(TimeCheck.class).packageName().length()
        ));
        List<Element> mergedClassCheckList = new ArrayList<>();
        for (Element classCheck : classCheckList) {//exclude class in package
            for (int i = 0; i < packageCheckList.size(); i++) {
                String classCheckName = elementsUtil.getPackageOf(classCheck).getQualifiedName().toString();
                String packageCheckName = packageCheckList.get(i).getAnnotation(TimeCheck.class).packageName();
                if (classCheckName.startsWith(packageCheckName)) {
                    break;
                }
                if (i == packageCheckList.size() - 1) {
                    mergedClassCheckList.add(classCheck);
                }
            }
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "clas list:" + mergedClassCheckList.size());
        messager.printMessage(Diagnostic.Kind.NOTE, "pack list:" + packageCheckList);
        openCheckList(mergedClassCheckList);
        openCheckList(packageCheckList);
        classCreator.generateJavaCode(processingEnv.getFiler());
    }

    private void openCheckList(List<? extends Element> checkList) {
        if (checkList.isEmpty()) {
            return;
        }
        for (Element element : checkList) {
            classCreator.appendMethod(element);
        }
    }
}
