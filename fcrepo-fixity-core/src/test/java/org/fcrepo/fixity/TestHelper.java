
package org.fcrepo.fixity;

import java.lang.reflect.Field;

/**
 * @author frank asseg
 */
public abstract class TestHelper {

    public static void setField(Object service, String name, Object obj)
        throws NoSuchFieldException {
        Field f = service.getClass().getDeclaredField(name);
        f.setAccessible(true);
        try {
            f.set(service, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
