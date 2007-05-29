package org.rubypeople.rdt.internal.ui.text.ruby.hover;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.launching.RubyRuntime;

public class RiDocHoverProvider extends AbstractRubyEditorTextHover {
	
	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		File ri = RubyRuntime.getRI();
    	if (ri == null || !ri.exists() || !ri.isFile()) return null;
    	
    	List<String> args = new ArrayList<String>();
    	args.add(0, ri.getAbsolutePath());
    	// these will get rid of some of the overhead formatting
    	args.add("-f");
    	args.add("html");
    	args.add("--no-pager");
    	
    	BufferedReader br = null; 
    	try {
			String symbol = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
			args.add(symbol);
			String[] argArray= (String[]) args.toArray(new String[args.size()]);
			Process p = Runtime.getRuntime().exec(argArray);
			if (p == null) return null;
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			// TODO: format the documentation that was fetched from RI 
			// for now: read the first 15 lines so 
			StringBuffer buf = new StringBuffer();
			for(int i = 0; i < 15; i++){
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
			return "" + buf.toString();			
    	} catch (BadLocationException e) {
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
