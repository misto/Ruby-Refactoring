package org.rubypeople.rdt.internal.ui.text.ruby.hover;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.rubypeople.rdt.core.ICodeAssist;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;
import org.rubypeople.rdt.launching.RubyRuntime;

public class RiDocHoverProvider extends AbstractRubyEditorTextHover {
	
	/*
	 * @see ITextHover#getHoverInfo(ITextViewer, IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		
		/*
		 * The region should be a word region an not of length 0.
		 * This check is needed because codeSelect(...) also finds
		 * the Ruby element if the offset is behind the word.
		 */
		if (hoverRegion.getLength() == 0)
			return null;
		
		ICodeAssist resolve= getCodeAssist();
		if (resolve != null) {
			try {
				IRubyElement[] result= resolve.codeSelect(hoverRegion.getOffset(), hoverRegion.getLength());
				if (result != null && result.length > 0) {
					return getHoverInfo(result);
				}
			} catch (RubyModelException x) {
				return null;
			}
		}
		try {
			IDocumentExtension3 extension = (IDocumentExtension3)textViewer.getDocument();			
			String contentType = null;
			try {
				contentType = extension.getContentType(IRubyPartitions.RUBY_PARTITIONING, hoverRegion.getOffset(), false);
			} catch (BadPartitioningException e) {
				// ignore
			}
			if (contentType != null && (contentType.equals(IRubyPartitions.RUBY_MULTI_LINE_COMMENT) || contentType.equals(IRubyPartitions.RUBY_SINGLE_LINE_COMMENT))) {
				return null;
			}
			String symbol = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());	
			if (symbol != null && (symbol.startsWith("@") || symbol.startsWith("$") || symbol.startsWith(":"))) return null; // don't try class/instance/global variables or symbols
			return getRIResult(symbol);
		} catch (BadLocationException e) {
			// ignore
		}
		return null;
	}
	
	
	private String getRIResult(String symbol) {
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
			args.add('"' + symbol + '"');
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
	
	@Override
	protected String getHoverInfo(IRubyElement[] rubyElements) {
		if (rubyElements == null || rubyElements.length == 0) return null;
		String symbol = getRICompatibleName(rubyElements[0]);
		if (symbol == null) return null;
		return getRIResult(symbol);
	}

	private String getRICompatibleName(IRubyElement element) {
		switch (element.getElementType()) {
		case IRubyElement.TYPE:
			return element.getElementName();
		case IRubyElement.METHOD:
			IMethod method = (IMethod) element;
			String delimeter = method.isSingleton() ? "::" : "#";
			return method.getDeclaringType().getElementName() + delimeter + element.getElementName();

		default:
			return null;
		}
	}
}
