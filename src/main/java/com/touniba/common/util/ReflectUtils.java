package com.touniba.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Reflect utils
 * <p>
 * Create by Magoo on 2016/7/21
 */
public class ReflectUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ReflectUtils.class);
    private static final WeakHashMap<Class<?>, WeakReference<Constructor<?>[]>> CONSTRUCTOR_CACHE =
            new WeakHashMap<>();
    private static final Class<?>[] EMPTY_ARRAY = new Class[]{};

    private static final WeakHashMap<ClassLoader, Map<String, WeakReference<Class<?>>>> CLASS_CACHE =
            new WeakHashMap<>();

    private static ClassLoader CLASS_LOADER; // class loader

    /**
     * Get all declared fields
     *
     * @param clazz
     * @return
     */
    public static List<Field> getAllDeclaredFields(Class<?> clazz) {
        List<Field> list = new ArrayList<>();
        Collections.addAll(list, clazz.getDeclaredFields());
        Class<?> superClazz = clazz.getSuperclass();
        if (null != superClazz) {
            list.addAll(getAllDeclaredFields(superClazz));
        }
        return list;
    }

    /**
     * Get declared field
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getDeclaredField(Class<?> clazz, String fieldName) {
        for (Field field : getAllDeclaredFields(clazz)) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    /**
     * get declared fields who's name in fieldNames
     *
     * @param clazz
     * @param fieldNames
     * @return
     */
    public static List<Field> getDeclaredFields(Class<?> clazz, String... fieldNames) {
        List<String> nameList = Arrays.asList(fieldNames);
        return getAllDeclaredFields(clazz).stream().filter(field -> nameList.contains(field.getName())).collect(Collectors.toList());
    }

    /**
     * Get fields with the annotation
     *
     * @param clazz
     * @param annotation
     * @return
     */
    public static List<Field> getAnnotationField(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Field> list = new ArrayList<>();
        for (Field field : getAllDeclaredFields(clazz)) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation that : annotations) {
                if (that.annotationType().equals(annotation)) {
                    list.add(field);
                }
            }
        }
        return list;
    }

    /**
     * get public fields
     *
     * @param clazz
     * @return
     */
    public static Field[] getPublicFields(Class<?> clazz) {
        List<Field> list = new ArrayList<>();
        if (null != clazz) {
            Field[] fields = clazz.getFields();
            for (Field field : fields) {
                if (field.getModifiers() == 1) {
                    list.add(field);
                }
            }
        }
        return list.toArray(new Field[]{});
    }

    /***
     * Get all declared methods.
     * @param clazz
     * @return
     */
    public static List<Method> getAllDeclaredMethods(Class clazz) {
        List<Method> list = new ArrayList<>();
        Collections.addAll(list, clazz.getDeclaredMethods());
        Class<?> superClazz = clazz.getSuperclass();
        if (null != superClazz) {
            list.addAll(getAllDeclaredMethods(superClazz));
        }
        return list;
    }

    /**
     * Get declared methods by name
     *
     * @param clazz
     * @param methodName
     * @return
     */
    public static List<Method> getDeclaredMethods(Class clazz, String methodName) {
        return getAllDeclaredMethods(clazz).stream().filter(method -> method.getName().equals(methodName)).collect(Collectors.toList());
    }

    /**
     * Get declared methods by name
     *
     * @param clazz
     * @param methodName
     * @return
     */
    public static List<Method> getDeclaredMethods(Class clazz, String methodName, Class... parameterTypes) {
        return getAllDeclaredMethods(clazz).stream().filter(method -> method.getName().equals(methodName) &&
                Arrays.equals(method.getParameterTypes(), parameterTypes)).collect(Collectors.toList());
    }

    /**
     * Get method.
     *
     * @param clazz
     * @param methodName
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     */
    public static Method getDeclaredMethod(Class clazz, String methodName, Class... parameterTypes) throws NoSuchMethodException {
        for (Method method : getAllDeclaredMethods(clazz)) {
            if (method.getName().equals(methodName) && Arrays.equals(parameterTypes, method.getParameterTypes())) {
                return method;
            }
        }
        return null;
    }

    /**
     * Getter method. Only public one.
     *
     * @param clazz
     * @param field
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public static Method getterMethod(Class<?> clazz, Field field) throws SecurityException {
        String fieldName = field.getName();
        List<String> prefixes = new ArrayList<>();
        if (field.getType().equals(boolean.class)) {
            prefixes.add("is");
        }
        prefixes.add("get");
        Method method = null;
        for (String prefix : prefixes) {
            String methodName = prefix + CharacterUtils.upperFirst(fieldName);
            try {
                method = clazz.getMethod(methodName);
            } catch (NoSuchMethodException e) {
                continue;
            }
            if (null != method) {
                break;
            }
        }
        return method;
    }

    /**
     * getter method， see {@link #getterMethod(Class, Field)}
     *
     * @param clazz
     * @param fieldName
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws NoSuchFieldException
     */
    public static Method getterMethod(Class<?> clazz, String fieldName) throws SecurityException, NoSuchFieldException {
        Field field = null;
        for (Field that : getAllDeclaredFields(clazz)) {
            if (that.getName().equals(fieldName)) {
                field = that;
                break;
            }
        }
        if (null == field) {
            throw new NoSuchFieldException(fieldName);
        }
        return getterMethod(clazz, field);
    }

    /**
     * setter method, only public ones
     *
     * @param clazz
     * @param field
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public static Method[] setterMethod(Class<?> clazz, Field field) throws SecurityException, NoSuchMethodException {
        String fieldName = field.getName();
        String methodName = "set" + CharacterUtils.upperFirst(fieldName);
        List<Method> list = new ArrayList<>();
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName) && method.getParameterTypes().length > 0) {
                list.add(method);
            }
        }
        return list.toArray(new Method[]{});
    }

    /**
     * setter method, see {@link #setterMethod(Class, Field)}
     *
     * @param clazz
     * @param fieldName
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws NoSuchFieldException
     */
    public static Method[] setterMethod(Class<?> clazz, String fieldName) throws SecurityException, NoSuchMethodException, NoSuchFieldException {
        Field field = null;
        for (Field that : getAllDeclaredFields(clazz)) {
            if (that.getName().equals(fieldName)) {
                field = that;
                break;
            }
        }
        if (null == field) {
            throw new NoSuchFieldException(fieldName);
        }
        return setterMethod(clazz, field);
    }

    /***
     * Get class by name, see {@link #getClassByName(ClassLoader, String)} ,use default class loader
     *
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?> getClassByName(String name) throws ClassNotFoundException {
        try {
            return getClassByName(getDefaultClassLoader(), name);
        } catch (ClassNotFoundException e) {
            // nothing
        }
        return Class.forName(name);
    }

    /***
     * Get class by name
     *
     * @param loader
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?> getClassByName(ClassLoader loader, String name) throws ClassNotFoundException {
        Map<String, WeakReference<Class<?>>> map = CLASS_CACHE.get(loader);
        if (null == map) {
            map = new HashMap<>();
            CLASS_CACHE.put(loader, map);
        }
        WeakReference<Class<?>> reference = map.get(name);
        Class<?> clazz = null;
        if (null != reference) {
            clazz = reference.get();
        }
        if (null != clazz) {
            return clazz;
        }
        if ("byte".equals(name)) {
            clazz = byte.class;
        } else if ("int".equals(name)) {
            clazz = int.class;
        } else if ("short".equals(name)) {
            clazz = short.class;
        } else if ("long".equals(name)) {
            clazz = long.class;
        } else if ("float".equals(name)) {
            clazz = float.class;
        } else if ("double".equals(name)) {
            clazz = double.class;
        } else if ("boolean".equals(name)) {
            clazz = boolean.class;
        } else {
            clazz = loader.loadClass(name);
        }
        map.put(name, new WeakReference<>(clazz));
        return clazz;
    }

    /**
     * Get the default classLoader
     *
     * @return
     */
    public static synchronized ClassLoader getDefaultClassLoader() {
        if (null == CLASS_LOADER) {
            CLASS_LOADER = Thread.currentThread().getContextClassLoader();
            if (CLASS_LOADER == null) {
                CLASS_LOADER = ReflectUtils.class.getClassLoader();
            }
        }
        return CLASS_LOADER;
    }

    /***
     * get generic types
     *
     * @param field
     * @return
     */
    public static Type[] getGenericTypes(Field field) {
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getActualTypeArguments();
        }
        return null;
    }

    /**
     * get value ,first getter then use violence
     *
     * @param obj
     * @param field
     * @return
     */
    public static Object getValue(Object obj, Field field) {
        Method method = null;
        try {
            method = getterMethod(obj.getClass(), field);
        } catch (Throwable ignored) {
        }
        if (null != method) {
            try {
                return method.invoke(obj);
            } catch (Throwable e) {
                LOG.debug("Invoke getter method error: {}", e.toString());
            }
        }
        field.setAccessible(true);
        try {
            return field.get(obj);
        } catch (Throwable e) {
            LOG.debug("Get field value error: {}", e.toString());
        }
        return null;
    }

    /**
     * get value,see {@link #getValue(Object, Field)}
     *
     * @param obj
     * @param fieldName
     * @return
     */
    public static Object getValue(Object obj, String fieldName) {
        Field field = null;
        for (Field that : getAllDeclaredFields(obj.getClass())) {
            if (that.getName().equals(fieldName)) {
                field = that;
                break;
            }
        }
        if (null == field) {
            return null;
        }
        return getValue(obj, field);
    }

    /**
     * is array class
     *
     * @param clazz
     * @return
     */
    public static boolean isArray(Class<?> clazz) {
        return clazz.isArray();
    }

    /**
     * is belong to
     *
     * @param clazz
     * @param target
     * @return
     */
    public static boolean isBelongsTo(Class<?> clazz, Class<?> target) {
        return clazz.equals(target) || target.isAssignableFrom(clazz) || isImplements(clazz, target);
    }

    /**
     * is collection
     *
     * @param clazz
     * @return
     */
    public static boolean isCollection(Class<?> clazz) {
        return isImplements(clazz, Collection.class);
    }

    /**
     * is implements
     *
     * @param clazz
     * @param face
     * @return
     */
    public static boolean isImplements(Class<?> clazz, Class<?> face) {
        if (clazz.equals(face)) {
            return true;
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        if (null == interfaces || interfaces.length < 1) {
            return false;
        }
        for (Class<?> c : interfaces) {
            if (c.equals(face) || face.isAssignableFrom(c)) {
                return true;
            } else {
                if (isImplements(c, face)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Is Map
     *
     * @param clazz
     * @return
     */
    public static boolean isMap(Class<?> clazz) {
        return isImplements(clazz, Map.class);
    }

    /**
     * New instance
     *
     * @param theClass
     * @return
     */
    public static <T> T newInstance(Class<T> theClass) {
        try {
            Constructor<?> constructor = getConstructor(theClass);
            constructor.setAccessible(true);
            return (T) constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get Constructor
     *
     * @param clazz
     * @param params
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public static Constructor<?> getConstructor(Class<?> clazz, Object... params) throws SecurityException, NoSuchMethodException {
        if (null == params || params.length < 1) {
            return clazz.getDeclaredConstructor(EMPTY_ARRAY);
        }
        Class<?>[] paramClasses = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            AssertUtils.notNullArgument(param, "param");
            paramClasses[i] = param.getClass();
        }
        Constructor<?>[] constructors = null;
        WeakReference<Constructor<?>[]> reference = CONSTRUCTOR_CACHE.get(clazz);
        if (null != reference) {
            constructors = reference.get();
        }
        if (null == constructors) {
            constructors = clazz.getDeclaredConstructors();
            CONSTRUCTOR_CACHE.put(clazz, new WeakReference<>(constructors));
        }
        for (Constructor<?> constructor : constructors) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            if (paramTypes.length == paramClasses.length) {
                if (isAssignableFrom(paramClasses, paramTypes)) {
                    return constructor;
                }
            }
        }
        throw new NoSuchMethodException("Constructor not found for params: " + Arrays.toString(paramClasses));
    }

    /**
     * New instance
     *
     * @param theClass
     * @param params
     * @return
     * @throws Exception
     */
    public static <T> T newInstance(Class<T> theClass, Object... params) {
        try {
            Constructor<?> constructor = getConstructor(theClass, params);
            constructor.setAccessible(true);
            return (T) constructor.newInstance(params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * parse basic value silently.
     *
     * @param type
     * @param str
     * @return
     */
    public static Object parseBasicValue(Type type, String str) {
        if (isBasicType(type)) {
            try {
                if (isByte(type)) {
                    return StringUtil.isEmpty(str) ? 0 : Byte.valueOf(str);
                } else if (isInteger(type)) {
                    return StringUtil.isEmpty(str) ? 0 : Integer.valueOf(str);
                } else if (isShort(type)) {
                    return StringUtil.isEmpty(str) ? 0 : Short.valueOf(str);
                } else if (isLong(type)) {
                    return StringUtil.isEmpty(str) ? 0L : Long.valueOf(str);
                } else if (isFloat(type)) {
                    return StringUtil.isEmpty(str) ? 0f :
                            Float.valueOf(str);
                } else if (isDouble(type)) {
                    return StringUtil.isEmpty(str) ? 0d : Double.valueOf(str);
                } else if (isBoolean(type)) {
                    return StringUtil.isEmpty(str) ? false : Boolean.valueOf(str);
                } else if (isString(type)) {
                    return str;
                } else if (isClass(type)) {
                    return Class.forName(str);
                } else if (isEnum(type)) {
                    return StringUtil.isEmpty(str) ? null : Enum.valueOf((Class) type, str);
                }
                return null;
            } catch (Throwable e) {
                LOG.debug("Parse basic value error: {}", e.toString());
            }
        }
        return null;
    }

    /**
     * Is basic type ? include Enum and Class.
     *
     * @param type
     * @return
     */
    public static boolean isBasicType(Type type) {
        if (isByte(type)) {
            return true;
        }
        if (isInteger(type)) {
            return true;
        }
        if (isShort(type)) {
            return true;
        }
        if (isLong(type)) {
            return true;
        }
        if (isFloat(type)) {
            return true;
        }
        if (isDouble(type)) {
            return true;
        }
        if (isBoolean(type)) {
            return true;
        }
        if (isString(type)) {
            return true;
        }
        if (isClass(type)) {
            return true;
        }
        if (isCharacter(type)) {
            return true;
        }
        if (isEnum(type)) {
            return true;
        }
        return false;
    }

    public static boolean isByte(Type type) {
        return type.equals(byte.class) || type.equals(Byte.class);
    }

    public static boolean isInteger(Type type) {
        return type.equals(int.class) || type.equals(Integer.class);
    }

    public static boolean isShort(Type type) {
        return type.equals(short.class) || type.equals(Short.class);
    }

    public static boolean isLong(Type type) {
        return type.equals(long.class) || type.equals(Long.class);
    }

    public static boolean isFloat(Type type) {
        return type.equals(float.class) || type.equals(Float.class);
    }

    public static boolean isDouble(Type type) {
        return type.equals(double.class) || type.equals(Double.class);
    }

    public static boolean isBoolean(Type type) {
        return type.equals(boolean.class) || type.equals(Boolean.class);
    }

    public static boolean isString(Type type) {
        return type.equals(String.class);
    }

    public static boolean isCharacter(Type type) {
        return type.equals(char.class) || type.equals(Character.class);
    }

    public static boolean isEnum(Type type) {
        return Enum.class.isAssignableFrom((Class) type);
    }

    public static boolean isClass(Type type) {
        return type.equals(Class.class);
    }

    /**
     * set value, first setter then use violence
     *
     * @param obj
     * @param field
     * @param values
     * @return
     */
    public static boolean setValue(Object obj, Field field, Object... values) {
        assert null != values;
        if (null == values) {
            return false;
        }
        Method[] methods = null;
        try {
            methods = setterMethod(obj.getClass(), field);
        } catch (Throwable ignored) {
        }
        if (null != methods) {
            for (Method method : methods) {
                try {
                    method.invoke(obj, values);
                    return true;
                } catch (Throwable e) {
                    LOG.debug("Invoke setter method error: {}", e.toString());
                }
            }
        }
        field.setAccessible(true);
        try {
            field.set(obj, values[0]); // only the first value
            return true;
        } catch (Throwable e) {
            LOG.debug("Set field value error: {}", e.toString());
        }
        return false;
    }

    /**
     * set value, see {@link #setValue(Object, Field, Object...)}
     *
     * @param obj
     * @param fieldName
     * @param values
     * @return
     */
    public static boolean setValue(Object obj, String fieldName, Object... values) {
        Field field = null;
        for (Field that : getAllDeclaredFields(obj.getClass())) {
            if (that.getName().equals(fieldName)) {
                field = that;
                break;
            }
        }
        return null != field && setValue(obj, field, values);
    }

    /**
     * is assignable form
     *
     * @param target
     * @param source
     * @return
     */
    private static boolean isAssignableFrom(Class<?>[] target, Class<?>[] source) {
        if (target.length != source.length) {
            throw new IllegalArgumentException("Num not match");
        }
        for (int i = 0; i < target.length; i++) {
            if (!source[i].isAssignableFrom(target[i])) {
                return false;
            }
        }
        return true;
    }

}
