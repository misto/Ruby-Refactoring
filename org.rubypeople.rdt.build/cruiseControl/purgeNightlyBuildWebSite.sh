#!/bin/sh
ssh -l rubypeople download.rubypeople.org "rm -r ~/subdomains/updatesite/httpdocs/nightly; mkdir ~/subdomains/updatesite/httpdocs/nightly; rm -r ~/subdomains/download/httpdocs/nightly ; mkdir ~/subdomains/download/httpdocs/nightly"
