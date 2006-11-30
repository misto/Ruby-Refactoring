package org.rubypeople.rdt.ui.text.hyperlinks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyType;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.RubyWordFinder;
import org.rubypeople.rdt.ui.IWorkingCopyManager;

public class RubyHyperLinkDetector implements IHyperlinkDetector {

	private final IEditorInput fEditorInput;

	public class RubyHyperlink implements IHyperlink {

		private IRegion fRegion;
		private final String fSymbol;
		private final IEditorInput fEditorInput;

		public RubyHyperlink(IEditorInput editorInput, IRegion region,
				String symbol) {
			this.fEditorInput = editorInput;
			this.fSymbol = symbol;
			// fRegion = new Region(region.getOffset(), 5);
			fRegion = region;
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
			String symbol = fSymbol;
			// IProject[] rubyProjects = RubyCore.getRubyProjects();
			IProject proj = null;
			// for (int i = 0; i < rubyProjects.length; i++) {
			// IProject project = rubyProjects[i];
			// if(project.getName().equals("RubyFormatter")){
			// try {
			// project.open(null);
			// } catch (CoreException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// proj = project;
			// break;
			// }
			// }
			if (fEditorInput instanceof IFileEditorInput) {
				IFileEditorInput fileInput = (IFileEditorInput) fEditorInput;
				proj = fileInput.getFile().getProject();
			}

			if (proj != null) {
				try {
					proj.open(null);
				} catch (CoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				IRubyProject rproject = RubyCore.create(proj);
				IType type;
				try {
					type = rproject.findType(symbol);
					if (type != null) {
						IResource resource = type.getResource();
						// TODO: use getSource to do a source view hover!
						ISourceRange sourceRange = type.getSourceRange();
						IFile file = proj.getFile(resource
								.getProjectRelativePath());
						openFileAndLocation(sourceRange, file);
					} else {
						// choose a method
						// get the types in the file
						IWorkingCopyManager manager = RubyPlugin.getDefault()
								.getWorkingCopyManager();
						IRubyScript rubyscript = manager
								.getWorkingCopy(fEditorInput);
						IType[] types = rubyscript.getTypes();

						List theTypes = assembleTypesAroundOffset(types);
						// Look for the methods with the name fSymbol
						// Primitive: this'll take the first one it finds ->
						// proof of concept, needs refinement;
						for (Iterator iterator = theTypes.iterator(); iterator
								.hasNext();) {
							RubyType currType = (RubyType) iterator.next();
							IMethod[] methods = currType.getMethods();
							for (int i = 0; i < methods.length; i++) {
								IMethod method = methods[i];
								if (method.getElementName().equals(fSymbol)) {
									ISourceRange sourceRange = method
											.getSourceRange();
									if (fEditorInput instanceof IFileEditorInput) {
										IFileEditorInput fileInput = (IFileEditorInput) fEditorInput;
										IFile file = fileInput.getFile();
										openFileAndLocation(sourceRange, file);
									}
									break;
								}
							}
						}
					}
				} catch (RubyModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("Hyperlink!!!");

			}
		}

		private List assembleTypesAroundOffset(IType[] types)
				throws RubyModelException {
			List retValue = new ArrayList();
			for (int i = 0; i < types.length; i++) {
				IType currType = types[i];
				if (fRegion.getOffset() >= currType.getSourceRange()
						.getOffset()) {
					if (fRegion.getOffset() + fRegion.getLength() <= currType
							.getSourceRange().getOffset()
							+ currType.getSourceRange().getLength()) {
						retValue.add(currType);
						if (currType.isModule()) {
							IRubyElement[] children = currType.getChildren();
							// ITypes newTypes =
							List newTypes = new ArrayList();
							for (int j = 0; j < children.length; j++) {
								IRubyElement element = children[j];
								if (element instanceof RubyType) {
									newTypes.add(element);
								}
							}
							retValue
									.addAll(assembleTypesAroundOffset((IType[]) newTypes
											.toArray(new IType[newTypes.size()])));
						} else {
							retValue.add(currType);
						}
					}
				}
			}
			return retValue;
		}

		private void openFileAndLocation(ISourceRange sourceRange, IFile file)
				throws PartInitException, CoreException {
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

	public RubyHyperLinkDetector(IEditorInput editorInput) {
		this.fEditorInput = editorInput;

	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		String symbol = "";
		IRegion newRegion = region;
		newRegion = RubyWordFinder.findWord(textViewer.getDocument(), region
				.getOffset());
		try {
			symbol = textViewer.getDocument().get(newRegion.getOffset(),
					newRegion.getLength());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("Symbol:" + symbol);
		return new IHyperlink[] { new RubyHyperlink(fEditorInput, newRegion,
				symbol) };
	}

}
