package org.rubypeople.rdt.internal.ui.text.correction;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.IMarkerHelpRegistry;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.IRubyAnnotation;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.text.ruby.IQuickFixProcessor;

public class RubyCorrectionProcessor implements org.eclipse.jface.text.quickassist.IQuickAssistProcessor {

	private static final String QUICKFIX_PROCESSOR_CONTRIBUTION_ID= "quickFixProcessors"; //$NON-NLS-1$

	private static ContributedProcessorDescriptor[] fContributedCorrectionProcessors= null;
	
	private String fErrorMessage;
	
	public boolean canAssist(IQuickAssistInvocationContext invocationContext) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canFix(Annotation annotation) {
		return hasCorrections(annotation);
	}

	public ICompletionProposal[] computeQuickAssistProposals(IQuickAssistInvocationContext invocationContext) {
		// TODO Auto-generated method stub
		return new ICompletionProposal[0];
	}

	public String getErrorMessage() {
		return fErrorMessage;
	}
	
	public static boolean hasCorrections(Annotation annotation) {
		if (annotation instanceof IRubyAnnotation) {
			IRubyAnnotation rubyAnnotation= (IRubyAnnotation) annotation;
			int problemId= rubyAnnotation.getId();
			if (problemId != -1) {
				IRubyScript cu= rubyAnnotation.getRubyScript();
				if (cu != null) {
					return hasCorrections(cu, problemId, rubyAnnotation.getMarkerType());
				}
			}
		}
		if (annotation instanceof SimpleMarkerAnnotation) {
			return hasCorrections(((SimpleMarkerAnnotation) annotation).getMarker());
		}
		return false;
	}
	
	private static boolean hasCorrections(IMarker marker) {
		if (marker == null || !marker.exists())
			return false;

		IMarkerHelpRegistry registry= IDE.getMarkerHelpRegistry();
		return registry != null && registry.hasResolutions(marker);
	}
	
	public static boolean hasCorrections(IRubyScript cu, int problemId, String markerType) {
		ContributedProcessorDescriptor[] processors= getCorrectionProcessors();
		SafeHasCorrections collector= new SafeHasCorrections(cu, problemId);
		for (int i= 0; i < processors.length; i++) {
			if (processors[i].canHandleMarkerType(markerType)) {
				collector.process(processors[i]);
				if (collector.hasCorrections()) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static ContributedProcessorDescriptor[] getCorrectionProcessors() {
		if (fContributedCorrectionProcessors == null) {
			fContributedCorrectionProcessors= getProcessorDescriptors(QUICKFIX_PROCESSOR_CONTRIBUTION_ID, true);
		}
		return fContributedCorrectionProcessors;
	}
	
	private static ContributedProcessorDescriptor[] getProcessorDescriptors(String contributionId, boolean testMarkerTypes) {
		IConfigurationElement[] elements= Platform.getExtensionRegistry().getConfigurationElementsFor(RubyUI.ID_PLUGIN, contributionId);
		ArrayList res= new ArrayList(elements.length);

		for (int i= 0; i < elements.length; i++) {
			ContributedProcessorDescriptor desc= new ContributedProcessorDescriptor(elements[i], testMarkerTypes);
			IStatus status= desc.checkSyntax();
			if (status.isOK()) {
				res.add(desc);
			} else {
				RubyPlugin.log(status);
			}
		}
		return (ContributedProcessorDescriptor[]) res.toArray(new ContributedProcessorDescriptor[res.size()]);
	}
	
	private static class SafeHasCorrections extends SafeCorrectionProcessorAccess {
		private final IRubyScript fCu;
		private final int fProblemId;
		private boolean fHasCorrections;

		public SafeHasCorrections(IRubyScript cu, int problemId) {
			fCu= cu;
			fProblemId= problemId;
			fHasCorrections= false;
		}

		public boolean hasCorrections() {
			return fHasCorrections;
		}

		public void safeRun(ContributedProcessorDescriptor desc) throws Exception {
			IQuickFixProcessor processor= (IQuickFixProcessor) desc.getProcessor(fCu);
			if (processor != null && processor.hasCorrections(fCu, fProblemId)) {
				fHasCorrections= true;
			}
		}
	}
	
	private static abstract class SafeCorrectionProcessorAccess implements ISafeRunnable {
		private MultiStatus fMulti= null;
		private ContributedProcessorDescriptor fDescriptor;

		public void process(ContributedProcessorDescriptor[] desc) {
			for (int i= 0; i < desc.length; i++) {
				fDescriptor= desc[i];
				SafeRunner.run(this);
			}
		}

		public void process(ContributedProcessorDescriptor desc) {
			fDescriptor= desc;
			SafeRunner.run(this);
		}

		public void run() throws Exception {
			safeRun(fDescriptor);
		}

		protected abstract void safeRun(ContributedProcessorDescriptor processor) throws Exception;

		public void handleException(Throwable exception) {
			if (fMulti == null) {
				fMulti= new MultiStatus(RubyUI.ID_PLUGIN, IStatus.OK, CorrectionMessages.RubyCorrectionProcessor_error_status, null);
			}
			fMulti.merge(new Status(IStatus.ERROR, RubyUI.ID_PLUGIN, IStatus.ERROR, CorrectionMessages.RubyCorrectionProcessor_error_status, exception));
		}

		public IStatus getStatus() {
			if (fMulti == null) {
				return Status.OK_STATUS;
			}
			return fMulti;
		}

	}

}
