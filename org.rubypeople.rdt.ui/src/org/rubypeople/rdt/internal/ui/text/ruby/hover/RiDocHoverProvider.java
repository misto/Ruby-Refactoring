package org.rubypeople.rdt.internal.ui.text.ruby.hover;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.rubypeople.rdt.internal.launching.RubyInterpreter;
import org.rubypeople.rdt.internal.launching.RubyRuntime;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.extensions.ITextHoverProvider;


public class RiDocHoverProvider implements ITextHoverProvider {
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion){
    	IPath riPath = new Path( RubyPlugin.getDefault().getPreferenceStore().getString( PreferenceConstants.RI_PATH ) );
    	List args = new ArrayList();
    	args.add(0, riPath.toString());
    	BufferedReader br = null; 
    	try {
			String symbol = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
			args.add(symbol);
            RubyInterpreter selectedInterpreter = RubyRuntime.getDefault().getSelectedInterpreter();
			if (selectedInterpreter == null) return null;
            Process p = selectedInterpreter.exec(args, null);
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			// TODO: format the documentation that was fetched from RI 
			// for now: read the first 3 lines (at most) and show them
			StringBuffer buf = new StringBuffer();
			for(int i = 0; i < 3; i++){
				String line = br.readLine();
				if(line != null){
					buf.append(line);
					buf.append("<br />");
				} else {
					break;
				}				
			}
			// If ambiguous, return nothing
			if (buf.indexOf("More than one method matched your request") > -1) return null;
			return "RI: " + buf.toString();			
    	} catch (BadLocationException e) {
    		RubyPlugin.log(e);
		} catch (CoreException e) {
			RubyPlugin.log(e);
		} catch (IOException e) {
			RubyPlugin.log(e);
		} finally {
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					RubyPlugin.log(e);
				}
			}
		}
		
		return null;
	}
}
