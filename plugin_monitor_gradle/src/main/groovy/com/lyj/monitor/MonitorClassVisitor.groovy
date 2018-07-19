package com.lyj.monitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter


class MonitorClassVisitor extends ClassVisitor {
    /**
     * 类名
     */
    private String className

    /**
     * 父类名
     */
    private String superName

    /**
     * 该类实现的接口
     */
    private String[] interfaces

    public MonitorClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor)
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

            private static boolean isMatchingInterfaces(String[] interfaces, String interfaceName) {
                boolean isMatch = false
                // 是否满足实现的接口
                interfaces.each { String inteface ->
                    if (inteface == interfaceName) {
                        isMatch = true
                    }
                }
                return isMatch
            }

            @Override
            protected void onMethodEnter() {
                if (className.startsWith('android')) {
                    return
                }
                if ("onCreate" == name
                        && (superName == "AppCompatActivity" || superName == "Activity" || superName == "FragmentActivity")) {
                    mv.visitVarInsn(ALOAD, 0)
                    mv.visitMethodInsn(INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onActivityCreate", "(Landroid/app/Activity;)V", false)
                } else if ("onDestroy" == name
                        && (superName == "AppCompatActivity" || superName == "Activity" || superName == "FragmentActivity")) {
                    mv.visitVarInsn(ALOAD, 0)
                    mv.visitMethodInsn(INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onActivityDestroy", "(Landroid/app/Activity;)V", false)
                } else if ("onResume" == name
                        && (superName == "AppCompatActivity" || superName == "Activity" || superName == "FragmentActivity")) {
                    mv.visitVarInsn(ALOAD, 0)
                    mv.visitMethodInsn(INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onActivityResume", "(Landroid/app/Activity;)V", false)
                } else if (isMatchingInterfaces(interfaces, 'android/view/View$OnClickListener') && "onClick" == name) {
                    mv.visitVarInsn(ALOAD, 1)
                    mv.visitMethodInsn(INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onActivityClick", "(Landroid/view/View;)V", false)
                }
            }

            @Override
            protected void onMethodExit(int i) {
                super.onMethodExit(i)
            }
        }
        return methodVisitor
    }
}