package org.rubypeople.rdt.core.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;


public class RubyContentDescriber implements ITextContentDescriber {

    /*
     *  (non-Javadoc)
     * @see org.eclipse.core.runtime.content.ITextContentDescriber#describe(java.io.Reader, org.eclipse.core.runtime.content.IContentDescription)
     */
    public int describe(Reader contents, IContentDescription description) throws IOException {
        BufferedReader reader = new BufferedReader(contents);
        // Skip all empty lines and find first line with characters
        String firstLine = "";
        while(firstLine.trim().length() == 0) {
            firstLine = reader.readLine();
            if (firstLine == null) return ITextContentDescriber.INVALID;
        }
        if (firstLine.indexOf("ruby") > -1 && firstLine.indexOf("#!") > -1) 
            return ITextContentDescriber.VALID;
        return ITextContentDescriber.INVALID;
    }

    /*
     *  (non-Javadoc)
     * @see org.eclipse.core.runtime.content.IContentDescriber#describe(java.io.InputStream, org.eclipse.core.runtime.content.IContentDescription)
     */
    public int describe(InputStream contents, IContentDescription description) throws IOException {
        Reader reader = new InputStreamReader(contents);
        return describe(reader, description);
    }

    /*
     *  (non-Javadoc)
     * @see org.eclipse.core.runtime.content.IContentDescriber#getSupportedOptions()
     */
    public QualifiedName[] getSupportedOptions() {
        // TODO Auto-generated method stub
        return null;
    }

}

