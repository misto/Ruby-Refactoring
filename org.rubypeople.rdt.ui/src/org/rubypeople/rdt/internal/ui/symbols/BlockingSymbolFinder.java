package org.rubypeople.rdt.internal.ui.symbols;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.rubypeople.rdt.internal.core.symbols.ISymbolFinder;
import org.rubypeople.rdt.internal.core.symbols.Symbol;
import org.rubypeople.rdt.internal.core.symbols.SymbolSchedulingRule;

// DSC UNIT TEST
public class BlockingSymbolFinder implements ISymbolFinder {

    private final ISymbolFinder delegate;

    public BlockingSymbolFinder(ISymbolFinder symbolFinder) {
        this.delegate = symbolFinder;
    }

    public Set find(final Symbol symbol) {
        FindSymbolJob job = new FindExactSymbolJob(delegate, symbol);
        return job.executeJob();
    }

    public Set find(String regExp, int symbolType) throws PatternSyntaxException {
        FindSymbolJob job = new FindInexactSymbolJob("find " + regExp, delegate, regExp, symbolType);
        return job.executeJob();
    }
    
    private static abstract class FindSymbolJob extends Job {

        protected Set locations = new HashSet();
        private PatternSyntaxException exception;

        public FindSymbolJob(String name) {
            super(name);
            setPriority(Job.INTERACTIVE);
            setUser(true);
            setRule(new SymbolSchedulingRule());
        }

        protected abstract Set find() throws PatternSyntaxException;

        private Set executeJob() {
            schedule();
            
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (exception != null)
                throw exception;
            return locations;
        }

        protected IStatus run(IProgressMonitor monitor) {
            try {
                locations = find();
            } catch (PatternSyntaxException e) {
                exception  = e;
            }
            return Status.OK_STATUS;
        }
        

    }

    private static class FindInexactSymbolJob extends FindSymbolJob {
        private final ISymbolFinder delegate;
        private final String regExp;
        private final int symbolType;

        private FindInexactSymbolJob(String name, ISymbolFinder delegate, String regExp, int symbolType) {
            super(name);
            this.delegate = delegate;
            this.regExp = regExp;
            this.symbolType = symbolType;
        }

        protected Set find() {
            return delegate.find(regExp, symbolType);
        }
    }


    private static class FindExactSymbolJob extends FindSymbolJob {
        private final ISymbolFinder delegate;

        private final Symbol symbol;

        private FindExactSymbolJob(ISymbolFinder delegate, Symbol symbol) {
            super("find "+symbol);
            this.delegate = delegate;
            this.symbol = symbol;
        }

        protected Set find() {
            return delegate.find(symbol);
        }
    }


    public Set find() {
        // TODO Auto-generated method stub
        return null;
    }

}
