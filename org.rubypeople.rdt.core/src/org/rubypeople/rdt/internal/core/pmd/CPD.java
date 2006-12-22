package org.rubypeople.rdt.internal.core.pmd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;

public class CPD {
	
	private Map<String, SourceCode> source = new HashMap<String, SourceCode>();
    private int minimumTileSize;
	private Language language;
	private boolean skipDuplicates;
	private MatchAlgorithm matchAlgorithm;
	private Tokens tokens = new Tokens();
	private CPDListener listener = new CPDNullListener();    
    private Set<String> current = new HashSet<String>();

	private CPD(int minimumTileSize, Language language) {
        this.minimumTileSize = minimumTileSize;
        this.language = language;
    }
	
	public static Iterator<Match> findMatches(IProject project) throws IOException {
		 boolean skipDuplicateFiles = true;
         int minimumTokens = 5;
         Language language = new RubyLanguage();

         CPD cpd = new CPD(minimumTokens, language);
         if (skipDuplicateFiles) {
             cpd.skipDuplicates();
         }
         cpd.addRecursively(project.getLocation().toOSString());
         cpd.go();
         return cpd.getMatches();
	}
	
    private void go() {
        TokenEntry.clearImages();
        matchAlgorithm = new MatchAlgorithm(source, tokens, minimumTileSize, listener);
        matchAlgorithm.findMatches();
    }
    
    private void skipDuplicates() {
        this.skipDuplicates = true;
    }
    
    private Iterator<Match> getMatches() {
        return matchAlgorithm.matches();
    }
    
    private void addRecursively(String dir) throws IOException {
        addDirectory(dir, true);
    }
    
    private void addDirectory(String dir, boolean recurse) throws IOException {
        if (!(new File(dir)).exists()) {
            throw new FileNotFoundException("Couldn't find directory " + dir);
        }
        FileFinder finder = new FileFinder();
        // TODO - could use SourceFileSelector here
        add(finder.findFilesFrom(dir, language.getFileFilter(), recurse));
    }
    
    private void add(List files) throws IOException {
        for (Iterator i = files.iterator(); i.hasNext();) {
            add(files.size(), (File) i.next());
        }
    }
    
    private void add(int fileCount, File file) throws IOException {

        if (skipDuplicates) {
            // TODO refactor this thing into a separate class
            String signature = file.getName() + '_' + file.length();
            if (current.contains(signature)) {
                System.out.println("Skipping " + file.getAbsolutePath() + " since it appears to be a duplicate file and --skip-duplicate-files is set");
                return;
            }
            current.add(signature);
        }

        if (!file.getCanonicalPath().equals(file.getAbsolutePath())) {
            System.out.println("Skipping " + file + " since it appears to be a symlink");
            return;
        }

        listener.addedFile(fileCount, file);
        SourceCode sourceCode = new SourceCode(new SourceCode.FileCodeLoader(file));
        language.getTokenizer().tokenize(sourceCode, tokens);
        source.put(sourceCode.getFileName(), sourceCode);
    }

}
