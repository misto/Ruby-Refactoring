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
import org.rubypeople.rdt.internal.core.util.IJobScheduler;

public class BlockingSymbolFinder implements ISymbolFinder {

    private final ISymbolFinder delegate;
    private final IJobScheduler scheduler;

    public BlockingSymbolFinder(ISymbolFinder symbolFinder, IJobScheduler scheduler) {
        this.delegate = symbolFinder;
        this.scheduler = scheduler;
    }

    public Set find(final Symbol symbol) {
        FindSymbolJob job = new FindExactSymbolJob(delegate, symbol);
        return job.executeJob(scheduler);
    }

    public Set find(String regExp, int symbolType) throws PatternSyntaxException {
        FindSymbolJob job = new FindInexactSymbolJob( delegate, regExp, symbolType);
        return job.executeJob(scheduler);
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

        private Set executeJob(IJobScheduler scheduler) {
            try {
                scheduler.execute(this);
                if (exception != null)
                    throw exception;
            } catch (InterruptedException e) {
            };
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

        protected boolean propertiesEquals(Job that) {
            return getRule().equals(that.getRule())
            && isSystem()== that.isSystem()
            && isUser() == that.isUser()
            && getPriority() == that.getPriority();
         }
        

    }

    public static class FindInexactSymbolJob extends FindSymbolJob {
        private final ISymbolFinder delegate;
        private final String regExp;
        private final int symbolType;

        public FindInexactSymbolJob(ISymbolFinder delegate, String regExp, int symbolType) {
            super("find " + regExp);
            this.delegate = delegate;
            this.regExp = regExp;
            this.symbolType = symbolType;
        }

        protected Set find() {
            return delegate.find(regExp, symbolType);
        }
        
        public boolean equals(Object obj) {
            if (!(obj instanceof FindInexactSymbolJob))
                return false;
            
            FindInexactSymbolJob that = (FindInexactSymbolJob) obj;
            return delegate.equals(that.delegate) 
                && regExp.equals(that.regExp)
                && symbolType == that.symbolType
                && propertiesEquals(that);
        }
        
        public int hashCode() {
            return 0;
        }
    }


    public static class FindExactSymbolJob extends FindSymbolJob {
        private final ISymbolFinder delegate;

        private final Symbol symbol;

        public FindExactSymbolJob(ISymbolFinder delegate, Symbol symbol) {
            super("find "+symbol);
            this.delegate = delegate;
            this.symbol = symbol;
        }

        protected Set find() {
            return delegate.find(symbol);
        }
        
        public boolean equals(Object obj) {
            if (!(obj instanceof FindExactSymbolJob))
                return false;
            
            FindExactSymbolJob that = (FindExactSymbolJob) obj;
            return delegate.equals(that.delegate) 
                && symbol.equals(that.symbol)
                && propertiesEquals(that);
        }
        
        public int hashCode() {
            return 0;
        }
    }
}
