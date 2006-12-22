package org.rubypeople.rdt.internal.ui.text.hyperlinks;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.codeassist.SelectionEngine;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.RubyWordFinder;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.text.hyperlinks.IHyperlinkProvider;

public class RubyElementsHyperlinkProvider implements IHyperlinkProvider {

       public RubyElementsHyperlinkProvider (){}

       class RubyElementsHyperlink implements IHyperlink {
               private IRegion fRegion;
               private final IEditorInput fEditorInput;
               private final IRubyElement[] fElements;

               public RubyElementsHyperlink(IEditorInput editorInput, IRegion region, String symbol, IRubyElement[] elements) {
                       this.fEditorInput = editorInput;
//                     fRegion = new Region(region.getOffset(), 5);
                       fRegion = region;
                       this.fElements = elements;
               }

               public IRegion getHyperlinkRegion() {
                       return fRegion;
               }

               public String getHyperlinkText() {
                       // TODO Auto-generated method stub
                       return null;
               }

               public String getTypeLabel() {
                       // TODO Auto-generated method stub
                       return null;
               }


               public void open() {
                       try {
                               // FIXME Check for first element which is an instanceof of IMember, don't just try to access the first element!
                               if(fElements != null && fElements.length > 0){
                                       ISourceRange sourceRange = ((IMember) fElements[0]).getSourceRange();
                                       IFile file = null;
                                       if (fEditorInput instanceof IFileEditorInput) {
                                               IFileEditorInput fileInput = (IFileEditorInput) fEditorInput;
                                               file = fileInput.getFile();
                                       }
                                       openFileAndLocation(sourceRange, file);
                               }
                       } catch (PartInitException e) {
                               RubyPlugin.log(e);
                       } catch (RubyModelException e) {
                               RubyPlugin.log(e);
                       } catch (CoreException e) {
                               RubyPlugin.log(e);
                       }
               }

               private void openFileAndLocation(ISourceRange sourceRange, IFile file)
               throws PartInitException, CoreException {
                       if (file == null) {
                               return;
                       }
                       if (sourceRange == null) {
                               return;
                       }
                       IEditorPart editorPart = IDE.openEditor(PlatformUI.getWorkbench()
                                       .getActiveWorkbenchWindow().getActivePage(), file, true);

                       IMarker mark = file.createMarker(IMarker.TEXT);
                       mark.setAttribute(IMarker.CHAR_START, sourceRange.getOffset());
                       mark.setAttribute(IMarker.CHAR_END, sourceRange.getOffset()
                                       + sourceRange.getLength());
                       IDE.gotoMarker(editorPart, mark);
                       mark.delete();
                       IDE.gotoMarker(editorPart, mark);
               }

       }


       public IHyperlink getHyperlink(IEditorInput input, ITextViewer textViewer, Node node,
                       IRegion region, boolean canShowMultipleHyperlinks) {
               IRegion newRegion = RubyWordFinder.findWord(textViewer.getDocument(), region.getOffset());
               try {
                       String symbol = textViewer.getDocument().get(newRegion.getOffset(), newRegion.getLength());
                       // Let's see if this is a RubyElement
                       SelectionEngine engine = new SelectionEngine();
                       IWorkingCopyManager manager = RubyPlugin.getDefault().getWorkingCopyManager();
                       IRubyScript script = manager.getWorkingCopy(input);
                       IRubyElement[] elements = engine.select(script, newRegion.getOffset(), newRegion.getOffset() + newRegion.getLength());
                       if(elements == null){
                               return null;
                       }
                       if(elements.length > 0){
                               // TODO: check if it's a RubyElement, if not, return null
                               return new RubyElementsHyperlink(input, newRegion, symbol, elements);
                       }
                       return null;
               } catch (Exception e) {
                       RubyPlugin.log(e);
               }
               return null;
       }
}