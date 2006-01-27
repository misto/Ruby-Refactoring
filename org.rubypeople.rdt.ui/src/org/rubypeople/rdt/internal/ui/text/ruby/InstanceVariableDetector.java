package org.rubypeople.rdt.internal.ui.text.ruby;

public class InstanceVariableDetector extends IdentifierDetector {

    public boolean isWordStart(char c) {
        return c == '@';
    }

}
