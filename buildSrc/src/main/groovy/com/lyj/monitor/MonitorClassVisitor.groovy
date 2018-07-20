package com.lyj.monitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class MonitorClassVisitor extends ClassVisitor {
    private ClassVisitor classVisitor;
    //类名
    private String className
    // 父类名
    private String superName
    //该类实现的接口
    private String[] interfaces
    //Activity访问过的方法，在类结束时判断是否需要添加方法
    public HashSet<String> visitedActivityMethods = new HashSet<>()// 无需判空

    public MonitorClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor)
        this.classVisitor = classVisitor
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        this.className = name
        this.superName = superName
        this.interfaces = interfaces
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature,
                                     String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)
        methodVisitor = new AdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, desc) {
            @Override
            protected void onMethodExit(int i) {
                super.onMethodExit(i)
                // 排除系统类
                if (className.startsWith('android')) {
                    return
                }

                if (MonitorUtil.isExtendsActivity(superName)) {
                    //activity 生命周期埋点
                    if (MonitorConfig.ACTIVITY_METHOD_ONCREATE == name) {
                        visitedActivityMethods.add(MonitorConfig.ACTIVITY_METHOD_ONCREATE)
                        mv.visitVarInsn(Opcodes.ALOAD, 0)
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onActivityCreate", "(Landroid/app/Activity;)V", false)
                    } else if (MonitorConfig.ACTIVITY_METHOD_ONDESTROY == name) {
                        visitedActivityMethods.add(MonitorConfig.ACTIVITY_METHOD_ONDESTROY)
                        mv.visitVarInsn(Opcodes.ALOAD, 0)
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onActivityDestroy", "(Landroid/app/Activity;)V", false)
                    } else if (MonitorConfig.ACTIVITY_METHOD_ONRESUME == name) {
                        visitedActivityMethods.add(MonitorConfig.ACTIVITY_METHOD_ONRESUME)
                        mv.visitVarInsn(Opcodes.ALOAD, 0)
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onActivityResume", "(Landroid/app/Activity;)V", false)
                    } else if (MonitorConfig.ACTIVITY_METHOD_ONPAUSE == name) {
                        visitedActivityMethods.add(MonitorConfig.ACTIVITY_METHOD_ONPAUSE)
                        mv.visitVarInsn(Opcodes.ALOAD, 0)
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onActivityPause", "(Landroid/app/Activity;)V", false)
                    }
                } else if (MonitorUtil.isMatchingInterfaces(interfaces, 'android/view/View$OnClickListener') && "onClick" == name) {
                    //onClick 埋点
                    mv.visitVarInsn(Opcodes.ALOAD, 1)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onClick", "(Landroid/view/View;)V", false)
                }
            }
        }
        return methodVisitor
    }

    @Override
    void visitEnd() {
        //是Activity类 查询其中4个方法是否都已调用，没调用的先插入函数再调用
        if (className.startsWith('android')) {
            return
        }

        if (MonitorUtil.isExtendsActivity(superName)) {
            boolean hasOnCreate = false
            boolean hasOnDestory = false
            boolean hasOnResume = false
            boolean hasOnPause = false
            for (int i = 0; i < visitedActivityMethods.size(); i++) {
                if (visitedActivityMethods[i] == MonitorConfig.ACTIVITY_METHOD_ONCREATE) {
                    hasOnCreate = true
                } else if (visitedActivityMethods[i] == MonitorConfig.ACTIVITY_METHOD_ONDESTROY) {
                    hasOnDestory = true
                } else if (visitedActivityMethods[i] == MonitorConfig.ACTIVITY_METHOD_ONRESUME) {
                    hasOnResume = true
                } else if (visitedActivityMethods[i] == MonitorConfig.ACTIVITY_METHOD_ONPAUSE) {
                    hasOnPause = true
                }
            }
            if (!hasOnCreate) {
                MonitorUtil.insertOnCreate(classVisitor, superName)
            }
            if (!hasOnDestory) {
                MonitorUtil.insertOnDestroy(classVisitor, superName)
            }
            if (!hasOnResume) {
                MonitorUtil.insertOnResume(classVisitor, superName)
            }
            if (!hasOnPause) {
                MonitorUtil.insertOnPause(classVisitor, superName)
            }
        }
        super.visitEnd()
    }
}