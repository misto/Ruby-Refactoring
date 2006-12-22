package org.rubypeople.rdt.internal.ui.text.hyperlinks;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IEditorInput;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.codeassist.SelectionEngine;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.text.hyperlinks.IHyperlinkProvider;

public class RubyHyperLinkDetector implements IHyperlinkDetector {

       public static final String RDT_UI_NAMESPACE = "org.rubypeople.rdt.ui";
       public static final String RDT_UI_HYPERLINKPROVIDER = "hyperlinkProvider";


       private List fExtensions;
       private final IEditorInput fEditorInput;


       public RubyHyperLinkDetector(IEditorInput editorInput) {
               this.fEditorInput = editorInput;
       }

       private List initExtensions() {
               if(fExtensions == null){
                       fExtensions = new ArrayList();
                       IExtensionRegistry reg = Platform.getExtensionRegistry();
                       IExtensionPoint[] points = reg.getExtensionPoints(RDT_UI_NAMESPACE);
                       // TODO: Look for textProvider!
                       IExtensionPoint point = null;

                       if(points != null){
                               for (int i = 0; i < points.length; i++) {
                                       IExtensionPoint currentPoint = points[i];
                                       String uniqueIdentifier = currentPoint.getUniqueIdentifier();
                                       if(uniqueIdentifier.endsWith(RDT_UI_HYPERLINKPROVIDER)){
                                               point = currentPoint;
                                               break;
                                       }
                               }

                               if(point != null){
                                       IExtension[] exts = point.getExtensions();

                                       IHyperlinkProvider prov = null;

                                       for (int i = 0; i < exts.length; i++) {
                                               IConfigurationElement[] elem = exts[i].getConfigurationElements();
                                               String attrs[] = elem[0].getAttributeNames();
                                               try {
                                                       Object tempProv = elem[0].createExecutableExtension("class");
                                                       if (tempProv instanceof IHyperlinkProvider) {
                                                               prov = (IHyperlinkProvider) tempProv;
                                                               fExtensions.add(prov);
                                                       }
//                                                     }
                                               } catch (Exception e) {
                                                       RubyPlugin.log(e);
                                               }

                                       }
                               }
                       }

               }
               return fExtensions;

       }



       public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
                       IRegion region, boolean canShowMultipleHyperlinks) {
               String symbol = "";
               IRegion newRegion = region;
//             newRegion = RubyWordFinder.findWord(textViewer.getDocument(), region.getOffset());
//             try {
//                     symbol = textViewer.getDocument().get(newRegion.getOffset(), newRegion.getLength());
//             } catch (BadLocationException e) {
//                     // TODO Auto-generated catch block
//                     e.printStackTrace();
//             }
//             System.out.println("Symbol:" + symbol);
//             return new IHyperlink[]{new RubyHyperLinkDetector(fEditorInput, newRegion, symbol)};

               List extensions = initExtensions();
//             final String symbol = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
               // first ask the extensions
               if(extensions.size() > 0){
                       SelectionEngine engine = new SelectionEngine();
                       IWorkingCopyManager manager = RubyPlugin.getDefault().getWorkingCopyManager();
                       IRubyScript script = manager.getWorkingCopy(fEditorInput);
                       RubyParser parser = new RubyParser();
                       try {
                               Node root = parser.parse((IFile) script.getResource(),new StringReader(script.getSource()));
                       //                              IRubyElement[] elements = engine.select(script, newRegion.getOffset(), newRegion.getOffset() + newRegion.getLength());
                               for(int i=0; i< extensions.size(); i++){
                                       IHyperlinkProvider currentProvider = (IHyperlinkProvider) extensions.get(i);
                                       IHyperlink link = currentProvider.getHyperlink(fEditorInput, textViewer, root, newRegion, true);
                                       // TODO: either do that or query all HyperlinkProviders and return a list of hyperlinks?
                                       if(link != null){
                                               return new IHyperlink[]{link};
                                       }
                               }
                       } catch (RubyModelException e) {
                               RubyPlugin.log(e);
                       }
               }

               return null;

       }

}