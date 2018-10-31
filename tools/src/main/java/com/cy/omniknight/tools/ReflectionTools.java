package com.cy.omniknight.tools;

import android.annotation.TargetApi;
import android.os.Build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cy on 18-9-27.
 */

public class ReflectionTools {
    public static final Map<String, Object> PRIMITIVE_RETURN_VALUES =
            Collections.unmodifiableMap(new HashMap<String, Object>() {{
                put("boolean", Boolean.FALSE);
                put("int", 0);
                put("long", (long) 0);
                put("float", (float) 0);
                put("double", (double) 0);
                put("short", (short) 0);
                put("byte", (byte) 0);
            }});

    public static Object getMethod(String className, String methodName) throws Exception {
        return getMethod(className, methodName, null, null);
    }

    /**
     * 返回是否调用成功
     *
     * @param className
     * @param methodName
     * @param parameterTypes
     * @param args
     *            是否有返回值
     * @return
     */
    public static Object getMethod(String className, String methodName, Class<?>[] parameterTypes,
                                   Object[] args) throws Exception {
        Class<?> classes = Class.forName(className);
        Object instance = classes.newInstance();
//        if (getInstance == null) {
//            throw new Exception("-----------反射获取类实例:" + className + "失败，返回");
//        }
        Method method = classes.getMethod(methodName, parameterTypes);
        Object roProductManufacturer = method.invoke(instance, args);
        return roProductManufacturer;
    }

    public static Object getMethod(String className, Object instance, String methodName) throws Exception {
        return getMethod(className, instance, methodName, null, null);

    }

    /**
     * 返回是否调用成功
     *
     * @param className
     * @param instance
     * @param methodName
     * @param parameterTypes
     * @param args
     *            是否有返回值
     * @return
     */
    public static Object getMethod(String className, Object instance, String methodName,
                                   Class<?>[] parameterTypes, Object[] args) throws Exception {
        Class<?> classes = Class.forName(className);
        if (instance == null) {
            throw new Exception("-----------反射获取类实例:" + className + "失败，返回");
        }
        Method method = classes.getMethod(methodName, parameterTypes);
        Object roProductManufacturer = method.invoke(instance, args);
        return roProductManufacturer;
    }

    public static Object getStaticMethod(String className, String methodName) throws Exception {
        return getStaticMethod(className, methodName, null, null);
    }

    public static Object getStaticMethod(String className, String methodName, Class<?>[] parameterTypes,
                                         Object[] args) throws Exception {
        Class<?> c = Class.forName(className);
        Method m = c.getMethod(methodName, parameterTypes);
        return m.invoke(c, args);
    }

    public static <T> T getClassInstance(String className, Class<?> parameterTypes, Object args)
            throws Exception {
        Class<?> classes = Class.forName(className);
        Constructor<?> constructor = classes.getDeclaredConstructor(parameterTypes);
        constructor.setAccessible(true);
        T instance = (T) constructor.newInstance(args);
        return instance;
    }

    public static <T> T getClassInstance(String className, Class<?>[] parameterTypes, Object[] args)
            throws Exception {
        Class<?> classes = Class.forName(className);
        Constructor<?> constructor = classes.getDeclaredConstructor(parameterTypes);
        constructor.setAccessible(true);
        T instance = (T) constructor.newInstance(args);
        return instance;
    }

    public static Class<?> getClass(String className) throws Exception {
        Class<?> classes = Class.forName(className);
        return classes;
    }

    public static void updateStaticFieldValue(String className, String staticFieldName, boolean args) {
        try {
            Class<?> classes = Class.forName(className);
            Field method = classes.getField(staticFieldName);
            method.setBoolean(classes, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getInterface(String callbackClassName, InvocationHandler mHandler) {
        try {
            Class<?> mCallback = Class.forName(callbackClassName);
            Object mObj = Proxy.newProxyInstance(mHandler.getClass().getClassLoader(),
                    new Class[] {mCallback}, mHandler);
            return mObj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T createNullProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return PRIMITIVE_RETURN_VALUES.get(method.getReturnType().getName());
                    }
                });
    }

    public static <A extends Annotation> A defaultsFor(Class<A> annotation) {
        return annotation.cast(
                Proxy.newProxyInstance(annotation.getClassLoader(), new Class[] { annotation },
                        new InvocationHandler() {
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                return method.getDefaultValue();
                            }
                        }));
    }

    /**
     * Reflectively get the value of a field.
     *
     * @param object Target object.
     * @param fieldName The field name.
     * @param <R> The return type.
     * @return Value of the field on the object.
     */
    @SuppressWarnings("unchecked")
    public static <R> R getField(final Object object, final String fieldName) {
        try {
            return traverseClassHierarchy(object.getClass(), NoSuchFieldException.class, new InsideTraversal<R>() {
                @Override
                public R run(Class<?> traversalClass) throws Exception {
                    Field field = traversalClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return (R) field.get(object);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reflectively set the value of a field.
     *
     * @param object Target object.
     * @param fieldName The field name.
     * @param fieldNewValue New value.
     */
    public static void setField(final Object object, final String fieldName, final Object fieldNewValue) {
        try {
            traverseClassHierarchy(object.getClass(), NoSuchFieldException.class, new InsideTraversal<Void>() {
                @Override
                public Void run(Class<?> traversalClass) throws Exception {
                    Field field = traversalClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(object, fieldNewValue);
                    return null;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reflectively set the value of a field.
     *
     * @param type Target type.
     * @param object Target object.
     * @param fieldName The field name.
     * @param fieldNewValue New value.
     */
    public static void setField(Class<?> type, final Object object, final String fieldName, final Object fieldNewValue) {
        try {
            Field field = type.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, fieldNewValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reflectively get the value of a static field.
     *
     * @param field Field object.
     * @param <R> The return type.
     * @return Value of the field.
     */
    @SuppressWarnings("unchecked")
    public static <R> R getStaticField(Field field) {
        try {
            makeFieldVeryAccessible(field);
            return (R) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reflectively get the value of a static field.
     *
     * @param clazz Target class.
     * @param fieldName The field name.
     * @param <R> The return type.
     * @return Value of the field.
     */
    public static <R> R getStaticField(Class<?> clazz, String fieldName) {
        try {
            return getStaticField(clazz.getDeclaredField(fieldName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reflectively set the value of a static field.
     *
     * @param field Field object.
     * @param fieldNewValue The new value.
     */
    public static void setStaticField(Field field, Object fieldNewValue) {
        try {
            makeFieldVeryAccessible(field);
            field.set(null, fieldNewValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reflectively set the value of a static field.
     *
     * @param clazz Target class.
     * @param fieldName The field name.
     * @param fieldNewValue The new value.
     */
    public static void setStaticField(Class<?> clazz, String fieldName, Object fieldNewValue) {
        try {
            setStaticField(clazz.getDeclaredField(fieldName), fieldNewValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reflectively call an instance method on an object.
     *
     * @param instance Target object.
     * @param methodName The method name to call.
     * @param classParameters Array of parameter types and values.
     * @param <R> The return type.
     * @return The return value of the method.
     */
    public static <R> R callInstanceMethod(final Object instance, final String methodName, ClassParameter<?>... classParameters) {
        try {
            final Class<?>[] classes = ClassParameter.getClasses(classParameters);
            final Object[] values = ClassParameter.getValues(classParameters);

            return traverseClassHierarchy(instance.getClass(), NoSuchMethodException.class, new InsideTraversal<R>() {
                @Override
                @SuppressWarnings("unchecked")
                public R run(Class<?> traversalClass) throws Exception {
                    Method declaredMethod = traversalClass.getDeclaredMethod(methodName, classes);
                    declaredMethod.setAccessible(true);
                    return (R) declaredMethod.invoke(instance, values);
                }
            });
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
            if (e.getTargetException() instanceof Error) {
                throw (Error) e.getTargetException();
            }
            throw new RuntimeException(e.getTargetException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reflectively call an instance method on an object on a specific class.
     *
     * @param cl The class.
     * @param instance Target object.
     * @param methodName The method name to call.
     * @param classParameters Array of parameter types and values.
     * @param <R> The return type.
     * @return The return value of the method.
     */
    public static <R> R callInstanceMethod(Class<?> cl, final Object instance, final String methodName, ClassParameter<?>... classParameters) {
        try {
            final Class<?>[] classes = ClassParameter.getClasses(classParameters);
            final Object[] values = ClassParameter.getValues(classParameters);

            Method declaredMethod = cl.getDeclaredMethod(methodName, classes);
            declaredMethod.setAccessible(true);
            return (R) declaredMethod.invoke(instance, values);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
            if (e.getTargetException() instanceof Error) {
                throw (Error) e.getTargetException();
            }
            throw new RuntimeException(e.getTargetException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reflectively call a static method on a class.
     *
     * @param clazz Target class.
     * @param methodName The method name to call.
     * @param classParameters Array of parameter types and values.
     * @param <R> The return type.
     * @return The return value of the method.
     */
    @SuppressWarnings("unchecked")
    public static <R> R callStaticMethod(Class<?> clazz, String methodName, ClassParameter<?>... classParameters) {
        try {
            Class<?>[] classes = ClassParameter.getClasses(classParameters);
            Object[] values = ClassParameter.getValues(classParameters);

            Method method = clazz.getDeclaredMethod(methodName, classes);
            method.setAccessible(true);
            return (R) method.invoke(null, values);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
            if (e.getTargetException() instanceof Error) {
                throw (Error) e.getTargetException();
            }
            throw new RuntimeException(e.getTargetException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load a class.
     *
     * @param classLoader The class loader.
     * @param fullyQualifiedClassName The fully qualified class name.
     * @return The class object.
     */
    public static Class<?> loadClass(ClassLoader classLoader, String fullyQualifiedClassName) {
        try {
            return classLoader.loadClass(fullyQualifiedClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a new instance of a class
     *
     * @param cl The class object.
     * @param <T> The class type.
     * @return New class instance.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static <T> T newInstance(Class<T> cl) {
        try {
            return cl.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reflectively call the constructor of an object.
     *
     * @param clazz Target class.
     * @param classParameters Array of parameter types and values.
     * @param <R> The return type.
     * @return The return value of the method.
     */
    public static <R> R callConstructor(Class<? extends R> clazz, ClassParameter<?>... classParameters) {
        try {
            final Class<?>[] classes = ClassParameter.getClasses(classParameters);
            final Object[] values = ClassParameter.getValues(classParameters);

            Constructor<? extends R> constructor = clazz.getDeclaredConstructor(classes);
            constructor.setAccessible(true);
            return constructor.newInstance(values);
        } catch (InstantiationException e) {
            throw new RuntimeException("error instantiating " + clazz.getName(), e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
            if (e.getTargetException() instanceof Error) {
                throw (Error) e.getTargetException();
            }
            throw new RuntimeException(e.getTargetException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <R, E extends Exception> R traverseClassHierarchy(Class<?> targetClass, Class<? extends E> exceptionClass, InsideTraversal<R> insideTraversal) throws Exception {
        Class<?> hierarchyTraversalClass = targetClass;
        while (true) {
            try {
                return insideTraversal.run(hierarchyTraversalClass);
            } catch (Exception e) {
                if (!exceptionClass.isInstance(e)) {
                    throw e;
                }
                hierarchyTraversalClass = hierarchyTraversalClass.getSuperclass();
                if (hierarchyTraversalClass == null) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void makeFieldVeryAccessible(Field field) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }

    public static Object defaultValueForType(String returnType) {
        return PRIMITIVE_RETURN_VALUES.get(returnType);
    }

    private interface InsideTraversal<R> {
        R run(Class<?> traversalClass) throws Exception;
    }

    /**
     * Typed parameter used with reflective method calls.
     *
     * @param <V> The value of the method parameter.
     */
    public static class ClassParameter<V> {
        public final Class<? extends V> clazz;
        public final V val;

        public ClassParameter(Class<? extends V> clazz, V val) {
            this.clazz = clazz;
            this.val = val;
        }

        public static <V> ClassParameter<V> from(Class<? extends V> clazz, V val) {
            return new ClassParameter<>(clazz, val);
        }

        public static ClassParameter<?>[] fromComponentLists(Class<?>[] classes, Object[] values) {
            ClassParameter<?>[] classParameters = new ClassParameter[classes.length];
            for (int i = 0; i < classes.length; i++) {
                classParameters[i] = ClassParameter.from(classes[i], values[i]);
            }
            return classParameters;
        }

        public static Class<?>[] getClasses(ClassParameter<?>... classParameters) {
            Class<?>[] classes = new Class[classParameters.length];
            for (int i = 0; i < classParameters.length; i++) {
                Class<?> paramClass = classParameters[i].clazz;
                classes[i] = paramClass;
            }
            return classes;
        }

        public static Object[] getValues(ClassParameter<?>... classParameters) {
            Object[] values = new Object[classParameters.length];
            for (int i = 0; i < classParameters.length; i++) {
                Object paramValue = classParameters[i].val;
                values[i] = paramValue;
            }
            return values;
        }
    }

    /**
     * String parameter used with reflective method calls.
     *
     * @param <V> The value of the method parameter.
     */
    public static class StringParameter<V> {
        public final String className;
        public final V val;

        public StringParameter(String className, V val) {
            this.className = className;
            this.val = val;
        }

        public static <V> StringParameter<V> from(String className, V val) {
            return new StringParameter<>(className, val);
        }
    }
}
