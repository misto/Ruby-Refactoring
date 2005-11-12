package org.rubypeople.rdt.internal.core.builder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.eclipse.shams.resources.ShamFolder;
import org.rubypeople.eclipse.shams.resources.ShamProject;
import org.rubypeople.eclipse.shams.runtime.ShamMonitor;
import org.rubypeople.rdt.internal.core.symbols.SymbolIndex;
import org.rubypeople.rdt.internal.core.util.ListUtil;

public abstract class AbstractRdtTestCase extends TestCase {
    static protected final String EXPECTED_TASK_NAME = "Removing Markers...";

    protected abstract void assertMarkersRemoved(List expectedFiles);
    protected abstract void assertIndexFlushed(List expectedFiles);
    abstract AbstractRdtCompiler createCompiler(SymbolIndex index, 
            IMarkerManager markerManager, List singleCompilers);

    protected ShamMonitor monitor;
    protected ShamFile t1;
    protected ShamFile t2;
    protected ShamFile t3;
    protected ShamFolder f1;
    protected ShamProject project;
    protected ShamMarkerManager markerManager;
    protected ShamSymbolIndex symbolIndex;
    protected ShamSingleCompiler singleCompiler1;
    protected ShamSingleCompiler singleCompiler2;
    protected AbstractRdtCompiler compiler;
    private ShamFile nonRubyFile;

    public void setUp() {
        t1 = new ShamFile("/test/T1.rb");
        t2 = new ShamFile("/test/T2.rb");
        t3 = new ShamFile("/test/F1/T3.rb");
        nonRubyFile = new ShamFile("/test/T3.txt");
        f1 = new ShamFolder("/test/F1");
        project = new ShamProject("test");
    
        markerManager = new ShamMarkerManager();
        symbolIndex = new ShamSymbolIndex();
        singleCompiler1 = new ShamSingleCompiler();
        singleCompiler2 = new ShamSingleCompiler();
        List singleCompilers = ListUtil.create(singleCompiler1, singleCompiler2);
        
        compiler = createCompiler(symbolIndex, markerManager, singleCompilers);
        monitor = new ShamMonitor();
    }


    public void testBasicCompile() throws Exception {
        project.addResource(t1);
        setFiles(ListUtil.create(t1));
        
        compiler.compile(monitor);
    
        assertCompliationFor(ListUtil.create(t1), 4);
    }

    public void testNotARubyFile() throws Exception {
        project.addResource(nonRubyFile);
        setFiles(ListUtil.create(nonRubyFile));
        
        compiler.compile(monitor);
    
        assertCompliationFor(ListUtil.create(), 2);
    }

    public void testCompileIncludesFolders() throws Exception {
        project.addResource(f1);
        f1.addResource(t3);
        setFiles(ListUtil.create(f1));
        
        compiler.compile(monitor);
        
        assertCompliationFor(ListUtil.create(t3), 4);
    }

    public void testCompileMultipleFiles() throws Exception {
        project.addResource(t1);
        project.addResource(t2);
        setFiles(ListUtil.create(t1, t2));
    
        compiler.compile(monitor);
    
        assertCompliationFor(ListUtil.create(t1, t2), 6);
    }

    public void testCancellation() throws Exception {
        monitor.cancelAfter(4);
        project.addResource(t1);
        project.addResource(t2);
        setFiles(ListUtil.create(t1, t2));
    
        compiler.compile(monitor);
        List expectedFiles = ListUtil.create(t1);
    
        monitor.assertTaskBegun("Building test...", 6);
        monitor.assertDone(4);
        List subTasks = ListUtil.create(EXPECTED_TASK_NAME, 
                t1.getFullPath().toString());
        monitor.assertSubTasks(subTasks);
        assertMarkersRemoved(ListUtil.create(t1,t2));
        assertIndexFlushed(ListUtil.create(t1,t2));
        singleCompiler1.assertCompiled(new HashSet(expectedFiles));
        singleCompiler2.assertCompiled(new HashSet(expectedFiles));
    }

    public void testCompileSkipsNonRubyFiles() throws Exception {
        ShamFile x1 = new ShamFile("/test/x1");
        project.addResource(t1);
        project.addResource(x1);
        setFiles(ListUtil.create(t1));
    
        compiler.compile(monitor);
    
        assertCompliationFor(ListUtil.create(t1), 4);
    }

    protected void setFiles(List filesForTest) throws Exception {
    }
    
    protected void assertCompliationFor(List expectedFiles, int totalWork) {
        monitor.assertTaskBegun("Building test...", totalWork);
        monitor.assertDone(totalWork);
        List subTasks = ListUtil.create(EXPECTED_TASK_NAME);
        for (Iterator iter = expectedFiles.iterator(); iter.hasNext();) {
            IFile file = (IFile) iter.next();
            subTasks.add(file.getFullPath().toString());
        }
    
        monitor.assertSubTasks(subTasks);
        assertMarkersRemoved(expectedFiles);
        assertIndexFlushed(expectedFiles);
        singleCompiler1.assertCompiled(new HashSet(expectedFiles));
        singleCompiler2.assertCompiled(new HashSet(expectedFiles));
    }

}
