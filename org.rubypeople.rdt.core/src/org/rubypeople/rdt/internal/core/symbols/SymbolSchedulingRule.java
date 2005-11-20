package org.rubypeople.rdt.internal.core.symbols;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class SymbolSchedulingRule implements ISchedulingRule {

    public boolean contains(ISchedulingRule rule) {
        return isConflicting(rule);
    }

    public boolean isConflicting(ISchedulingRule rule) {
        return rule instanceof SymbolSchedulingRule;
    }

}
