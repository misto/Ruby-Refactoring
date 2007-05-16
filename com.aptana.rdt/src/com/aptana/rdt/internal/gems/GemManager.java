package com.aptana.rdt.internal.gems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.internal.resources.XMLWriter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.osgi.service.environment.Constants;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.ui.gems.GemsMessages;

public class GemManager {

	private static final String LOCAL_SWITCH = "-l";
	private static final String LIST_COMMAND = "list";
	private static final String INSTALL_COMMAND = "install";
	private static final String VERSION_SWITCH = "-v";
	private static final String UNINSTALL_COMMAND = "uninstall";
	private static final String UPDATE_COMMAND = "update";
	private static final String EXECUTABLE = "ruby";
	private static final String VM_ARGS = "-e STDOUT.sync=true -e STDERR.sync=true -e load(ARGV.shift)";

	private static final int TIMEOUT = 30000;
	private static final String TIMEOUT_MSG = "Installing gem took more than 30 seconds, intentionally broke out to avoid infinite loop";
	
	private static final String REMOTE_GEMS_CACHE_FILE = "remote_gems.xml";
	private static final String LOCAL_GEMS_CACHE_FILE = "local_gems.xml";
	private static final String GEM_INDEX_URL = "http://gems.rubyforge.org/yaml.Z";

	private static GemManager fgInstance;

	private Set<Gem> gems;
	private Set<Gem> remoteGems;
	private Set<GemListener> listeners;

	private GemManager() {
		gems = new HashSet<Gem>();
		// FIXME Somehow allow user to refresh remote gem list
		// FIXME Do an incremental check for new remote gems somehow?
		remoteGems = new HashSet<Gem>();
		listeners = new HashSet<GemListener>();
		Job job = new Job(GemsMessages.GemManager_loading_remote_gems) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				remoteGems = loadLocalCache(getConfigFile(REMOTE_GEMS_CACHE_FILE));
				if (remoteGems.isEmpty()) {
					remoteGems = loadRemoteGems();
					storeGemCache(remoteGems, getConfigFile(REMOTE_GEMS_CACHE_FILE));
				}
				return Status.OK_STATUS;
			}

		};
		job.schedule();
		Job job2 = new Job(GemsMessages.GemManager_loading_local_gems) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				gems = loadLocalCache(getConfigFile(LOCAL_GEMS_CACHE_FILE));
				if (gems.isEmpty()) {
					gems = loadLocalGems();
					storeGemCache(gems, getConfigFile(LOCAL_GEMS_CACHE_FILE));					
				}
				synchronized (listeners) {
					for (GemListener listener : listeners) {
						listener.gemsRefreshed();
					}
				}
				return Status.OK_STATUS;
			}

		};
		job2.schedule();
	}

	protected Set<Gem> loadLocalCache(File file) {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
			XMLReader reader = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();
			GemManagerContentHandler handler = new GemManagerContentHandler();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(fileReader));

			return handler.getGems();
		} catch (FileNotFoundException e) {
			// This is okay, will get thrown if no config exists yet
		} catch (SAXException e) {
			AptanaRDTPlugin.log(e);
		} catch (ParserConfigurationException e) {
			AptanaRDTPlugin.log(e);
		} catch (FactoryConfigurationError e) {
			AptanaRDTPlugin.log(e);
		} catch (IOException e) {
			AptanaRDTPlugin.log(e);
		} finally {
			try {
				if (fileReader != null)
					fileReader.close();
			} catch (IOException e) {
				// ignore
			}
		}
		return new HashSet<Gem>();
	}

	protected void storeGemCache(Set<Gem> gems, File file) {
		XMLWriter out = null;
		try {
			out = new XMLWriter(new FileOutputStream(file));
			writeXML(gems, out);
		} catch (FileNotFoundException e) {
			AptanaRDTPlugin.log(e);
		} catch (IOException e) {
			AptanaRDTPlugin.log(e);
		} finally {
			if (out != null)
				out.close();
		}
	}

	private File getConfigFile(String fileName) {
		return AptanaRDTPlugin.getDefault().getStateLocation().append(
				fileName).toFile();
	}

	/**
	 * Writes each server configuration to file in XML format.
	 * @param gems 
	 * 
	 * @param out
	 *            the writer to use
	 */
	private void writeXML(Set<Gem> gems, XMLWriter out) {
		out.startTag("gems", null);
		for (Gem gem : gems) {
			out.startTag("gem", null);
			out.printSimpleTag("name", gem.getName());
			out.printSimpleTag("version", gem.getVersion());
			out.printSimpleTag("description", gem.getDescription());
			out.printSimpleTag("platform", gem.getPlatform());
			out.endTag("gem");
		}
		out.endTag("gems");
		out.flush();
	}

	private Set<Gem> loadRemoteGems() {
		
		try {
			List<String> lines = new ArrayList<String>();
			try {
				lines = getContents();
			} catch (DataFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return convertToGems(lines);
		} catch (MalformedURLException e) {
			AptanaRDTPlugin.log(e);
		} catch (IOException e) {
			AptanaRDTPlugin.log(e);
		}
		return new HashSet<Gem>();
	}

	private Set<Gem> convertToGems(List<String> lines) {
		Set<Gem> gems = new HashSet<Gem>();
		String name = null;
		String version = null;
		String description = null;
		String platform = null;
		boolean nextIsRealVersion = false;
		for (String line : lines) {
			if (nextIsRealVersion && line.trim().startsWith("version: ")) {
				version = line.trim().substring(9);
				if (version.charAt(0) == '"')
					version = version.substring(1);
				if (version.charAt(version.length() - 1) == '"')
					version = version.substring(0, version.length() - 1);
				nextIsRealVersion = false;
			} else if (line.trim().equals(
					"version: !ruby/object:Gem::Version")) {
				nextIsRealVersion = true;
			}
			if (line.trim().startsWith("name:")) {
				name = line.trim().substring(6);
			}
			if (line.trim().startsWith("platform:")) {
				if (line.trim().length() == 9) {
					platform = Gem.RUBY_PLATFORM;
				} else {
					platform = line.trim().substring(10);
				}
			}
			if (line.trim().startsWith("summary:")) {
				description = line.trim().substring(9);
			}
			if (description != null && name != null && version != null
					&& platform != null) {
				gems.add(new Gem(name, version, description, platform));
				description = null;
				version = null;
				name = null;
				platform = null;
			}
		}
		return gems;
	}

	private List<String> getContents() throws MalformedURLException, IOException, DataFormatException {
		// XXX Make sure this algorithm is returning same number of gems!!!!!
		List<String> lines = new ArrayList<String>();
		URL url = new URL(GEM_INDEX_URL);
		URLConnection con = url.openConnection();
		InputStream content = (InputStream) con.getContent();
		byte[] input = new byte[1024];
		int index = 0;
		while (true) {
			int bytesToRead = content.available();
			byte[] tmp = new byte[bytesToRead];
			int length = content.read(tmp);
			if (length == -1) break;
			while ((index + length) > input.length) { // if we'll overflow the array, we need to expand it
				byte[] newInput = new byte[input.length * 2];
				System.arraycopy(input, 0, newInput, 0, input.length);
				input = newInput;
			}			
			System.arraycopy(tmp, 0, input, index, length);
			index += length;
		}				
		
//		 Decompress the bytes
		 Inflater decompresser = new Inflater();
		 decompresser.setInput(input);
		 byte[] result = new byte[input.length * 20]; // XXX This is a hack. I have no idea what the length should be here
		 int resultLength = decompresser.inflate(result);
		 decompresser.end();

		 // Decode the bytes into a String
		 String outputString = new String(result, 0, resultLength);
		 String[] lineArray =  outputString.split("\n");	
		 for (int i = 0; i < lineArray.length; i++) {
			 lines.add(lineArray[i]);
		 }
		 return lines;
	}

	private Set<Gem> loadLocalGems() {
		List<String> lines = launchAndRead(LIST_COMMAND + " " + LOCAL_SWITCH);
		if (lines.size() > 2) {
			lines.remove(0); // Remove first 3 lines from local list
			lines.remove(0);
			lines.remove(0);
		}
		return parseOutGems(lines);
	}

	private List<String> launchAndRead(String command) {
		try {
			ILaunchConfiguration config = createGemLaunchConfiguration(command);
			ILaunch launch = config.launch(ILaunchManager.RUN_MODE, null);
			IProcess[] processes = launch.getProcesses();
			IProcess p = processes[0];
			IStreamsProxy proxy = p.getStreamsProxy();
			final StringBuffer output = new StringBuffer();
			IStreamMonitor monitor = proxy.getOutputStreamMonitor();
			monitor.addListener(new IStreamListener() {
				public void streamAppended(String text, IStreamMonitor monitor) {
					output.append(text);
				}

			});
			long start = System.currentTimeMillis();
			String lastOut = null;
			while (!p.isTerminated() || output.length() == 0) {
				Thread.yield();
				if (lastOut != null && !lastOut.equals(output.toString())) {
					start = System.currentTimeMillis(); // restart timeout if we
					// have changes in
					// output
				}
				lastOut = output.toString();				
				if (System.currentTimeMillis() > start + TIMEOUT) {
					AptanaRDTPlugin.log(new Exception(TIMEOUT_MSG));
					break;
				}
			}

			BufferedReader reader = new BufferedReader(new StringReader(output
					.toString()));
			String line = null;
			try {
				List<String> lines = new ArrayList<String>();
				while ((line = reader.readLine()) != null) {
					lines.add(line);
				}
				return lines;
			} catch (IOException e) {
				AptanaRDTPlugin.log(e);
			}

		} catch (CoreException e) {
			AptanaRDTPlugin.log(e);
		}
		return new ArrayList<String>();
	}

	private Set<Gem> parseOutGems(List<String> lines) {
		Set<Gem> gems = new HashSet<Gem>();
		for (int i = 0; i < lines.size();) {
			String nameAndVersion = lines.get(i);
			String description = lines.get(i + 1);
			int j = 2;
			if ((i + 2) < lines.size()) {
				String nextLine = lines.get(i + 2);
				while (nextLine.trim().length() != 0) {
					j++;
					description += " " + nextLine.trim();
					nextLine = lines.get(i + j);
				}
			}
			int openParen = nameAndVersion.indexOf('(');
			int closeParen = nameAndVersion.indexOf(')');
			String name = nameAndVersion.substring(0, openParen);
			String version = nameAndVersion
					.substring(openParen + 1, closeParen);
			gems.add(new Gem(name.trim(), version, description.trim()));
			i += (j + 1);
		}
		return gems;
	}

	public boolean update(Gem gem) {
		try {
			String command = UPDATE_COMMAND + " " + gem.getName();
			ILaunchConfiguration config = createGemLaunchConfiguration(command);
			config.launch(ILaunchManager.RUN_MODE, null);
		} catch (CoreException e) {
			AptanaRDTPlugin.log(e);
			return false;
		}
		return true;
	}

	private ILaunchConfigurationType getRubyApplicationConfigType() {
		return getLaunchManager().getLaunchConfigurationType(
				IRubyLaunchConfigurationConstants.ID_RUBY_APPLICATION);
	}

	private ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	private ILaunchConfiguration createGemLaunchConfiguration(String arguments) {
		String gemPath = getGemScriptPath();
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationType configType = getRubyApplicationConfigType();
			ILaunchConfigurationWorkingCopy wc = configType
					.newInstance(null, getLaunchManager()
							.generateUniqueLaunchConfigurationNameFrom(gemPath));
			wc.setAttribute(IRubyLaunchConfigurationConstants.ATTR_FILE_NAME,
					gemPath);
			wc.setAttribute(
					IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME,
					RubyRuntime.getDefaultVMInstall().getName());
			wc.setAttribute(
					IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE,
					RubyRuntime.getDefaultVMInstall().getVMInstallType()
							.getId());
			wc.setAttribute(
					IRubyLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
					arguments);
			 wc
			 .setAttribute(
			 IRubyLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
			 VM_ARGS);
			Map<String, String> map = new HashMap<String, String>();
			map
					.put(IRubyLaunchConfigurationConstants.ATTR_RUBY_COMMAND,
							EXECUTABLE);
			wc
					.setAttribute(
							IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP,
							map);
			wc.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, true);
			config = wc.doSave();
		} catch (CoreException ce) {
			// ignore for now
		}
		return config;
	}

	private static String getGemScriptPath() {
		IVMInstall vm = RubyRuntime.getDefaultVMInstall();
		File installLocation = vm.getInstallLocation();
		String path = installLocation.getAbsolutePath();
		return path + File.separator + "bin" + File.separator + "gem";
	}

	public boolean installGem(Gem gem) {
		try {
			String command = INSTALL_COMMAND + " " + gem.getName();
			if (gem.getVersion() != null && gem.getVersion().trim().length() > 0) {
				command += " " + VERSION_SWITCH + " " + gem.getVersion();
			}
			ILaunchConfiguration config = createGemLaunchConfiguration(command);
			ILaunch launch = config.launch(ILaunchManager.RUN_MODE, null);
			IProcess[] processes = launch.getProcesses();
			IProcess p = processes[0];
			IStreamMonitor monitor = p.getStreamsProxy()
					.getOutputStreamMonitor();
			final StringBuffer buffer = new StringBuffer();
			monitor.addListener(new IStreamListener() {

				public void streamAppended(String text, IStreamMonitor monitor) {
					buffer.append(text);
				}

			});
			String contents = null;
			boolean wroteSelection = false;
			long start = System.currentTimeMillis();
			String lastOut = null;
			while (!p.isTerminated()) {
				contents = buffer.toString();
				if (contents != null && contents.trim().length() > 0
						&& !wroteSelection) {
					// System.out.println(contents);
					if (contents.contains("Select which gem")) {
						// Parse out options
						Map<String, String> options = new HashMap<String, String>();

						String[] lines = contents.split("\n");
						for (int i = 0; i < lines.length; i++) {
							String line = lines[i].trim();
							if (Character.isDigit(line.charAt(0))) {
								String number = line.substring(0, line
										.indexOf('.'));
								int parenIndex = line.indexOf('(');
								if (parenIndex == -1)
									continue; // Skip or cancel option
								String platform = line.substring(
										parenIndex + 1, line.lastIndexOf(')'));
								options.put(platform, number);
							}
						}
						// Automatically select the option which matches this
						// platform.
						String myPlatform = Gem.RUBY_PLATFORM;
						if (Platform.getOS().equals(Constants.OS_WIN32)) {
							myPlatform = Gem.MSWIN32_PLATFORM;
						}
						try {
							p.getStreamsProxy().write(
									options.get(myPlatform) + "\r\n");
							wroteSelection = true;
						} catch (IOException e) {
							AptanaRDTPlugin.log(e);
						}
					}
				} else {
					Thread.yield();
					if (lastOut != null && !lastOut.equals(buffer.toString())) {
						start = System.currentTimeMillis(); // restart timeout
						// if we have
						// changes in output
					}
					if (System.currentTimeMillis() > start + TIMEOUT) {
						AptanaRDTPlugin.log(new Exception(TIMEOUT_MSG));
						break;
					}
				}

			}
		} catch (CoreException e) {
			AptanaRDTPlugin.log(e);
			return false;
		}
		for (GemListener listener : listeners) {
			listener.gemAdded(gem);
		}
		return true;
	}

	public boolean removeGem(Gem gem) {
		try {
			String command = UNINSTALL_COMMAND + " " + gem.getName();
			if (gem.getVersion() != null && gem.getVersion().trim().length() > 0) {
				command += " " + VERSION_SWITCH + " " + gem.getVersion();
			}
			ILaunchConfiguration config = createGemLaunchConfiguration(command);
			config.launch(ILaunchManager.RUN_MODE, null);
		} catch (CoreException e) {
			AptanaRDTPlugin.log(e);
			return false;
		}
		for (GemListener listener : listeners) {
			listener.gemRemoved(gem);
		} // FIXME Need to wait until uninstall is finished!
		return true;
	}

	public Set<Gem> getGems() {
		return Collections.unmodifiableSortedSet(new TreeSet<Gem>(gems));
	}

	public static GemManager getInstance() {
		if (fgInstance == null)
			fgInstance = new GemManager();
		return fgInstance;
	}

	public boolean refresh() {
		Set<Gem> newGems = loadLocalGems();
		if (!newGems.isEmpty()) {
			gems.clear();
			gems = newGems;
			for (GemListener listener : listeners) {
				listener.gemsRefreshed();
			}
			return true;
		}
		return false;
	}

	public synchronized void addGemListener(GemListener listener) {
		listeners.add(listener);
	}

	public interface GemListener {
		public void gemsRefreshed();
		public void gemAdded(Gem gem);
		public void gemRemoved(Gem gem);
	}

	public Set<Gem> getRemoteGems() {
		SortedSet<Gem> sorted = new TreeSet<Gem>(remoteGems);
		SortedSet<Gem> logical = new TreeSet<Gem>();
		String name = null;
		Collection<Gem> temp = new HashSet<Gem>();
		for (Gem gem : sorted) {
			if (name != null && !gem.getName().equals(name)) {
				logical.add(LogicalGem.create(temp));
				temp.clear();
			}
			name = gem.getName();
			temp.add(gem);
		}
		return Collections.unmodifiableSortedSet(logical);
	}

	public boolean gemInstalled(String gemName) {
		Set<Gem> gems = getGems();
		for (Gem gem : gems) {
			if (gem.getName().equalsIgnoreCase(gemName)) return true;
		}
		return false;
	}

	public synchronized void removeGemListener(GemListener listener) {
		listeners.remove(listener);		
	}
}