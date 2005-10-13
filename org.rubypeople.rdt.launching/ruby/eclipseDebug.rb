# Copyright (C) 2000  Network Applied Communication Laboratory, Inc.
# Copyright (C) 2000  Information-technology Promotion Agency, Japan
require 'cgi'
require 'thread'

# ECLIPSE_LISTEN_PORT is the port on which the debugger waits for commands from
# eclipse. If you change this, you must also change the corresponding port in
# org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy
ECLIPSE_LISTEN_PORT = (defined? $EclipseListenPort) ? $EclipseListenPort : 1098

# ECLIPSE_VERBOSE prints tihe communication between eclipse and ruby debugger
# on stderr. If you have started a eclipse debug session (and use default preferences for
# colors of streams), the communication will be printed in red letters to the eclipse console.
ECLIPSE_VERBOSE = defined? DebugVerbose


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
    debugIntern(false, *params)
    if @socket then
      @socket.printf(*params)
    end    
  end

  def printXml(s, *params)
    out(s, *params) 
  end

  def printVariable(name, binding, kind)
    printVariableValue(name, eval(name, binding), kind)
  end
  
  def printVariableValue(name, value, kind)
    if value == nil then
      out("<variable name=\"%s\" kind=\"%s\"/>", CGI.escapeHTML(name), kind)
      return
    end
    if value.class().name == "Array" or value.class().name == "Hash" then
      hasChildren = value.length > 0
      if value.length == 0 then
        valueString = "Empty " + value.class().name
      else
        valueString = value.class().name + " (" + value.length.to_s + " element(s))"
      end
    else 
	  hasChildren = value.instance_variables.length > 0 || value.class.class_variables.length > 0
      valueString = value.to_s
      if valueString =~ /^\"/ then
        valueString.slice!(1..(valueString.length)-2) 
      end	  
	end
    out("<variable name=\"%s\" kind=\"%s\" value=\"%s\" type=\"%s\" hasChildren=\"%s\" objectId=\"%s\"/>", CGI.escapeHTML(name), kind, CGI.escapeHTML(valueString), value.class(), hasChildren, value.respond_to?(:object_id) ? value.object_id : value.id)
  end

  def printBreakpoint(n, debugFuncName, file, pos)
    out("<breakpoint file=\"%s\" line=\"%s\" threadId=\"%s\"/>", file, pos, DEBUGGER__.get_thread_num()) 	      
  end

  def printException(file, pos, exception)
    out("<exception file=\"%s\" line=\"%s\" type=\"%s\" message=\"%s\" threadId=\"%s\"/>", file, pos, exception.class, CGI.escapeHTML(exception.to_s), DEBUGGER__.get_thread_num()) 	      
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
  
  def printLoadResult(file, exception=nil)
    if exception then
      out("<loadResult file=\"%s\" exceptionType=\"%s\" exceptionMessage=\"%s\"/>", file, exception.class, CGI.escapeHTML(exception.to_s)) 	      
    else
      out("<loadResult file=\"%s\" status=\"OK\"/>", file) 	      
    end
  end
  
  def debug(*params)
    debugIntern(true, *params)
  end
  
  def debugIntern(escape, *params)
    if ECLIPSE_VERBOSE then
      output = sprintf(*params)
      output = CGI.escapeHTML(output) if escape
      STDERR.print(output)
      STDERR.print("\n")
    end
  end
end

#multiplex = PrinterMultiplexer.new
#multiplex.addPrinter(XmlPrinter.new(nil))
#multiplex.addPrinter(XmlPrinter.new(nil))
#multiplex.printXml("TEST")



require 'socket'
require 'tracer'

class Tracer
  def Tracer.trace_func(*vars)
    Single.trace_func(*vars)
  end
end



SCRIPT_LINES__ = {} unless defined? SCRIPT_LINES__

class DEBUGGER__

  class CommandLinePrinter
    def printXml(s)
      # print XML only
    end

    def printVariable(name, binding, kind)
      stdout.printf "  %s => %s\n", name, eval(name, binding).inspect
    end

    def printBreakpoint(n, debugFuncName, file, pos)
      stdout.printf "Breakpoint %d, %s at %s:%s\n", n, debugFuncName, file, pos
      stdout.flush
    end
    
    def printFrame(pos, n, file, line, id)
      if pos == n
        stdout.printf "--> #%d  %s:%s%s\n", n, file, line, id ? ":in `#{id.id2name}'":""
      else
        stdout.printf "    #%d  %s:%s%s\n", n, file, line, id ? ":in `#{id.id2name}'":""
      end
    end
    
    def printException(file, line, exception)
      stdout.printf "%s:%d: `%s' (%s)\n", file, line, exception, exception.class
      stdout.flush
    end

    def printThread(num, thread)      
      if thread == Thread.current
        stdout.print "+"
      else
        stdout.print " "
      end
      stdout.printf "%d ", num
      stdout.print thread.inspect, "\t"
      file = DEBUGGER__.context(thread).instance_eval{@file}
      if file
        stdout.print file, ":", DEBUGGER__.context(thread).instance_eval{@line}
      end
      stdout.print "\n"
    end


    def printStepEnd(file, line, framesCount)

    end

    def debug(*params)

    end

    def stdout
      DEBUGGER__.stdout
    end
  end



  class Context
    attr_reader  :shouldResume
    def initialize
      @printer = DEBUGGER__.printer
      @socket = DEBUGGER__.socket
      @stop_next = 0
      @last_file = nil
      @last = [nil, nil]
      @file = nil
      @line = nil
      @no_step = nil
      @frames = []
      @finish_pos = 0
      @trace = false
      @catch = nil # "StandardError"
      @suspend_next = false
      @shouldResume = false
    end

    def stop_next(n=1)
      @stop_next = n
    end

    def set_suspend
      @suspend_next = true
    end

    def clear_suspend
      @suspend_next = false
    end

    def stopCurrentThread
      @printer.debug("Suspending : %s", Thread.current)
      Thread.stop()      
      @printer.debug("Resumed : %s", Thread.current)
    end

    def trace?
      @trace
    end

    def set_trace(arg)
      @trace = arg
    end

    def stdout
      if @socket then
        @socket
      else 
        DEBUGGER__.stdout
      end
    end

    def break_points
      DEBUGGER__.break_points
    end

    def display
      DEBUGGER__.display
    end

    def context(th)
      DEBUGGER__.context(th)
    end

    def set_trace_all(arg)
      DEBUGGER__.set_trace(arg)
    end

    def set_last_thread(th)
      DEBUGGER__.set_last_thread(th)
    end

    def debug_eval_private(str, binding)
      # evaluates str like "var.@instance_var.@instance_var"
      # and "var.privateMethod"
      # return value might be nil
      # the scan must detect array and hash accesses, which might
      # themselves contain point separated expressions, e.g.
      # array[Test.getX].y
      names  = str.scan(/[^\[\]]*(?=\.)|.*\[.*\](?=\.)|.*$/)
      # names can contain empty strings
      obj = eval(names[0], binding)
      (1..names.length-1).each { |i|
        if names[i] != "" then
          if names[i].length > 2 && names[i][0..1] == '@@' then
            @printer.debug("Evaluating (class_var): %s on %s", names[i], obj )
            obj = obj.class.class_eval("#{names[i]}")
          else
            @printer.debug("Evaluating (instance_var): %s on %s", names[i], obj )        
            obj = obj.instance_eval("#{names[i]}")
          end
        end
      }
      return obj
    end
    
    def debug_eval(str, binding, noPrivateInstanceVars = false)
      begin
        if noPrivateInstanceVars then
          val = eval(str, binding)
        else
          val = debug_eval_private(str, binding)
        end
        val
      rescue ScriptError, StandardError => error	
        if error.to_s =~ /private method .* called for/ then
          return debug_eval(str, binding, true)
        end
        @printer.debug("Error in debug_eval (noPrivateInstancsVars=%s): %s", noPrivateInstanceVars, error)
        raise error
      end
    end

    def debug_silent_eval(str, binding)
      begin
        val = eval(str, binding)
        val
      rescue StandardError, ScriptError
        nil
      end
    end

    def var_list(ary, binding, kind)
      ary.sort!
      @printer.printXml("<variables>")
      for v in ary
        @printer.printVariable(v,binding, kind)	
      end
      @printer.printXml("</variables>")
    end

    def printArrayElements(array)
      index = 0 
      array.each { |e|
        @printer.printVariableValue('[' + index.to_s + ']', e, 'instance') 
        index += 1 
      }
    end

    def printHashElements(hash)
	  hash.keys.each { | k |
	    if k.class.name == "String"
	      name = '\'' + k + '\''
	    else
	      name = k.to_s
	    end
        @printer.printVariableValue(name, hash[k], 'instance') 
      }
    end


    def getConstantsInClass(aClass) 
      constants = aClass.constants() - Object.constants
      return constants.delete_if { |c| ! aClass.const_defined? c }
    end


    def getBinding(pos)
      # returns frame info of frame pos, if pos is within bound,  nil otherwise
      if !pos then
        return nil
      end
      pos = pos.to_i
      pos -= 1
      if pos >= @frames.size || pos < 0 then
        @printer.debug("%i must be between %i and %i.", pos+1, 1, @frames.size)
        return nil
      end
      @printer.debug("Using frame %s for evaluation of variable.", pos)
      return @frames[pos][0]
    end


    def debug_variable_info(input, binding)

      case input
      when /^\s*g(?:lobal)?$/
        var_list(global_variables, binding, 'global')

      when /^\s*l(?:ocal)?\s*(\d+)?$/
        new_binding = getBinding($1)
        if new_binding then
          binding = new_binding
        end
        begin
          #@printer.debug("binding=%s", binding)
          localVars = eval("local_variables", binding)
          # v 1.8.1 dies in eval @printer.debug("HERE") 
          if eval('self.to_s', binding) !~ /main/ then
            localVars << "self"
          end
          var_list(localVars, binding, 'local')
        rescue StandardError => bang
          @printer.debug("Exception while evaluating local_variables: %s", bang)
          var_list([], binding, 'local') 
        end		

      when /^\s*i(?:nstance)?\s*(\d+)?\s+(\d+)?/        
        new_binding = getBinding($1)
        if new_binding then
          binding = new_binding
        end
        begin
          @printer.printXml("<variables>")
          if $2 then
            obj = ObjectSpace._id2ref($2.to_i)
            if (!obj) then
              @printer.debug("unknown object id : %s", $2)
            end
          else          
            obj = debug_eval($', binding)
          end
          if (obj.class.name == "Array") then
            printArrayElements(obj)
          	return
          end
          if (obj.class.name == "Hash") then
            printHashElements(obj)
          	return
          end          
          @printer.debug("%s", obj)
          instanceBinding = obj.instance_eval{binding()}	      
          obj.instance_variables.each {
            | instanceVar |
            @printer.printVariable(instanceVar, instanceBinding, 'instance')
          }
          classBinding = obj.class.class_eval('binding()')
          obj.class.class_variables.each {
            | classVar |
            @printer.printVariable(classVar, classBinding, 'class')
          }
          # I'd like to optimize getting the constants, but they are more dynamic
          # than one would expect from true constants.
          getConstantsInClass(obj.class).each {
            | constant |
            @printer.printVariable(constant, classBinding, 'constant')
          }
        rescue StandardError => error
          @printer.debug("%s", error)          
        ensure
          @printer.printXml("</variables>")  
        end
        
        
      when /^\s*inspect\s*(\d+)?\s+/        
        new_binding = getBinding($1)
        if new_binding then
          binding = new_binding
        end
        begin          
          obj = debug_eval($', binding, true)
        rescue ScriptError, StandardError => error
          @printer.printXml("<processingException type=\"%s\" message=\"%s\"/>", error.class, error.message)
          return 
        end          
        @printer.debug("%s", obj)
        @printer.printXml("<variables>")
        @printer.printVariableValue($', obj, "local")          
        @printer.printXml("</variables>")

        
      end
    end

    def debug_method_info(input, binding)
      case input
      when /^i(:?nstance)?\s+/
        obj = debug_eval($', binding);

        len = 0
        for v in obj.methods.sort
          len += v.size + 1
          if len > 70
            len = v.size + 1
            stdout.print "\n"
          end
          stdout.print v, " "
        end
        stdout.print "\n"

      else
        obj = debug_eval(input, binding)
        unless obj.kind_of? Module
          stdout.print "Should be Class/Module: ", input, "\n"
        else
          len = 0
          for v in obj.instance_methods.sort
            len += v.size + 1
            if len > 70
              len = v.size + 1
              stdout.print "\n"
            end
            stdout.print v, " "
          end
          stdout.print "\n"
        end
      end
    end

    def thnum(thread=Thread.current)
      num = DEBUGGER__.instance_eval{@thread_list[thread]}
      unless num
        DEBUGGER__.make_thread_list
        num = DEBUGGER__.instance_eval{@thread_list[thread]}
      end
      num
    end
    
    def processInput(input)
      binding, file, line, id = @frames[0]
      readUserInput(binding, file, line, input)
    end
        
    def readUserInput(binding, binding_file, binding_line, input)
      
      @printer.debug("Processing #{input}, binding=%s", binding)
      @shouldResume = false 
      frame_pos = 0      
      previous_line = nil
      display_expressions(binding)

      

        case input
        when /^\s*tr(?:ace)?(?:\s+(on|off))?(?:\s+(all))?$/
          if defined?( $2 )
            if $1 == 'on'
              set_trace_all true
            else
              set_trace_all false
            end
          elsif defined?( $1 )
            if $1 == 'on'
              set_trace true
            else
              set_trace false
            end
          end
          if trace?
            stdout.print "Trace on.\n"
          else
            stdout.print "Trace off.\n"
          end
          
        when /^\s*b(?:reak)?\s+(?:(add|remove)\s+)?((?:.*?+:)?.+)$/
          if $1 then
            mode = $1
          else
            mode = "add"
          end
          pos = $2
          if pos.index(":")
            file, pos = pos.split(":")
          end
          file = File.basename(file)
          if pos =~ /^\d+$/
            pname = pos
            pos = pos.to_i
          else
            pname = pos = pos.intern.id2name
          end
          if mode == "add" then
            break_points.push [true, 0, file, pos]
            @printer.debug("Set breakpoint %d at %s:%s\n", break_points.size, file, pname )
          else
            break_points_length = break_points.length
            break_points.delete_if {
              | b | 
              b[2] == file && b[3] == pos	
            }
            if break_points_length == break_points.length then
              @printer.debug("No such breakpoint to remove : %s:%s", file, pname)
            else
              @printer.debug("Removed breakpoint : %s:%s", file, pname)
            end
          end
          
          #        when /^\s*wat(?:ch)?\s+(.+)$/
          #          exp = $1
          #          break_points.push [true, 1, exp]
          #          stdout.printf "Set watchpoint %d\n", break_points.size, exp
          
          #        when /^\s*b(?:reak)?$/
          #          if break_points.find{|b| b[1] == 0}
          #            n = 1
          #            stdout.print "Breakpoints:\n"
          #            for b in break_points
          #              if b[0] and b[1] == 0
          #                stdout.printf "  %d %s:%s\n", n, b[2], b[3] 
          #              end
          #              n += 1
          #            end
          #          end
          #          if break_points.find{|b| b[1] == 1}
          #            n = 1
          #            stdout.print "\n"
          #            stdout.print "Watchpoints:\n"
          #            for b in break_points
          #              if b[0] and b[1] == 1
          #                stdout.printf "  %d %s\n", n, b[2]
          #              end
          #              n += 1
          #            end
          #          end
          #          if break_points.size == 0
          #            stdout.print "No breakpoints\n"
          #          else
          #            stdout.print "\n"
          #          end
          
          #        when /^\s*del(?:ete)?(?:\s+(\d+))?$/
          #          pos = $1
          #          unless pos
          #            input = readline("Clear all breakpoints? (y/n) ", false)
          #            if input == "y"
          #              for b in break_points
          #                b[0] = false
          #              end
          #            end
          #          else
          #            pos = pos.to_i
          #            if break_points[pos-1]
          #              break_points[pos-1][0] = false
          #            else
          #              stdout.printf "Breakpoint %d is not defined\n", pos
          #            end
          #          end
          
          #        when /^\s*disp(?:lay)?\s+(.+)$/
          #          exp = $1
          #          display.push [true, exp]
          #          stdout.printf "%d: ", display.size
          #          display_expression(exp, binding)
          #
          #        when /^\s*disp(?:lay)?$/
          #          display_expressions(binding)
          #
          #        when /^\s*undisp(?:lay)?(?:\s+(\d+))?$/
          #          pos = $1
          #          unless pos
          #            input = readline("Clear all expressions? (y/n) ", false)
          #            if input == "y"
          #              for d in display
          #                d[0] = false
          #              end
          #            end
          #          else
          #            pos = pos.to_i
          #            if display[pos-1]
          #              display[pos-1][0] = false
          #            else
          #              stdout.printf "Display expression %d is not defined\n", pos
          #            end
          #          end
          
        when /^\s*c(?:ont)?$/
          @shouldResume = true
          
        when /^\s*s(?:tep)?(?:\s+(\d+))?$/
          if $1
            lev = $1.to_i
          else
            lev = 1
          end
          @stop_next = lev
          @shouldResume = true
          
        when /^\s*n(?:ext)?(?:\s+(\d+))?$/
          if $1
            lev = $1.to_i
          else
            lev = 1
          end
          @stop_next = lev
          @no_step = @frames.size - frame_pos
          @shouldResume = true
          
        when /^\s*w(?:here)?$/, /^\s*f(?:rame)?$/
          display_frames(frame_pos)
          
          #        when /^\s*fin(?:ish)?$/
          #          if frame_pos == @frames.size
          #            stdout.print "\"finish\" not meaningful in the outermost frame.\n"
          #          else
          #            @finish_pos = @frames.size - frame_pos
          #            frame_pos = 0
          #            prompt = false
          #          end
          
        when /^\s*cat(?:ch)?(?:\s+(.+))?$/
          # $1 can also be nil 
          @catch = $1
          @catch = nil if @catch == 'off'
          if @catch then
            @printer.debug("Catchpoint set to #{@catch}")
          else
            @printer.debug("Catchpoints disabled")
          end
         
          
        when /^\s*v(?:ar)?\s+/
          debug_variable_info($', binding)
          
        when /^\s*m(?:ethod)?\s+/
          debug_method_info($', binding)
          
        when /^\s*th(?:read)?\s+/
          DEBUGGER__.debug_thread_info($', binding)
          
        when /^\s*p\s+/
          stdout.printf "%s\n", debug_eval($', binding).inspect
          
        when /^\s*load\s+/
          @printer.debug("loading file: %s", $')
          begin
            load $'            
            @printer.printLoadResult($')
          rescue Exception => error
            @printer.printLoadResult($', error)
          end

        else
          @printer.debug("Unknown input : %s", input)
        end
        
      
      
    end



    def display_expressions(binding)
      n = 1
      for d in display
        if d[0]
          stdout.printf "%d: ", n
          display_expression(d[1], binding)
        end
        n += 1
      end
    end

    def display_expression(exp, binding)
      stdout.printf "%s = %s\n", exp, debug_silent_eval(exp, binding).to_s
    end


    def display_frames(pos)
      pos += 1
      n = 0
      at = @frames
      @printer.printXml("<frames>") 
      for bind, file, line, id in at
        n += 1
        break unless bind
        @printer.printFrame(pos, n, file, line, id)
      end
      @printer.printXml("</frames>") 
    end

    def debug_funcname(id)
      if id.nil?
        "toplevel"
      else
        id.id2name
      end
    end

    def debug_command(file, line, id, binding)
      @printer.debug("debug_command, @stop_next=%s, @frames.size=%s", @stop_next, @frames.size)
      if @stop_next == 0 && @frames.size > 0 then
        # write suspended only if the suspension was originated by stepping and not before the start 
        # of the program
        @printer.printStepEnd(file, line, @frames.size)
      end
      set_last_thread(Thread.current)

      #readUserInput(binding, file, line ) 
      #Thread.stop
    end

    def check_break_points(file, pos, binding, id)
      return false if break_points.empty?
      file = File.basename(file)
      n = 1
      for b in break_points
        @printer.debug("file=%s, pos=%s; breakpoint: %s, %s, %s, %s.\n ", file, pos, b[0], b[1], b[2], b[3])
        if b[0]
          if b[1] == 0 and b[2] == file and b[3] == pos
            @printer.printBreakpoint(n, debug_funcname(id), file, pos)
            return true
          elsif b[1] == 1
            if debug_silent_eval(b[2], binding)
              stdout.printf "Watchpoint %d, %s at %s:%s\n", n, debug_funcname(id), file, pos
              return true
            end
          end
        end
        n += 1
      end
      return false
    end

    def excn_handle(file, line, id, binding)
      
      if $!.class <= SystemExit
        @printer.debug( "SystemExit at %s:%d: `%s' (%s)\n", file, line, $!, $!.class   )
        set_trace_func nil
        exit
      end
      if @catch and ($!.class.ancestors.find { |e| e.to_s == @catch })
        @printer.printException(file, line, $!)
        fs = @frames.size
        tb = caller(0)[-fs..-1]
        stopCurrentThread
        @frames[0] = [binding, file, line, id]
        debug_command(file, line, id, binding)
      end     
      
    end

    def trace_func(event, file, line, id, binding, klass)

      Tracer.trace_func(event, file, line, id, binding, klass) if trace?
      
      @file = file
      @line = line
      case event
      when 'line'           
        DEBUGGER__.printer().debug("trace line, file=%s, line=%s, stop_next=%d, binding=%s", file, line, @stop_next, binding)      
        if @frames[0] then
          @frames[0][0] = binding
          @frames[0][1] = file
          @frames[0][2] = line
          @frames[0][3] = id
        else
          @frames[0] = [binding, file, line, id]	
        end
        if !@no_step or @frames.size == @no_step
          @stop_next -= 1
        elsif @frames.size < @no_step
          @stop_next = 0		# break here before leaving...
        end

        if @stop_next == 0 or check_break_points(file, line, binding, id)	
          if [file, line] == @last
            @stop_next = 1
          else
            @no_step = nil
            debug_command(file, line, id, binding)
            stopCurrentThread
            @last = [file, line]
          end
        end

      when 'call'
        DEBUGGER__.printer().debug("trace call, file=%s, line=%s, method=%s", file, line, id.id2name)
        @frames.unshift [binding, file, line, id]
        if check_break_points(file, id.id2name, binding, id) or
            check_break_points(klass.to_s, id.id2name, binding, id) then
          stopCurrentThread		
          debug_command(file, line, id, binding)
        end

      when 'c-call'
        if @frames[0] then
          @frames[0][1] = file
          @frames[0][2] = line
        else
          @frames[0] = [binding, file, line, id]	
        end

      when 'class'
        @frames.unshift [binding, file, line, id]

      when 'return', 'end'
        DEBUGGER__.printer().debug("trace return and end, file=%s, line=%s", file, line)
        if @frames.size == @finish_pos
          @stop_next = 1
          @finish_pos = 0
        end
        @frames.shift

      when 'end'
        DEBUGGER__.printer().debug("trace end, file=%s, line=%s", file, line)
        @frames.shift

      when 'raise' 
        excn_handle(file, line, id, binding)

      end
      @last_file = file
    end
  end

  trap("INT") { DEBUGGER__.interrupt }
  @last_thread = Thread::main
  @max_thread = 1
  @thread_list = {Thread::main => 1}
  @break_points = []
  @display = []
  @waiting = []
  @stdout = STDOUT

  class << DEBUGGER__
    def stdout
      @stdout
    end

    def stdout=(s)
      @stdout = s
    end

    def display
      @display
    end

    def break_points
      @break_points
    end

    def waiting
      @waiting
    end

    def set_trace( arg )
      Thread.critical = true
      make_thread_list
      for th in @thread_list
        context(th[0]).set_trace arg
      end
      Thread.critical = false
    end

    def set_last_thread(th)
      @last_thread = th
    end

    def suspend
      printer.debug("Suspending all")
      Thread.critical = true
      make_thread_list
      for th in @thread_list
        next if th[0] == Thread.current
        context(th[0]).set_suspend
      end
      Thread.critical = false
      # Schedule other threads to suspend as soon as possible.
      Thread.pass
    end

    def resume
      Thread.critical = true
      make_thread_list
      for th in @thread_list
	next if th[0] == Thread.current
        context(th[0]).clear_suspend
      end
      waiting.each do |th|
        th.run
      end
      waiting.clear
      Thread.critical = false
      # Schedule other threads to restart as soon as possible.
      Thread.pass
    end

    def context(thread=Thread.current)
      c = thread[:__debugger_data__]
      unless c
        thread[:__debugger_data__] = c = Context.new
      end
      c
    end

    def findThread(context)
      for thread in Thread::list
        if context == thread[:__debugger_data__]
          return thread
        end
      end
    end 

    def interrupt
      context(@last_thread).stop_next
    end

    def get_thread(num)
      th = @thread_list.index(num)
      unless th
        printer.debug("No thread ##{num}\n")
      end
      th
    end

    def get_thread_num(thread=Thread.current)
      make_thread_list
      @thread_list[thread]
    end

    def print_thread(thread) 
      num = @thread_list[thread]
      printer.printThread(num, thread)	
    end

    def thread_list_all
      printer.printXml("<threads>") 
      for num in @thread_list.values.sort
        printer.printThread(num, get_thread(num))
      end
      printer.printXml("</threads>") 
    end

    def make_thread_list
      hash = {}
      for th in Thread::list
        next if th == @@inputReader
        if @thread_list.key? th
          hash[th] = @thread_list[th]
        else
          @max_thread += 1
          hash[th] = @max_thread
        end
      end
      @thread_list = hash
    end

    def debug_thread_info(input, binding)
      case input
      when /^l(?:ist)?/
        make_thread_list
        thread_list_all

      when /^c(?:ur(?:rent)?)?$/
        make_thread_list
        print_thread()

      when /^(?:sw(?:itch)?\s+)?(\d+)/
        make_thread_list
        th = get_thread($1.to_i)
        if th == Thread.current
          @stdout.print "It's the current thread.\n"
        else
          print_thread(th)
          context(th).stop_next
          th.run
          return
        end

      when /^stop\s+(\d+)/
        make_thread_list
        th = get_thread($1.to_i)
        if th == Thread.current
          @stdout.print "It's the current thread.\n"
        elsif th.stop?
          @stdout.print "Already stopped.\n"
        else
          print_thread(th)
          context(th).suspend 
        end

      when /^resume\s+(\d+)/
        make_thread_list
        th = get_thread($1.to_i)
        if th == Thread.current
          @stdout.print "It's the current thread.\n"
        elsif !th.stop?
          @stdout.print "Already running."
        else
          print_thread(th)
          th.run
        end

      when /^change\s+(\d+)/
        make_thread_list
        th = get_thread($1.to_i)
        print_thread(th)
        return context(th) 
      end  	  
    end
  end

  @@socket = nil
  @@printer = nil
  @@inputReader = nil
  @@isStarted = false
  
  def DEBUGGER__.printer
    @@printer 
  end

  def DEBUGGER__.socket
    @@socket 
  end

  def DEBUGGER__.isStarted
    @@isStarted 
  end

  def DEBUGGER__.setStarted
    @@isStarted = true 
  end


  def DEBUGGER__.inputReader
    @@inputReader 
  end
  
  def DEBUGGER__.traceOn()  
    set_trace_func @@traceProc
    Thread.critical = false ;
  end

  def DEBUGGER__.traceOff()  
    Thread.critical = true ;	
    set_trace_func nil
  end

  def DEBUGGER__.readCommandLoop()
    sleep(1.0) # workaround for large files with ruby 1.6.8, otherwise parse exceptions
    loop do
      sleep(0.1)
      newData, x, y = IO.select( [socket], nil, nil, 0.001 )    
      next unless newData
      DEBUGGER__.traceOff()
      input = newData[0].gets.chomp!
      input.strip!
      @@printer.debug("READ #{input}")
      if !DEBUGGER__.isStarted && input == "cont" then
        DEBUGGER__.setStarted()
        DEBUGGER__.traceOn()     		
        next
      end
      input =~ /^th\s+(\d+)\s*;\s*/		
      if $~ then
        @@printer.debug("Using context for thread: %s", $1.to_i)
        context = DEBUGGER__.context(get_thread($1.to_i))
        input = input[$~[0].length..input.length]
      else
        @@printer.debug("Using context for main thread : %s", Thread.main)
        context = DEBUGGER__.context(Thread.main)
      end 		
      context.processInput(input)
      if context.shouldResume then
      	  threadToResume = DEBUGGER__.findThread(context) 
      end
      DEBUGGER__.traceOn()     		
      next unless threadToResume
      threadToResume.run()
    end
  end

  # use 127.0.0.1 instead of localhost because OSX 10.4
  server = TCPServer.new('127.0.0.1', ECLIPSE_LISTEN_PORT)
  puts "ruby #{RUBY_VERSION} debugger listens on port #{ECLIPSE_LISTEN_PORT}"
  $stdout.flush
  STDERR.sync = true ;
  @@socket = server.accept
  @@printer = XmlPrinter.new(@@socket)
  @@printer.debug("Socket connection established.")
  
  @@printer.debug("Starting command reader loop.")
  @@inputReader = Thread.new {
    begin
      DEBUGGER__.readCommandLoop()
    rescue ScriptError, StandardError => error	
      puts error
    end
  }

  @@traceProc = proc { |event, file, line, id, binding, klass, *rest|
    
    #@@printer.debug("trace %s, %s:%s", event, file, line)		
    next if Thread.current == DEBUGGER__.inputReader
    next if file =~ /eclipseDebug.rb$/
    #@@printer.debug("trace %s, %s:%s", event, file, line)		
    while (!DEBUGGER__.isStarted) do
      @@printer.debug("Debugging not yet started.")
      sleep(1)
    end
    DEBUGGER__.context.trace_func event, file, line, id, binding, klass    
  }

  @@printer.debug("Setting trace func: %s", @@traceProc)
  set_trace_func @@traceProc
end


