package com.cy.omniknight.tools;

import java.lang.reflect.Field;

public class ToStringClass {
    @Override
    public String toString() {
        Class<?> classes = this.getClass();
        return getCurrentClass(classes.getSuperclass()) + getCurrentClass(classes);
    }

    private String getCurrentClass(Class<?> classes) {
        StringBuffer sb = new StringBuffer();
        try {
            Field[] fields = classes.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                String fieldName = field.getName();
                sb.append("{");
                sb.append(fieldName);
                sb.append(":");
                if (field.getType() == classes) {
                    sb.append("ignore add myself");
                } else if (field.getType() == Integer.class) {
                    sb.append(field.get(this));
                } else if (field.getType() == Long.class) {
                    sb.append(field.getLong(this));
                } else if (field.getType() == Boolean.class) {
                    sb.append(field.getBoolean(this));
                } else if (field.getType() == char.class) {
                    sb.append(field.getChar(this));
                } else if (field.getType() == Double.class) {
                    sb.append(field.getDouble(this));
                } else if (field.getType() == Float.class) {
                    sb.append(field.getFloat(this));
                } else {
                    sb.append(field.get(this));
                }
                sb.append("}");
                sb.append("\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
