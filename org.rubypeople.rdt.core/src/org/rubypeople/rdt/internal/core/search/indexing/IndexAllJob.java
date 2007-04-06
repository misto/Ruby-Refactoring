package org.rubypeople.rdt.internal.core.search.indexing;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyModelManager;

public class IndexAllJob extends Job {
		private IndexManager index;

		public IndexAllJob(IndexManager index) {
			super("Search Index Job");
			this.index = index;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			// TODO Load up saved data if there is any, rather than starting
			// over
			// TODO Clear saved state if user cleans a project
			// TODO Save state after a run
			IRubyModel model = RubyModelManager.getRubyModelManager().getRubyModel();
			addChildren(model);
			return Status.OK_STATUS;
		}

		private void addChildren(IParent parent) {
			try {
				IRubyElement[] children = parent.getChildren();
				for (int i = 0; i < children.length; i++) {
					index.addElement(children[i]);
					if (children[i] instanceof IParent) {
						IParent newParent = (IParent) children[i];
						addChildren(newParent);
					}
				}
			} catch (RubyModelException e) {
				RubyCore.log(e);
			}
		}

	}

