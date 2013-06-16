package org.jenkinsci.plugins.zap;

import static org.junit.Assert.*;
import org.jenkinsci.plugins.zap.model.Alert;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class AbstractZapTestBase {
    
    protected final void assertAlert(Alert alert, int high, int medium) {
    	
        assertEquals(high + "/" + medium, alert.getRisk() + "/" + alert.getReliability());
    }
}
