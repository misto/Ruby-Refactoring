
  <table width="100%">
    <tr>
      <td align="left"><font face="arial,helvetica,geneva"><b>Ruby Development Tools</b></font></td>
      <td align="right"><img src="images/rdt_big.png"></td>
    </tr>
  </table>
  
  <table bgcolor="#ffffff" border="0" cellpadding="2" cellspacing="5" width="100%">
    <tr>
      <td align="right" valign="top" width="20"><img src="images/arrow.gif" alt="" border="0" height="16" width="16"></td>
      <td valign="top"><font face="arial,helvetica,geneva" size="-1"><b> Welcome to RDT</b>
        <br/>
        <p>RDT is an open source Ruby IDE for the Eclipse platform.<br>
Features supported are syntax highlighting, on the fly syntax check,
graphical outline, Test::Unit view/runner, Ruby application launching, 
content assist, source formatter, and a Ruby debugger.<br/>
A regular expression plugin is also available (Thanks to the <a href="http://e-p-i-c.sourceforge.net/" target="_top">EPIC</a> project).<br/>
        </p>
        <br/>
        </font>
      </td>
    </tr>
	</table>

		<div id="mainPartHeader">
			<a name="WhatsNew">What's New</a>
		 </div>


    <!-- INSERT NEW NEWS HERE!!! -->
			
	<table>
	 <tr>
      <td align="right" valign="top" width="20"><img src="images/arrow.gif" alt="->" border="0" height="16" width="16"></td>
      <td valign="top"><font face="arial,helvetica,geneva" size="-1"><b>0.7.0 Release Candidate (2005-12-29)</b>
        <br/>
The development of 0.7.0 has reached a point where we can present a release candidate. New features include ruby specific searching and text hovers with ri information. Next to that there are a lot of improvements with code completion, in the editor, outline view and debugger, more details can be found in the 
<a href="http://download.rubypeople.org/nightly/Changelog.txt">Changelog</a>. 
Note that our update site URLs have changed. The old URLs have been redirected, but the udpate manager doesn't handle it correctly. Please read the 
<a href="http://rubyeclipse.sourceforge.net/download.rdt.html">download information</a> if you want to use the udpate manager (this build is available in the integration stream). 
 

 
The final release of 0.7.0 is scheduled for next week but the exact date is up to the feedback we will receive.</p>
        </font>
      </td>
    </tr>
	
				    <tr>
      <td align="right" valign="top" width="20"><img src="images/arrow.gif" alt="->" border="0" height="16" width="16"></td>
      <td valign="top"><font face="arial,helvetica,geneva" size="-1"><b>Article on developerWorks (2005-10-18)</b>
        <br/>
Recently Neal Ford has published an article on IBM's developerWorks site. It gives an excellent overview of RDT, particularly useful for newbies:<br/>
 <a href="http://www-128.ibm.com/developerworks/opensource/library/os-rubyeclipse/">Using the Ruby Development Tools plug-in for Eclipse</a>
</p>
        </font>
      </td>
    </tr>
		    <tr>
      <td align="right" valign="top" width="20"><img src="images/arrow.gif" alt="->" border="0" height="16" width="16"></td>
      <td valign="top"><font face="arial,helvetica,geneva" size="-1"><b>0.6.0 Released (2005-09-29)</b>
        <br/>
Release 0.6.0 is out: read the <a href="http://sourceforge.net/forum/forum.php?forum_id=499503">news</a>, 
check the <a href="http://rubyeclipse.sourceforge.net/userdoc/html/ch04.html">documentation</a>, <a href="http://rubyeclipse.sourceforge.net/download.rdt.html">download</a> and enjoy, 
<a href="http://rubyeclipse.sourceforge.net/contact.rdt.html">feedback</a> very much appreciated!
</p>
        </font>
      </td>
    </tr>
	    <tr>
      <td align="right" valign="top" width="20"><img src="images/arrow.gif" alt="->" border="0" height="16" width="16"></td>
      <td valign="top"><font face="arial,helvetica,geneva" size="-1"><b> Help needed for 0.6.0 Release (2005-09-01)</b>
        <br/>
Release 0.6.0 is coming soon. The main bulk of work for the integration of JRuby has been managed. 
Now we need the comments of our users. Please check out our <a href="http://rubyeclipse.sourceforge.net/nightlyBuild">nightly builds</a> and give us your feedback in the forums or on the developers mailing list.

Next to the new features we can offer with 0.6.0, the large changes behind the scenes give us also
an excellent position for improving code searching and navigability in the next releases. The new features
of 0.6.0 are (see also the <a href="http://download.rubypeople.org/nightly/Changelog.txt">Changelog</a>)

<ul>
<li><b>Code Folding</b> - Folding can be enabled for classes and methods</li>
<li><b>Outline view</b> - more detailed, e.g. support for local variables</li>
<li><b>RI view</b> - use Ruby's ri utility from an RDT view</li>
<li><b>Task tags</b> - creates tasks for configurable keywords (like TODO, FIXME) in ruby comments</li>
<li><b>Editor improvements</b> - Auto-complete of brackets, parens, and single/double quotes; better code-assist</li>
<li><b>Inspection shortcuts</b> - Configurable shortcuts for frequently used inspections during a debug session, like showing all methods of an object, global constants and so on.</li>
</ul>
</p>
        </font>
      </td>
    </tr>

    <tr>
      <td align="right" valign="top" width="20"><img src="images/arrow.gif" alt="->" border="0" height="16" width="16"></td>
      <td valign="top"><font face="arial,helvetica,geneva" size="-1"><b> RDT 0.6.0 Work Continues (2005-02-15)</b>
        <br/>
<p>Just an update to our users to let them know, we are working hard on RDT 0.6.0.
<ul> Planned work includes:
<li><b>RI plugin</b> - allows users to lookup docs on Ruby classes inside an Eclipse plugin (interaces with Ruby's ri utility)</li>
<li><b>JRuby's parser</b> - We're attempting to combin some efforts with JRuby to leverage their parser. This should improve our results markedly (as will be
noticable in things such as the Outline view).</li>
<li><b>Richer Core Model</b> - Anotehr lareg change unde rthe hood will be a move to a much richer internal model for Ruby code, mirroring that of JDT's.
This should allow for significant improvements in the ability to get out richer features.</li>
<li><b>Editor improvements</b> - Smart auto-matching of parentheses, brackets and quotes, as in JDT (Type "{" and RDT will add the closing bracket and insert th                                                                                             e cursor between them).</li>
</ul>
</p>
        </font>
      </td>
    </tr>



		<tr>
      <td align="right" valign="top" width="20"><img src="images/arrow.gif" alt="->" border="0" height="16" width="16"></td>
      <td valign="top"><font face="arial,helvetica,geneva" size="-1"><b> RDT 0.5.0 Docs Translated to Japaenese (2004-12-24)</b>
        <br/>
<p>Thanks to Masanori Kado our RDT Documention for release 0.5.0 has been 
translated to Japanese and is available on the web. Seeing as how Ruby is 
wildly popular in Japan, this is a huge contribution. Thank you! 
<br/>
On a side note, the 0.5.0 release is available on the update site, sorry I never updated the news when we added it.
<br/>
<a href="http://capsctrl.que.jp/kdmsnr/wiki/rdt/" target="_top">View the translated docs now!</a></p>
        </font>
      </td>
    </tr>
    <tr>
      <td align="right" valign="top" width="20"><img src="images/arrow.gif" alt="->" border="0" height="16" width="16"></td>
      <td valign="top"><font face="arial,helvetica,geneva" size="-1"><b> RDT 0.5.0 Released (2004-11-28)</b>
        <br/>
<p>This release includes a significant number of new features for RDT. New 
features included in this release: Test::Unit runner, Template code completions 
(which can be created and edited by users), a Regular expression plugin, an 
introductory tutorial/cheat sheet, integration into Eclipse's Welcome screens, 
and assorted bugfixes.  
<br/>
Please note that the 0.5.0 release is not yet available on the update site, but will be 
within the next day or two.
<br/>
<a href="download.html" target="main">Download it now!</a></p>
        </font>
      </td>
    </tr>
    <tr>
      <td align="right" valign="top" width="20"><img src="images/arrow.gif" alt="->" border="0" height="16" width="16"></td>
      <td valign="top"><font face="arial,helvetica,geneva" size="-1"><b> New website! (2004-11-24)</b>
        <br/>
<p>Welcome to the latest website for the RDT project. In an effort to help
promote the project, I decided a new website was a dire need. I know, for one,
that when I'm looking at a new program I base a large part of the decision on
the website of the project. If the site offers no screenshots, docs or other
compelling reason to use the program I typically pass by. Here's hoping we
increase the the usership and visibility of the priject. We could use the 
extra hands!</p>
        </font>
      </td>
    </tr>
  </table>
