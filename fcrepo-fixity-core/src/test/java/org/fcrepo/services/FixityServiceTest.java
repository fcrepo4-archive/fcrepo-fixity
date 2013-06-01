/**
 *
 */
package org.fcrepo.services;

import java.util.Arrays;

import org.fcrepo.fixity.service.FixityService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author frank asseg
 *
 */
public class FixityServiceTest {

    @Autowired
    private FixityService fixityService;

    @Test
    @Ignore
    public void testQueueFixityCheck() throws Exception {
        fixityService.queueFixityCheck(Arrays.asList("/objects/testob1","/objects/testob2"));
    }
}
