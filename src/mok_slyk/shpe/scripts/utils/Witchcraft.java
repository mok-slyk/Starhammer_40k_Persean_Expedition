package mok_slyk.shpe.scripts.utils;
import com.fs.starfarer.api.Global;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class Witchcraft {
    private static Logger log = Global.getLogger(Witchcraft.class);
    protected static final MethodHandles.Lookup lookup = MethodHandles.lookup();
    protected static final Class<?> lookupClass = lookup.getClass();

    public static Class<?> fieldClass;
    private static MethodHandle isAccessible;
    private static MethodHandle setAccessible;
    private static MethodHandle getName;
    private static MethodHandle getModifiers;
    private static MethodHandle getType;
    private static MethodHandle get;
    private static MethodHandle set;

    static {
        try {
            fieldClass = Class.forName("java.lang.reflect.Field", false, Class.class.getClassLoader());
            isAccessible = lookup.findVirtual(fieldClass, "isAccessible", MethodType.methodType(boolean.class));
            setAccessible = lookup.findVirtual(fieldClass, "setAccessible", MethodType.methodType(void.class, boolean.class));
            getName = lookup.findVirtual(fieldClass, "getName", MethodType.methodType(String.class));
            getModifiers = lookup.findVirtual(fieldClass, "getModifiers", MethodType.methodType(int.class));
            getType = lookup.findVirtual(fieldClass, "getType", MethodType.methodType(Class.class));
            get = lookup.findVirtual(fieldClass, "get", MethodType.methodType(Object.class, Object.class));
            set = lookup.findVirtual(fieldClass, "set", MethodType.methodType(void.class, Object.class, Object.class));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
            log.warn("Failed to initialize reflection tools for fields", e);
        }
    }

    private Witchcraft() {}
    private static Class<?> getFieldClass() {
        Class<?> fieldClass = null;
        try {
            fieldClass = Class.forName("java.lang.reflect.Field", false, Class.class.getClassLoader());
        } catch (ClassNotFoundException ignored) {}
        return fieldClass;
    }

    private static MethodHandle getFieldGetHandle() {
        MethodHandle fieldGetHandle = null;
        try {
            fieldGetHandle = MethodHandles.lookup().findVirtual(fieldClass, "get", MethodType.methodType(Object.class, Object.class));
        } catch (Exception ignored) {}
        return fieldGetHandle;
    }

    private static MethodHandle getFieldAccessHandle() {
        MethodHandle fieldAccessHandle = null;
        try {
            fieldAccessHandle = MethodHandles.lookup().findVirtual(fieldClass, "setAccessible", MethodType.methodType(Void.class, Boolean.class));
        } catch (Exception ignored) {}
        return fieldAccessHandle;
    }

    public static Object getFromFieldInObject(Object object, String fieldName) {
        log.info("getting from object");
        //return getFromField(object, getAnyField(object, fieldName));
        //return ReflectionUtils.INSTANCE.get(fieldName, object);
        try {
            return new FieldWrapper(findField(object, fieldName), object).get();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getFromField(Object object, Field field) {
        //FromFieldGetter action = new FromFieldGetter(object, field);
        log.info("getting from field");
        //return AccessController.doPrivileged(action);
        try {
            setAccessible.invoke(field,true);
            return get.invoke(field, object);
        } catch (Throwable e) {
            log.warn("problem getting from field: " + e);
            return null;
        }
    }
    public static Field findField(Object instance, String fieldName) {
        return findField(instance, fieldName, true);
    }
    public static Field findField(Object instance, String fieldName, boolean recursive) {
        Class<?> instanceClass = instance.getClass();
        Field field = null;
        while (instanceClass != null && field == null) {
            try {
                log.info("getting field name");
                field = instanceClass.getField(fieldName);
                log.info("got field");
            } catch (Exception e) {
                log.info("problem getting field normall: " + e + " ; attempting to get declared");
                try {
                    field = instanceClass.getDeclaredField(fieldName);
                    log.info("got declared field");
                } catch (Exception f) {
                    log.warn("problem getting declared field: " + f);
                    instanceClass = recursive ? instanceClass.getSuperclass() : null;
                    if (instanceClass != null) log.info("attempting to get from " + instanceClass.getName());
                }
            }
        }
        log.info("got field name?");
        return field;
    }

    public static class FieldWrapper {
        public static Class<?> fieldClass;
        private static MethodHandle isAccessible;
        private static MethodHandle setAccessible;
        private static MethodHandle getName;
        private static MethodHandle getModifiers;
        private static MethodHandle getType;
        private static MethodHandle get;
        private static MethodHandle set;

        static {
            try {
                fieldClass = Class.forName("java.lang.reflect.Field", false, Class.class.getClassLoader());
                isAccessible = lookup.findVirtual(fieldClass, "isAccessible", MethodType.methodType(boolean.class));
                setAccessible = lookup.findVirtual(fieldClass, "setAccessible", MethodType.methodType(void.class, boolean.class));
                getName = lookup.findVirtual(fieldClass, "getName", MethodType.methodType(String.class));
                getModifiers = lookup.findVirtual(fieldClass, "getModifiers", MethodType.methodType(int.class));
                getType = lookup.findVirtual(fieldClass, "getType", MethodType.methodType(Class.class));
                get = lookup.findVirtual(fieldClass, "get", MethodType.methodType(Object.class, Object.class));
                set = lookup.findVirtual(fieldClass, "set", MethodType.methodType(void.class, Object.class, Object.class));
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
                log.warn("Failed to initialize reflection tools for fields", e);
            }
        }

        private final String name;
        private final Object field;
        private final Object instance;

        private FieldWrapper(Object field, Object instance) throws Throwable {
            setAccessible.invoke(field, true);

            this.name = (String) getName.invoke(field);
            this.field = field;
            this.instance = instance;
        }

        public Object get() {
            try {
                return get.invoke(this.field, this.instance);
            } catch (Throwable t) {
                log.error("Failed to use get() for '"+this.name, t);
            }; return null;
        }

        public void set(Object value) {
            try {
                set.invoke(this.field, this.instance, value);
            } catch (Throwable t) {
                log.error("Failed to use set for '"+this.name, t);
            }
        }
    }
    public static class FromFieldGetter implements PrivilegedAction<Object>{
        Object object;
        Field field;
        public FromFieldGetter(Object object, Field field) {
            this.object = object;
            this.field = field;
        }
        @Override
        public Object run() {
            try {
                setAccessible.invoke(field,true);
                return get.invoke(field, object);
            } catch (Throwable e) {
                log.warn(e);
                return null;
            }
        }
    }
}
