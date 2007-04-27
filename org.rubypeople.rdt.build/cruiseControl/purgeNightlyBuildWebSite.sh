#!/bin/sh
ssh -l rubypeople download.rubypeople.org "rm -r ~/subdomains/updatesite/httpdocs/nightly; mkdir ~/subdomains/updatesite/httpdocs/nightly; rm -r ~/subdomains/download/httpdocs/nightly ; mkdir ~/subdomains/download/httpdocs/nightly"
ssh -l mbarchfeld www.aptana.org "rm -r /var/websites/updatesite.rubypeople.org/html/nightly; mkdir /var/websites/updatesite.rubypeople.org/html/nightly; rm -r /var/websites/download.rubypeople.org/html/nightly ; mkdir /var/websites/download.rubypeople.org/html/nightly"
 

