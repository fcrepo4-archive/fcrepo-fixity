/**
 *
 */
package org.fcrepo.services;

import java.util.Arrays;

import org.fcrepo.fixity.service.FixityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author frank asseg
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/unit-test-fixity.xml")
public class FixityServiceTest {

    @Autowired
    private FixityService fixityService;

    @Test
    public void testQueueFixityCheck() throws Exception {
        fixityService.queueFixityCheck(Arrays.asList("/objects/testob1","/objects/testob2"));
    }
}
