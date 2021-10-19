package ch.sbb.timetable.field.number.versioning;

import ch.sbb.timetable.field.number.entity.Version;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

  public static void getFieldPropertyEdited(Version clazz) {
    try {
      for (Field field : clazz.getClass().getDeclaredFields()) {
        field.setAccessible(true); // to allow the access of member attributes
        Object attribute = field.get(clazz);
        if (attribute != null) {
          System.out.println(field.getName() + "=" + attribute);
        }
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public static String[] getNotNullFields(Object obj) {
    List<String> al = new ArrayList<String>();
    if (obj != null) {              // Check for null input.
      Class<?> cls = obj.getClass();
      Field[] fields = cls.getFields();
      for (Field f : fields) {
        try {
          if (f.get(obj) != null) { // Check for null value.
            al.add(f.getName());    // Add the field name.
          }
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    String[] ret = new String[al.size()]; // Create a String[] to return.
    return al.toArray(ret);               // return as an Array.
  }


  public static String getFieldName(Method method) {
    try {
      Class<?> clazz = method.getDeclaringClass();
      BeanInfo info = Introspector.getBeanInfo(clazz);
      PropertyDescriptor[] props = info.getPropertyDescriptors();
      for (PropertyDescriptor pd : props) {
        if (method.equals(pd.getWriteMethod()) || method.equals(pd.getReadMethod())) {
          System.out.println(pd.getDisplayName());
          return pd.getName();
        }
      }
    } catch (IntrospectionException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }
}
