require 'test/unit/ui/testrunnermediator'

module Test
  module Unit
    module UI
      
      module Eclipse # :nodoc:
        
        # Runs a Test::Unit::TestSuite on the console.
        class TestRunner
          
          # The class variable which tracks the ids of the tests
          # In Java they actually use the hashcode, but I'll cheat
          # for now
          @@test_id = 0
          
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
          end
          
          # Begins the test run.
          def start
            setup_mediator
            attach_to_mediator
            return start_mediator
          end
          
          private
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
              fault_type = "%FAILED"
            else
              fault_type = "%ERROR"
            end
            output_single("#{fault_type} #{@@test_id},#{@last_test_name}\n")
            output_single("%TRACES \n")
            output_single("#{fault.location}\n")
            output_single("%TRACEE \n")
            @already_outputted = true
          end
          
          def started(result)
            @result = result
            output_single("%TESTC  #{@suite.size} v2\n")
          end
          
          def finished(elapsed_time)
            modified_time = elapsed_time * 1000
            output("%RUNTIME#{modified_time.to_i}\n")
          end
          
          def test_started(name)
            @@test_id = @@test_id + 1
            @last_test_name = name
            output_single("%TESTS  #{@@test_id},#{name}\n")
          end
          
          def test_finished(name)
            output_single("%TESTE  #{@@test_id},#{name}\n")
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
  require ARGV[0].gsub(/.+::/, '')
  port = ARGV[1].to_i

  session = TCPSocket.new('localhost', port)
  Test::Unit::UI::Eclipse::TestRunner.run(eval(ARGV[0]), session)
  session.close
  exit
end

