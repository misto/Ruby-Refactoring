# Copyright (C) 2000  Network Applied Communication Laboratory, Inc.
# Copyright (C) 2000  Information-technology Promotion Agency, Japan
require 'cgi'
# ECLIPSE_DEBUG turns on the eclipse mode which allows the rubyeclipse plugin
# to talk to the debugger.
ECLIPSE_DEBUG = true

# ECLIPSE_LISTEN_PORT is the port on which the debugger waits for commands from
# eclipse. If you change this, you must also change the corresponding port in
# org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy
ECLIPSE_LISTEN_PORT = 1098

# ECLIPSE_CREATE_SOCKET allows to run "ruby -reclipseDebug xxx.rb" without starting
# a socket. That can be useful for testing after this file or the debug.rb file have been
# modified. In order to have the XML output displayed, you must set ECLIPSE_VERBOSE
# to true.
ECLIPSE_CREATE_SOCKET = true

# ECLIPSE_VERBOSE prints the communication between eclipse and ruby debugger
# on stderr. If you have started a eclipse debug session (and use default preferences for
# colors of streams), the communication will be printed in red letters to the eclipse console.
ECLIPSE_VERBOSE = true


class PrinterMultiplexer
  def initialize
    @printers = []
  end

  def addPrinter(printer)
    @printers << printer
  end

  def method_missing(methodName, *args)
    @printers.each { |printer|
      printer.send(methodName, *args)
    }
  end
end



class XmlPrinter

  def initialize(socket)
    @socket = socket
  end

  def out(*params)
    if @socket then
      @socket.printf(*params)
    end
    debug(*params)
  end

  def printXml(s)
    out(s) 
  end

  def printVariable(name, binding)
    value = eval(name, binding)
    if !value then
      out("<variable name=\"%s\"/>", name)
      return
    end
    valueString = value.to_s
    if valueString =~ /^\"/ then
      valueString.slice!(1..(valueString.length)-2) 
    end
    out("<variable name=\"%s\" value=\"%s\" type=\"%s\" hasChildren=\"%s\"/>", name, CGI.escapeHTML(valueString), value.type(), value.instance_variables.length > 0 )
  end

  def printBreakpoint(n, debugFuncName, file, pos)
    out("<breakpoint file=\"%s\" line=\"%s\" threadId=\"%s\"/>", file, pos, DEBUGGER__.get_thread_num()) 	      
  end

  def printException(file, pos, exception)
    out("<exception file=\"%s\" line=\"%s\" type=\"%s\" message=\"%s\" threadId=\"%s\"/>", file, pos, exception.type, CGI.escapeHTML(exception.to_s), DEBUGGER__.get_thread_num()) 	      
  end

  def printStepEnd(file, line, framesCount)
    out("<suspended file=\"%s\" line=\"%s\" frames=\"%s\" threadId=\"%s\"/>", file, line, framesCount, DEBUGGER__.get_thread_num())    
  end

  def printFrame(pos, n, file, line, id)
    out("<frame no=\"%s\" file=\"%s\" line=\"%s\"/>", n, file, line)
  end
  
  def printThread(num, thread)      
      out("<thread id=\"%s\" status=\"%s\"/>", num, thread.status) ;
  end  
  
  def debug(*params)
    if ECLIPSE_VERBOSE then
      STDERR.printf(*params)
      STDERR.print("\n")
      STDERR.flush
    end
  end
end

#multiplex = PrinterMultiplexer.new
#multiplex.addPrinter(XmlPrinter.new(nil))
#multiplex.addPrinter(XmlPrinter.new(nil))
#multiplex.printXml("TEST")


require 'debug.rb'
