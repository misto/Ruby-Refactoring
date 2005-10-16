package org.rubypeople.rdt.ui.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.rubypeople.rdt.internal.ui.TS_InternalUi;
import org.rubypeople.rdt.internal.ui.rubyeditor.TS_InternalUiRubyEditor;
import org.rubypeople.rdt.internal.ui.text.TS_InternalUiText;

public class TS_Ui {
    public static Test suite() {
        TestSuite suite = new TestSuite("org.rubypeople.rdt.ui.tests");      
        suite.addTest(TS_InternalUi.suite());
        suite.addTest(TS_InternalUiRubyEditor.suite());
        suite.addTest(TS_InternalUiText.suite());
        return suite;
    }
}
