package com.carverlee.pidan.processor;

import com.carverlee.pidan.annotation.CheckScope;
import com.carverlee.pidan.annotation.TimeCheck;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * @author carverLee
 * 2019/9/26 13:49
 */
class AopClassCreator {
    private static int seq = 1;
    private Messager messager;
    private Elements elementsUtil;
    private TypeSpec.Builder typeBuilder;
    private final String packageName = "com.carverlee.pidan";
    private final ClassName CLASS_LOG = ClassName.get("android.util", "Log");
    private final ClassName CLASS_SIGNATURE = ClassName.get("org.aspectj.lang", "Signature");
    private final ClassName CLASS_PROCEEDING_JOIN_POINT = ClassName.get("org.aspectj.lang", "ProceedingJoinPoint");
    private final ClassName CLASS_ASPECT = ClassName.get("org.aspectj.lang.annotation", "Aspect");
    private final ClassName CLASS_AROUND = ClassName.get("org.aspectj.lang.annotation", "Around");
//    private final ClassName CLASS_POINT_CUT = ClassName.get("org.aspectj.lang.annotation", "Pointcut");

    AopClassCreator(Messager messager, Elements elements) {
        this.messager = messager;
        this.elementsUtil = elements;
        init();
    }

    private void init() {
        typeBuilder = TypeSpec.classBuilder("TimeCheckAopInterceptor")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(CLASS_ASPECT);
    }

    public void generateJavaCode(Filer filer) throws IOException {
        messager.printMessage(Diagnostic.Kind.NOTE, "begin to create javaFile");
        JavaFile javaFile = JavaFile.builder(packageName, typeBuilder.build())
                .build();
        javaFile.writeTo(filer);
    }

    public void appendMethod(Element element) {
        TimeCheck timeCheck = element.getAnnotation(TimeCheck.class);
        if (timeCheck.scope() == CheckScope.CLASS) {
            appendClass(element, timeCheck.tag());
            messager.printMessage(Diagnostic.Kind.NOTE, "append class ");
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE, "append package ");
            appendPackage(element, timeCheck.tag());
        }
    }

    private void appendPackage(Element element, String tag) {
        typeBuilder.addMethod(buildPointCut(buildPackageExecutionStr(element), tag));
    }

    private void appendClass(Element element, String tag) {
        typeBuilder.addMethod(buildPointCut(buildClassExecutionStr(element), tag));
    }

    private MethodSpec buildPointCut(String memberVal, String tag) {
        AnnotationSpec annotationSpec = AnnotationSpec.builder(CLASS_AROUND)
                .addMember("value", memberVal)
                .build();
        ParameterSpec parameterSpec = ParameterSpec
                .builder(CLASS_PROCEEDING_JOIN_POINT, "point")
                .build();

        CodeBlock codeBlock = CodeBlock.builder()
                .addStatement("$T signature = point.getSignature()", CLASS_SIGNATURE)
                .addStatement("String methodName = signature.getDeclaringTypeName() + \".\" + signature.getName()")
                .addStatement("long before = System.currentTimeMillis()")
                .addStatement("Object proceed = point.proceed()")
                .addStatement("$T.i($S, \"timeCount: \" + methodName + \",execute time:\" + (System.currentTimeMillis() - before))", CLASS_LOG, tag)
                .addStatement("return proceed")
                .build();
        return MethodSpec.methodBuilder("log" + seq++)
                .addAnnotation(annotationSpec)
                .addModifiers(Modifier.PUBLIC)
                .addException(Throwable.class)
                .returns(Object.class)
                .addParameter(parameterSpec)
                .addCode(codeBlock)
                .build();
    }

    private String buildPackageExecutionStr(Element element) {
        StringBuilder sb = new StringBuilder();
        TimeCheck timeCheck = element.getAnnotation(TimeCheck.class);
        String packageName = timeCheck.packageName();
        if (packageName.length() == 0) {
            packageName = elementsUtil.getPackageOf(element).getQualifiedName().toString();
        }
        sb.append(String.format("\"execution(* %s..*.*(..))\"", packageName));
//        sb.append("||");
//        sb.append(String.format("execution(* %s.*.*(..))\"", packageName));
        return sb.toString();
    }

    private String buildClassExecutionStr(Element element) {
        PackageElement packageElement = elementsUtil.getPackageOf(element);
        String packageName = packageElement.getQualifiedName().toString();
        String fullName = packageName + "." + element.getSimpleName();
        return String.format("\"execution(* %s.*(..))\"", fullName);
    }
}
