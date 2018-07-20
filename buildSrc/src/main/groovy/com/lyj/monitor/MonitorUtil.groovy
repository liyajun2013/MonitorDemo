package com.lyj.monitor

class MonitorUtil {
    /**
     * 判断实现的接口里是否包括interfaceName这里类
     * @param interfaces
     * @param interfaceName
     * @return
     */
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

    /**
     * 是否第一个继承Activity的类
     * @param superName
     * @return
     */
    private static boolean isExtendsActivity(String superName) {
        if (superName == "android/support/v7/app/AppCompatActivity"
                || superName == "android/support/v4.app/FragmentActivity"
                || superName == "android/support/v4.app/Activity") {
            return true
        }
        return false
    }

}