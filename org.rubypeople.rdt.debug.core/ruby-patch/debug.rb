# Copyright (C) 2000  Network Applied Communication Laboratory, Inc.
# Copyright (C) 2000  Information-technology Promotion Agency, Japan

if $SAFE > 0
  STDERR.print "-r debug.rb is not available in safe mode\n"
  exit 1
end
require 'socket'
require 'tracer'

class Tracer
  def Tracer.trace_func(*vars)
    Single.trace_func(*vars)
  end
end

ECLIPSE_DEBUG = false unless defined? ECLIPSE_DEBUG


SCRIPT_LINES__ = {} unless defined? SCRIPT_LINES__

class DEBUGGER__
  
  class CommandLinePrinter
    def printXml(s)
      # print XML only
    end

    def printVariable(name, binding)
      stdout.printf "  %s => %s\n", name, eval(name, binding).inspect
    end

    def printBreakpoint(n, debugFuncName, file, pos)
      stdout.printf "Breakpoint %d, %s at %s:%s\n", n, debugFuncName, file, pos
    end
    
    def printFrame(pos, n, file, line, id)
      if pos == n
	stdout.printf "--> #%d  %s:%s%s\n", n, file, line, id ? ":in `#{id.id2name}'":""
      else
	stdout.printf "    #%d  %s:%s%s\n", n, file, line, id ? ":in `#{id.id2name}'":""
      end
    end
    
    def printException(file, line, exception)
      stdout.printf "%s:%d: `%s' (%s)\n", file, line, exception, exception.type
      stdout.flush
	end

    def printStepEnd(file, line, framesCount)

    end

    def debug(*params)

    end

    def stdout
      DEBUGGER__.stdout
    end
  end

  class Mutex
    def initialize
      @locker = nil
      @waiting = []
      @locked = false;
    end

    def locked?
      @locked
    end

    def lock
      return if @locker == Thread.current
      while (Thread.critical = true; @locked)
	@waiting.push Thread.current
	Thread.stop
      end
      @locked = true
      @locker = Thread.current
      Thread.critical = false
      self
    end

    def unlock
      return unless @locked
      unless @locker == Thread.current
	raise RuntimeError, "unlocked by other"
      end
      Thread.critical = true
      t = @waiting.shift
      @locked = false
      @locker = nil
      Thread.critical = false
      t.run if t
      self
    end
  end
  MUTEX = Mutex.new

  class Context
    DEBUG_LAST_CMD = []

#    begin
#   require 'readline'
#      def readline(prompt, hist)
#	Readline::readline(prompt, hist)
#      end
#    rescue LoadError
    def readline(prompt, hist)
      line = nil ;
      if @socket  then
	line = @socket.gets
	exit unless line
	line.chop! # the end character is 13
      else
	STDOUT.print prompt
	STDOUT.flush
	line = STDIN.gets
	exit unless line
	line.chomp!
      end
      @printer.debug( "READ: #{line}" )      
      line
    end
    USE_READLINE = false
    #    end


    def initialize
      @printer = DEBUGGER__.printer
      @socket = DEBUGGER__.socket
      if Thread.current == Thread.main
	@stop_next = 1
      else
	@stop_next = 0
      end
      @last_file = nil
      @last = [nil, nil]
      @file = nil
      @line = nil
      @no_step = nil
      @frames = []
      @finish_pos = 0
      @trace = false
      @catch = "StandardError"
      @suspend_next = false
      @contsCount = 0 
    end

    def isFirstCont()
      @contsCount += 1
      return @contsCount == 1
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

    def suspend_all
      DEBUGGER__.suspend
    end

    def resume_all
      DEBUGGER__.resume
    end

    def check_suspend
      while (Thread.critical = true; @suspend_next)
	DEBUGGER__.waiting.push Thread.current
	@suspend_next = false
	Thread.stop
      end
      Thread.critical = false
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
      names  = str.split('.')
      obj = eval(names[0], binding)
      (1..names.length-1).each { |i|
	obj = obj.instance_eval { eval(names[i], binding()) }
      }
      return obj
    end
    
    def debug_eval(str, binding, privateInstanceVars = false)
      begin
	if privateInstanceVars then
	  val = eval(str, binding)
	else
	  val = debug_eval_private(str, binding)
	end
	val
      rescue ScriptError, StandardError => error	
	if error.to_s =~ /private method .* called for/ then
	  return debug_eval(str, binding, true)
	end
	at = eval("caller(0)", binding)
	stdout.printf "%s:%s\n", at.shift, $!.to_s.sub(/\(eval\):1:(in `.*?':)?/, '') #`
	for i in at
	  stdout.printf "\tfrom %s\n", i
	end
	throw :debug_error
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

    def var_list(ary, binding)
      ary.sort!
      @printer.printXml("<variables>")
      for v in ary
	@printer.printVariable(v,binding)	
      end
      @printer.printXml("</variables>")
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
	var_list(global_variables, binding)

      when /^\s*l(?:ocal)?\s*(\d+)?$/
	new_binding = getBinding($1)
	if new_binding then
	  binding = new_binding
	end
	var_list(eval("local_variables", binding), binding)


      when /^\s*i(?:nstance)?\s*(\d+)?\s+/        
	new_binding = getBinding($1)
	if new_binding then
	  binding = new_binding
	end
	begin
	  obj = debug_eval($', binding)
	rescue StandardError 
	end
	var_list(obj.instance_variables, obj.instance_eval{binding()})

	
      when /^\s*c(?:onst(?:ant)?)?\s+/
	obj = debug_eval($', binding)
	unless obj.kind_of? Module
	  stdout.print "Should be Class/Module: ", $', "\n"
	else
	  var_list(obj.constants, obj.module_eval{binding()})
	end
      end
    end

    def debug_method_info(input, binding)
      case input
      when /^i(:?nstance)?\s+/
	obj = debug_eval($', binding)

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

    def thnum
      num = DEBUGGER__.instance_eval{@thread_list[Thread.current]}
      unless num
	DEBUGGER__.make_thread_list
	num = DEBUGGER__.instance_eval{@thread_list[Thread.current]}
      end
      num
    end

    def debug_command(file, line, id, binding)
      
      if @stop_next == 0 && @frames.size > 0 then
	# write suspended only if the suspension was originated by stepping and not before the start 
	# of the program
	@printer.printStepEnd(file, line, @frames.size)
      end
      MUTEX.lock
      set_last_thread(Thread.current)
      frame_pos = 0
      binding_file = file
      binding_line = line
      previous_line = nil
      if (ENV['EMACS'] == 't')
	stdout.printf "\032\032%s:%d:\n", binding_file, binding_line
      elsif !ECLIPSE_DEBUG then 	
	stdout.printf "%s:%d:%s", binding_file, binding_line,
	  line_at(binding_file, binding_line)
      end
      @frames[0] = [binding, file, line, id]
      display_expressions(binding)
      prompt = true
      while prompt and input = readline("(rdb:%d) "%thnum(), true)
	catch(:debug_error) do
	  if input == ""
	    input = DEBUG_LAST_CMD[0]
	    stdout.print input, "\n"
	  else
	    DEBUG_LAST_CMD[0] = input
	  end

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

	  when /^\s*b(?:reak)?\s+((?:.*?+:)?.+)$/
	    pos = $1
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
	    break_points.push [true, 0, file, pos]
	    stdout.printf "Set breakpoint %d at %s:%s\n", break_points.size, file, pname

	  when /^\s*wat(?:ch)?\s+(.+)$/
	    exp = $1
	    break_points.push [true, 1, exp]
	    stdout.printf "Set watchpoint %d\n", break_points.size, exp

	  when /^\s*b(?:reak)?$/
	    if break_points.find{|b| b[1] == 0}
	      n = 1
	      stdout.print "Breakpoints:\n"
	      for b in break_points
		if b[0] and b[1] == 0
		  stdout.printf "  %d %s:%s\n", n, b[2], b[3] 
		end
		n += 1
	      end
	    end
	    if break_points.find{|b| b[1] == 1}
	      n = 1
	      stdout.print "\n"
	      stdout.print "Watchpoints:\n"
	      for b in break_points
		if b[0] and b[1] == 1
		  stdout.printf "  %d %s\n", n, b[2]
		end
		n += 1
	      end
	    end
	    if break_points.size == 0
	      stdout.print "No breakpoints\n"
	    else
	      stdout.print "\n"
	    end

	  when /^\s*del(?:ete)?(?:\s+(\d+))?$/
	    pos = $1
	    unless pos
	      input = readline("Clear all breakpoints? (y/n) ", false)
	      if input == "y"
		for b in break_points
		  b[0] = false
		end
	      end
	    else
	      pos = pos.to_i
	      if break_points[pos-1]
		break_points[pos-1][0] = false
	      else
		stdout.printf "Breakpoint %d is not defined\n", pos
	      end
	    end

	  when /^\s*disp(?:lay)?\s+(.+)$/
	    exp = $1
	    display.push [true, exp]
	    stdout.printf "%d: ", display.size
	    display_expression(exp, binding)

	  when /^\s*disp(?:lay)?$/
	    display_expressions(binding)

	  when /^\s*undisp(?:lay)?(?:\s+(\d+))?$/
	    pos = $1
	    unless pos
	      input = readline("Clear all expressions? (y/n) ", false)
	      if input == "y"
		for d in display
		  d[0] = false
		end
	      end
	    else
	      pos = pos.to_i
	      if display[pos-1]
		display[pos-1][0] = false
	      else
		stdout.printf "Display expression %d is not defined\n", pos
	      end
	    end

	  when /^\s*c(?:ont)?$/
	    # TODO: Handling for breakpoint in first line of file (that is: the first
	    # not empty line). Problem: $1, or line do not contain that line
	    #if isFirstCont() then
	    #  puts "START #{$1}"	      
	    #  prompt = check_break_points(file, $1, binding, id)
	    #else
	      prompt = false
	    #end


	  when /^\s*s(?:tep)?(?:\s+(\d+))?$/
	    if $1
	      lev = $1.to_i
	    else
	      lev = 1
	    end
	    @stop_next = lev
	    prompt = false

	  when /^\s*n(?:ext)?(?:\s+(\d+))?$/
	    if $1
	      lev = $1.to_i
	    else
	      lev = 1
	    end
	    @stop_next = lev
	    @no_step = @frames.size - frame_pos
	    prompt = false

	  when /^\s*w(?:here)?$/, /^\s*f(?:rame)?$/
	    display_frames(frame_pos)

	  when /^\s*l(?:ist)?(?:\s+(.+))?$/
	    if not $1
	      b = previous_line ? previous_line + 10 : binding_line - 5
	      e = b + 9
	    elsif $1 == '-'
	      b = previous_line ? previous_line - 10 : binding_line - 5
	      e = b + 9
	    else
	      b, e = $1.split(/[-,]/)
	      if e
		b = b.to_i
		e = e.to_i
	      else
		b = b.to_i - 5
		e = b + 9
	      end
	    end
	    previous_line = b
	    display_list(b, e, binding_file, binding_line)

	  when /^\s*up(?:\s+(\d+))?$/
	    previous_line = nil
	    if $1
	      lev = $1.to_i
	    else
	      lev = 1
	    end
	    frame_pos += lev
	    if frame_pos >= @frames.size
	      frame_pos = @frames.size - 1
	      stdout.print "At toplevel\n"
	    end
	    binding, binding_file, binding_line = @frames[frame_pos]
	    stdout.printf "#%d %s:%s\n", frame_pos+1, binding_file, binding_line

	  when /^\s*down(?:\s+(\d+))?$/
	    previous_line = nil
	    if $1
	      lev = $1.to_i
	    else
	      lev = 1
	    end
	    frame_pos -= lev
	    if frame_pos < 0
	      frame_pos = 0
	      stdout.print "At stack bottom\n"
	    end
	    binding, binding_file, binding_line = @frames[frame_pos]
	    stdout.printf "#%d %s:%s\n", frame_pos+1, binding_file, binding_line

	  when /^\s*fin(?:ish)?$/
	    if frame_pos == @frames.size
	      stdout.print "\"finish\" not meaningful in the outermost frame.\n"
	    else
	      @finish_pos = @frames.size - frame_pos
	      frame_pos = 0
	      prompt = false
	    end

	  when /^\s*cat(?:ch)?(?:\s+(.+))?$/
	    if $1
	      excn = $1
	      if excn == 'off'
		@catch = nil
		stdout.print "Clear catchpoint.\n"
	      else
		@catch = excn
		stdout.printf "Set catchpoint %s.\n", @catch
	      end
	    else
	      if @catch
		stdout.printf "Catchpoint %s.\n", @catch
	      else
		stdout.print "No catchpoint.\n"
	      end
	    end

	  when /^\s*q(?:uit)?$/
	    input = readline("Really quit? (y/n) ", false)
	    if input == "y"
	      exit!	# exit -> exit!: No graceful way to stop threads...
	    end

	  when /^\s*v(?:ar)?\s+/
	    debug_variable_info($', binding)

	  when /^\s*m(?:ethod)?\s+/
	    debug_method_info($', binding)

	  when /^\s*th(?:read)?\s+/
	    if DEBUGGER__.debug_thread_info($', binding) == :cont
	      prompt = false
	    end

	  when /^\s*p\s+/
	    stdout.printf "%s\n", debug_eval($', binding).inspect

	  when /^\s*h(?:elp)?$/
	    debug_print_help()

	  else
	    v = debug_eval(input, binding)
	    stdout.printf "%s\n", v.inspect unless (v == nil)
	  end
	end
      end
      MUTEX.unlock
      resume_all
    end

    def debug_print_help
      stdout.print <<EOHELP
Debugger help v.-0.002b
Commands
  b[reak] [file|method:]<line|method>
                             set breakpoint to some position
  wat[ch] <expression>       set watchpoint to some expression
  cat[ch] <an Exception>     set catchpoint to an exception
  b[reak]                    list breakpoints
  cat[ch]                    show catchpoint
  del[ete][ nnn]             delete some or all breakpoints
  disp[lay] <expression>     add expression into display expression list
  undisp[lay][ nnn]          delete one particular or all display expressions
  c[ont]                     run until program ends or hit breakpoint
  s[tep][ nnn]               step (into methods) one line or till line nnn
  n[ext][ nnn]               go over one line or till line nnn
  w[here]                    display frames
  f[rame]                    alias for where
  l[ist][ (-|nn-mm)]         list program, - lists backwards
                             nn-mm lists given lines
  up[ nn]                    move to higher frame
  down[ nn]                  move to lower frame
  fin[ish]                   return to outer frame
  tr[ace] (on|off)           set trace mode of current thread
  tr[ace] (on|off) all       set trace mode of all threads
  q[uit]                     exit from debugger
  v[ar] g[lobal]             show global variables
  v[ar] l[ocal]              show local variables
  v[ar] i[nstance] <object>  show instance variables of object
  v[ar] c[onst] <object>     show constants of object
  m[ethod] i[nstance] <obj>  show methods of object
  m[ethod] <class|module>    show instance methods of class or module
  th[read] l[ist]            list all threads
  th[read] c[ur[rent]]       show current thread
  th[read] [sw[itch]] <nnn>  switch thread context to nnn
  th[read] stop <nnn>        stop thread nnn
  th[read] resume <nnn>      resume thread nnn
  p expression               evaluate expression and print its value
  h[elp]                     print this help
  <everything else>          evaluate
EOHELP
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

    def frame_set_pos(file, line)
      if @frames[0]
	@frames[0][1] = file
	@frames[0][2] = line
      end
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

    def display_list(b, e, file, line)
      stdout.printf "[%d, %d] in %s\n", b, e, file
      if lines = SCRIPT_LINES__[file] and lines != true
	n = 0
	b.upto(e) do |n|
	  if n > 0 && lines[n-1]
	    if n == line
	      stdout.printf "=> %d  %s\n", n, lines[n-1].chomp
	    else
	      stdout.printf "   %d  %s\n", n, lines[n-1].chomp
	    end
	  end
	end
      else
	stdout.printf "No sourcefile available for %s\n", file
      end
    end

    def line_at(file, line)
      lines = SCRIPT_LINES__[file]
      if lines
	return "\n" if lines == true
	line = lines[line-1]
	return "\n" unless line
	return line
      end
      return "\n"
    end

    def debug_funcname(id)
      if id.nil?
	"toplevel"
      else
	id.id2name
      end
    end

    def check_break_points(file, pos, binding, id)
      return false if break_points.empty?
      file = File.basename(file)
      n = 1
      for b in break_points
	#print "file=#{file}, pos=#{pos},  #{b[0]},#{b[1]},#{b[2]},#{b[3]}\n "
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
      
      if $!.type <= SystemExit
	stdout.printf "SystemExit at %s:%d: `%s' (%s)\n", file, line, $!, $!.type      
	set_trace_func nil
	exit
      end
	  puts "#{@catch}"
      if @catch and ($!.type.ancestors.find { |e| e.to_s == @catch })
	@printer.printException(file, line, $!)
	fs = @frames.size
	tb = caller(0)[-fs..-1]
	if tb
	  for i in tb
	    stdout.printf "\tfrom %s\n", i
	  end
	end
	suspend_all
	debug_command(file, line, id, binding)

      else
	      stdout.printf "%s:%d: `%s' (%s)\n", file, line, $!, $!.type
      end
    end

    def trace_func(event, file, line, id, binding, klass)
      Tracer.trace_func(event, file, line, id, binding, klass) if trace?
      context(Thread.current).check_suspend
      @file = file
      @line = line
      case event
      when 'line'
	frame_set_pos(file, line)
	if !@no_step or @frames.size == @no_step
	  @stop_next -= 1
	elsif @frames.size < @no_step
	  @stop_next = 0		# break here before leaving...
	else
	  # nothing to do. skipped.
	end
	if @stop_next == 0 or check_break_points(file, line, binding, id)
	  if [file, line] == @last
	    @stop_next = 1
	  else
	    @no_step = nil
	    suspend_all
	    debug_command(file, line, id, binding)
	    @last = [file, line]
	  end
	end

      when 'call'
	@frames.unshift [binding, file, line, id]
	if check_break_points(file, id.id2name, binding, id) or
	    check_break_points(klass.to_s, id.id2name, binding, id)
	  suspend_all
	  debug_command(file, line, id, binding)
	end

      when 'c-call'
	frame_set_pos(file, line)

      when 'class'
	@frames.unshift [binding, file, line, id]

      when 'return', 'end'
	if @frames.size == @finish_pos
	  @stop_next = 1
	  @finish_pos = 0
	end
	@frames.shift

      when 'end'
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

  class <<DEBUGGER__
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

    def interrupt
      context(@last_thread).stop_next
    end

    def get_thread(num)
      th = @thread_list.index(num)
      unless th
	@stdout.print "No thread ##{num}\n"
	throw :debug_error
      end
      th
    end

    def thread_list(num)
      th = get_thread(num)
      if th == Thread.current
	@stdout.print "+"
      else
	@stdout.print " "
      end
      @stdout.printf "%d ", num
      @stdout.print th.inspect, "\t"
      file = context(th).instance_eval{@file}
      if file
	@stdout.print file,":",context(th).instance_eval{@line}
      end
      @stdout.print "\n"
    end

    def thread_list_all
      for th in @thread_list.values.sort
	thread_list(th)
      end
    end

    def make_thread_list
      hash = {}
      for th in Thread::list
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
	thread_list(@thread_list[Thread.current])

      when /^(?:sw(?:itch)?\s+)?(\d+)/
	make_thread_list
	th = get_thread($1.to_i)
	if th == Thread.current
	  @stdout.print "It's the current thread.\n"
	else
	  thread_list(@thread_list[th])
	  context(th).stop_next
	  th.run
	  return :cont
	end

      when /^stop\s+(\d+)/
	make_thread_list
	th = get_thread($1.to_i)
	if th == Thread.current
	  @stdout.print "It's the current thread.\n"
	elsif th.stop?
	  @stdout.print "Already stopped.\n"
	else
	  thread_list(@thread_list[th])
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
	  thread_list(@thread_list[th])
	  th.run
	end
      end
    end


  end

  if !ECLIPSE_DEBUG then
    stdout.printf "Debug.rb\n"
    stdout.printf "Emacs support available.\n\n"
  end
  
  if ECLIPSE_DEBUG then
	if ECLIPSE_CREATE_SOCKET then
	  server = TCPServer.new('localhost', ECLIPSE_LISTEN_PORT)
	  puts "ruby debugger listens on port #{ECLIPSE_LISTEN_PORT}"
	  $stdout.flush
	  @@socket = server.accept
	  @@printer = XmlPrinter.new(@@socket)
	else
	  puts "eclipse mode, reading from stdin"
	  @@socket = nil
	  @@printer = PrinterMultiplexer.new
	  @@printer.addPrinter(XmlPrinter.new(nil))
	  @@printer.addPrinter(CommandLinePrinter.new())
	end	
      else
	@@printer = CommandLinePrinter.new()
      end
      
  def DEBUGGER__.printer
     @@printer 
  end

  def DEBUGGER__.socket
     @@socket 
  end

  
  set_trace_func proc { |event, file, line, id, binding, klass, *rest|
    DEBUGGER__.context.trace_func event, file, line, id, binding, klass
  }
end
