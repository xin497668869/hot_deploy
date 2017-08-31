package com.alibaba.druid.support.logging;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 * @description
 */
public class LogFactory {


    static {
        // TODO add slf4j logging

        //优先选择log4j,而非Apache Common Logging. 因为后者无法设置真实Log调用者的信息
        tryImplementation("org.apache.log4j.Logger", "com.alibaba.druid.support.logging.Log4jImpl");
        tryImplementation("java.util.logging.Logger", "com.alibaba.druid.support.logging.Jdk14LoggingImpl");
        tryImplementation("org.apache.commons.logging.LogFactory",
                          "com.alibaba.druid.support.logging.JakartaCommonsLoggingImpl");
        tryImplementation("java.lang.Object", "com.alibaba.druid.support.logging.NoLoggingImpl");
    }

    @SuppressWarnings("unchecked")
    private static void tryImplementation(String testClassName, String implClassName) {
//        if (logConstructor == null) {
//            try {
//                Resources.classForName(testClassName);
//                Class implClass = Resources.classForName(implClassName);
//                logConstructor = implClass.getConstructor(new Class[]{Class.class});
//            } catch (Throwable t) {
//            }
//        }
    }

    public static Log getLog(Class aClass) {
        try {
            return new Log() {
                @Override
                public boolean isDebugEnabled() {
                    return false;
                }

                @Override
                public void error(String msg, Throwable e) {

                }

                @Override
                public void error(String msg) {

                }

                @Override
                public boolean isInfoEnabled() {
                    return false;
                }

                @Override
                public void info(String msg) {

                }

                @Override
                public void debug(String msg) {

                }

                @Override
                public void debug(String msg, Throwable e) {

                }

                @Override
                public boolean isWarnEnabled() {
                    return false;
                }

                @Override
                public void warn(String msg) {

                }

                @Override
                public void warn(String msg, Throwable e) {

                }

                @Override
                public int getErrorCount() {
                    return 0;
                }

                @Override
                public int getWarnCount() {
                    return 0;
                }

                @Override
                public int getInfoCount() {
                    return 0;
                }

                @Override
                public void resetStat() {

                }
            };
        } catch (Throwable t) {
            throw new RuntimeException("Error creating logger for class " + aClass + ".  Cause: " + t, t);
        }
    }

    /**
     * This method will switch the logging implementation to Log4J if Log4J is available on the classpath. This is
     * useful in situations where you want to use Log4J to log iBATIS activity but commons logging is on the classpath.
     * Note that this method is only effective for log classes obtained after calling this method. If you intend to use
     * this method you should call it before calling any other iBATIS method.
     */
    @SuppressWarnings("unchecked")
    public static synchronized void selectLog4JLogging() {
        try {
//            Resources.classForName("org.apache.log4j.Logger");
//            Class implClass = Resources.classForName("com.alibaba.druid.support.logging.Log4jImpl");
//            logConstructor = implClass.getConstructor(new Class[] { Class.class });
        } catch (Throwable t) {
        }
    }

    /**
     * This method will switch the logging implementation to Java native logging if you are running in JRE 1.4 or above.
     * This is useful in situations where you want to use Java native logging to log iBATIS activity but commons logging
     * or Log4J is on the classpath. Note that this method is only effective for log classes obtained after calling this
     * method. If you intend to use this method you should call it before calling any other iBATIS method.
     */
    @SuppressWarnings("unchecked")
    public static synchronized void selectJavaLogging() {
        try {
//            Resources.classForName("java.util.logging.Logger");
//            Class implClass = Resources.classForName("com.alibaba.druid.support.logging.Jdk14LoggingImpl");
//            logConstructor = implClass.getConstructor(new Class[] { Class.class });
        } catch (Throwable t) {
        }
    }
}
