require 'test/unit/ui/testrunnermediator'

module Test
  module Unit
    module UI
      module Eclipse # :nodoc:
        
        # Runs a Test::Unit::TestSuite on the console.
        class TestRunner
                  
          # Creates a new TestRunner and runs the suite.
          def TestRunner.run(suite, io=STDOUT)
            return new(suite, io).start
          end
          
          # Takes care of the ARGV parsing and suite
          # determination necessary for running one of the
          # TestRunners from the command line.
          def TestRunner.start_command_line_test
            if ARGV.empty?
              puts "You should supply the name of a test suite file to the runner"
              exit
            end
            require ARGV[0].gsub(/.+::/, '')
            new(eval(ARGV[0])).start
          end
          
          # Creates a new TestRunner for running the passed
          # suite. If quiet_mode is true, the output while
          # running is limited to progress dots, errors and
          # failures, and the final result. io specifies
          # where runner output should go to; defaults to
          # STDOUT.
          def initialize(suite, io=STDOUT)
            if (suite.respond_to?(:suite))
              @suite = suite.suite
            else
              @suite = suite
            end
            @io = io
            @already_outputted = false
            @faults = []
            @tests = []
          end
          
          # Begins the test run.
          def start
            setup_mediator
            attach_to_mediator
            return start_mediator
          end
          
          private
          def send_tree(test)
            if(test.instance_of?(Test::Unit::TestSuite))
			  notifyTestTreeEntry("#{getTestId(test)},#{escapeComma(test.to_s.strip)},true,#{test.size}")
			  test.tests.each { |myTest| send_tree(myTest) }		
		    else 
			  notifyTestTreeEntry("#{getTestId(test)},#{escapeComma(test.name.strip)},false,#{test.size}")
		    end
          end
          
          def getTestId(test)
            @tests << test
            return test.id
          end
         
          def escapeComma(s)
            t = s.gsub(/\\/, "\\\\")
            t = t.gsub(',', "\\,")
		    return t
          end
          
          def notifyTestTreeEntry(treeEntry) 
		    output("%TSTTREE#{treeEntry}\n")
	      end
          
          def setup_mediator # :nodoc:
            @mediator = create_mediator(@suite)
            suite_name = @suite.to_s
            if ( @suite.kind_of?(Module) )
              suite_name = @suite.name
            end
          end
          
          def create_mediator(suite) # :nodoc:
            return TestRunnerMediator.new(suite)
          end
          
          def attach_to_mediator # :nodoc:
            @mediator.add_listener(TestResult::FAULT, &method(:add_fault))
            @mediator.add_listener(TestRunnerMediator::STARTED, &method(:started))
            @mediator.add_listener(TestRunnerMediator::FINISHED, &method(:finished))
            @mediator.add_listener(TestCase::STARTED, &method(:test_started))
            @mediator.add_listener(TestCase::FINISHED, &method(:test_finished))
          end
          
          def start_mediator # :nodoc:
            return @mediator.run_suite
          end
          
          def add_fault(fault) # :nodoc:
            @faults << fault
            if (fault.instance_of?(Test::Unit::Failure))
              fault_type = "%FAILED "
              header = "Test::Unit::AssertionFailedError: #{fault.message}"
              stack_trace = get_location(fault.location)
            else
              fault_type = "%ERROR  "
              header = "Exception: #{fault.exception.message}"
              stack_trace = get_trace(fault.exception.backtrace)
            end
            output_single("#{fault_type}#{@last_test_id},#{@last_test_name}\n")
            output_single("%TRACES \n")
            output_single("#{header}\n")
            output_single("#{stack_trace}\n")
            output_single("%TRACEE \n")
            @already_outputted = true
          end
          
          def get_location(location)
            return location[location.index('[') + 1, location.index(']') - 1].chop
          end
          
          def get_trace(backtrace)
            str = ""
            backtrace.each { |line| str << "#{line}\n" }
            return str
          end
          
          def started(result)
            @result = result
            output_single("%TESTC  #{@suite.size} v2\n")
            send_tree(@suite)
          end
          
          def finished(elapsed_time)
            modified_time = elapsed_time * 1000
            output("%RUNTIME#{modified_time.to_i}\n")
          end
          
          def get_test(name)
            @tests.each { |test| 
              if test.name == name
                return test
              end
            }
          end
          
          def test_started(name)
            @last_test_id = get_test(name).id
            @last_test_name = name
            output_single("%TESTS  #{@last_test_id},#{name}\n")
          end
          
          def test_finished(name)
            output_single("%TESTE  #{@last_test_id},#{name}\n")
            @already_outputted = false
          end
          
          def nl
            output("")
          end
          
          def output(something)
            @io.puts(something)
            @io.flush
          end
          
          def output_single(something)
            @io.write(something)
            @io.flush
          end
        end
      end
    end
  end
end

if __FILE__ == $0
  require 'socket'
  if ARGV.empty?
    puts "You should supply the name of a test suite file and the port to the runner"
    exit
  end
  # Expect args in this order:
  # 1. filename
  # 2. port
  # 3. keepAlive
  # 4. test class name
  # 5. test name (optional, unused right now)
  #
  
  filename = ARGV[0]
  require filename.gsub(/.+::/, '')
  port = ARGV[1].to_i
  keepAliveString = ARGV[2]
  testClass = ARGV[3]
  #testMethod = ARGV[4]
  
  if keepAliveString == "false"
    keepAlive = nil
  else
    keepAlive = true
  end
 
  session = TCPSocket.new('localhost', port)
  testSuite = eval(testClass)
  remoteTestRunner = Test::Unit::UI::Eclipse::TestRunner.new(testSuite, session)
  remoteTestRunner.start
  
  if (keepAlive)
	begin
	  while (true)
		if (message = session.gets)			
		  if (message[0, 7] == ">STOP   ")
			# remoteTestRunner.stop
			break
		  elsif (message[0, 7] == ">RERUN  ")
			arg = message[8, message.length - 1]
			#format: testId className testName
			c0 = arg.index(' ')
			c1 = arg.index(' ', c0+1)
			s = arg[0, c0 - 1]
			testId = s.to_i
			className = arg[c0+1, c1 - 1]
			testName = arg[c1 + 1, arg.length - 1]
			remoteTestRunner = Test::Unit::UI::Eclipse::TestRunner.new(testSuite, session)
            remoteTestRunner.start
		    #remoteTestRunner.addRerunRequest(RerunRequest.new(testId, className, testName))
		  end
	    end
	  end
	rescue Exception
	  # remoteTestRunner.stop
	end
  end
    
  session.close
  exit
end

