package org.rubypeople.rdt.internal.ui.symbols;

import junit.framework.TestCase;

import org.eclipse.core.runtime.jobs.Job;
import org.rubypeople.rdt.internal.core.builder.ShamSymbolIndex;
import org.rubypeople.rdt.internal.core.symbols.Symbol;
import org.rubypeople.rdt.internal.core.symbols.SymbolSchedulingRule;
import org.rubypeople.rdt.internal.core.util.ShamJobScheduler;
import org.rubypeople.rdt.internal.ui.symbols.BlockingSymbolFinder.FindExactSymbolJob;
import org.rubypeople.rdt.internal.ui.symbols.BlockingSymbolFinder.FindInexactSymbolJob;


public class TC_BlockingSymbolFinder extends TestCase {


    private BlockingSymbolFinder finder;
    private ShamJobScheduler scheduler;
    private ShamSymbolIndex index;

    public void setUp() {
        scheduler = new ShamJobScheduler();
        index = new ShamSymbolIndex();
        finder = new BlockingSymbolFinder(index, scheduler);
    }
    
    public void testFindExact() throws Exception {
        TestSymbol symbol = new TestSymbol();
        finder.find(symbol);
        
        FindExactSymbolJob expectedJob = new FindExactSymbolJob(index, symbol);
        setExpectedJobOptions(expectedJob);
        scheduler.assertScheduled(expectedJob);
    }

    public void testFindInexact() throws Exception {
        finder.find("pattern", 17);
        
        FindInexactSymbolJob  expectedJob = new FindInexactSymbolJob(index, "pattern", 17);
        setExpectedJobOptions(expectedJob);
        scheduler.assertScheduled(expectedJob);
    }

    private void setExpectedJobOptions(Job expectedJob) {
        expectedJob.setRule(new SymbolSchedulingRule());
        expectedJob.setSystem(false);
        expectedJob.setUser(true);
        expectedJob.setPriority(Job.INTERACTIVE);
    }

    private static class TestSymbol extends Symbol {

        public TestSymbol() {
            super(null, 0);
        }
        
        public boolean equals(Object obj) {
            return this == obj;
        }
        
        public int hashCode() {
            return 0;
        }
    }
}
