package com.daghosoft.dent;

import org.junit.Ignore;
import org.junit.Test;

public class MainTest {

    @Test
    @Ignore
    public void mainTest() {
        // Main.main(new String[] {"exec"});
        Main.main(new String[] { "moveBasePath", "deleteEmpty", "deleteByExtension", "debugFlag", "exec" });
        // Main.main(null);
    }

}
