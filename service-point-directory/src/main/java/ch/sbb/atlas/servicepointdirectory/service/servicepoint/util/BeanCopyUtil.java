package ch.sbb.atlas.servicepointdirectory.service.servicepoint.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BeanCopyUtil {
    /**
     * Copies properties from one object to another
     * @param source
     * @destination
     * @return
     */
    public static void copyNonNullProperties(Object source, Object destination, String... ignoreProperties){
        Set<String> ignorePropertiesSet = getNullPropertyNames(source);
        ignorePropertiesSet.addAll(Arrays.asList(ignoreProperties));
        BeanUtils.copyProperties(source, destination, ignoreProperties);
    }

    /**
     * Returns an array of null properties of an object
     * @param source
     * @return
     */
    public static Set<String> getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for(PropertyDescriptor pd : pds) {
            //check if value of this property is null then add it to the collection
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        return emptyNames;
    }
}
