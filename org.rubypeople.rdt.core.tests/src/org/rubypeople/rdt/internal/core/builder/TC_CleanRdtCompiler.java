package org.rubypeople.rdt.internal.core.builder;

import java.util.List;


public class TC_CleanRdtCompiler extends AbstractRdtTestCase {

    AbstractRdtCompiler createCompiler(IMarkerManager markerManager, List singleCompilers) {
        return new CleanRdtCompiler(project, 
                markerManager, singleCompilers);
    }

    protected void assertMarkersRemoved(List expectedFiles) {
        markerManager.assertMarkersRemovedFor(project);
    }
}
