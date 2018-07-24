package com.lyj.monitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MonitorUtil {
    /**
     * 判断实现的接口里是否包括interfaceName这里类
     * @param interfaces
     * @param interfaceName
     * @return
     */
    static boolean isMatchingInterfaces(String[] interfaces, String interfaceName) {
        boolean isMatch = false
        // 是否满足实现的接口
        interfaces.each { String inteface ->
            if (inteface == interfaceName) {
                isMatch = true
            }
        }
        return isMatch
    }

    /**
     * 是否第一个继承Activity的类
     * @param superName
     * @return
     */
    static boolean isExtendsActivity(String superName) {
        if (superName == "android/support/v7/app/AppCompatActivity"
                || superName == "android/support/v4.app/FragmentActivity"
                || superName == "android/support/v4.app/Activity") {
            return true
        }
        return false
    }
    /**
     * 是否第一个继承Fragment的类
     * @param superName
     * @return
     */
    static boolean isExtendsFragment(String superName) {
        if (superName == "android/app/Fragment"
                || superName == "android/support/v4/app/Fragment"
                || superName == "android/support/v4/app/DialogFragment"
                || superName == "android/app/DialogFragment") {
            return true
        }
        return false
    }
    /**
     * 插入onDestory函数 带有TraceUtil埋点
     * @param classVisitor
     * @param superName
     */
    static void insertActivityOnDestroy(ClassVisitor classVisitor, String superName) {
        MethodVisitor mv = classVisitor.visitMethod(Opcodes.ACC_PROTECTED, "onDestroy", "()V", null, null)
        mv.visitCode()
        //插入super.onXxx
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, "onDestroy", "()V", false)
        //插入需要的函数
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onActivityDestroy", "(Landroid/app/Activity;)V", false)
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(1, 1)
        mv.visitEnd()
    }

    /**
     * 插入onCreate函数 带有TraceUtil埋点
     * @param classVisitor
     * @param superName
     */
    static void insertActivityOnCreate(ClassVisitor classVisitor, String superName) {
        MethodVisitor mv = classVisitor.visitMethod(Opcodes.ACC_PROTECTED, "onCreate", "(Landroid/os/Bundle;)V", null, null)
        mv.visitCode()
        //插入super.onXxx
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitVarInsn(Opcodes.ALOAD, 1)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, "onCreate", "(Landroid/os/Bundle;)V", false)
        //插入需要的函数
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onActivityCreate", "(Landroid/app/Activity;)V", false);
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(2, 2)
        mv.visitEnd()
    }

    /**
     * 插入onResume函数 带有TraceUtil埋点
     * @param classVisitor
     * @param superName
     */
    static void insertActivityOnResume(ClassVisitor classVisitor, String superName) {
        MethodVisitor mv = classVisitor.visitMethod(Opcodes.ACC_PROTECTED, "onResume", "()V", null, null)
        mv.visitCode()
        //插入super.onXxx
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, "onResume", "()V", false)
        //插入需要的函数
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onActivityResume", "(Landroid/app/Activity;)V", false)
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(1, 1)
        mv.visitEnd()
    }
    /**
     * 插入onPause函数 带有TraceUtil埋点
     * @param classVisitor
     * @param superName
     */
    static void insertActivityOnPause(ClassVisitor classVisitor, String superName) {
        MethodVisitor mv = classVisitor.visitMethod(Opcodes.ACC_PROTECTED, "onPause", "()V", null, null)
        mv.visitCode()
        //插入super.onXxx
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, "onPause", "()V", false)
        //插入需要的函数
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onActivityPause", "(Landroid/app/Activity;)V", false)
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(1, 1)
        mv.visitEnd()
    }

    /**
     * 插入onResume函数 带有TraceUtil埋点
     * @param classVisitor
     * @param superName
     */
    static void insertFragmentOnResume(ClassVisitor classVisitor, String superName) {
        MethodVisitor mv = classVisitor.visitMethod(Opcodes.ACC_PUBLIC, "onResume", "()V", null, null);
        mv.visitCode()
        //插入super.onXxx
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, "onResume", "()V", false)
        //插入需要的函数
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onFragmentResume", "(Ljava/lang/Object;)V", false)
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(1, 1)
        mv.visitEnd()
    }

    /**
     * 插入onPause函数 带有TraceUtil埋点
     * @param classVisitor
     * @param superName
     */
    static void insertFragmentOnPause(ClassVisitor classVisitor, String superName) {
        MethodVisitor mv = classVisitor.visitMethod(Opcodes.ACC_PUBLIC, "onPause", "()V", null, null);
        mv.visitCode()
        //插入super.onXxx
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, "onPause", "()V", false)
        //插入需要的函数
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onFragmentPause", "(Ljava/lang/Object;)V", false)
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(1, 1)
        mv.visitEnd()
    }

    /**
     * 插入onHiddenChanged函数 带有TraceUtil埋点
     * @param classVisitor
     * @param superName
     */
    static void insertFragmentOnHiddenChanged(ClassVisitor classVisitor, String superName) {
        MethodVisitor mv = classVisitor.visitMethod(Opcodes.ACC_PUBLIC, "onHiddenChanged", "(Z)V", null, null)
        mv.visitCode()
        //插入super.onXxx
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitVarInsn(Opcodes.ILOAD, 1)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, "onHiddenChanged", "(Z)V", false)
        //插入需要的函数
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitVarInsn(Opcodes.ILOAD, 1)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onFragmentHiddenChanged", "(Ljava/lang/Object;Z)V", false)
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd()
    }

    /**
     * 插入setUserVisibleHint函数 带有TraceUtil埋点
     * @param classVisitor
     * @param superName
     */
    static void insertFragmentSetUserVisibleHint(ClassVisitor classVisitor, String superName) {
        MethodVisitor mv = classVisitor.visitMethod(Opcodes.ACC_PUBLIC, "setUserVisibleHint", "(Z)V", null, null)
        mv.visitCode()
        //插入super.onXxx
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitVarInsn(Opcodes.ILOAD, 1)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, "setUserVisibleHint", "(Z)V", false)
        //插入需要的函数
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitVarInsn(Opcodes.ILOAD, 1)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lyj/libmonitor/TraceUtil", "onFragmentSetUserVisibleHint", "(Ljava/lang/Object;Z)V", false)
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd()
    }
}