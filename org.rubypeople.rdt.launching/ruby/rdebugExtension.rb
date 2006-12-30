module Debugger
  class XmlPrinter
    
    def print_inspect(eval_result)
      print_element("variables") do 
        print_variable("eval_result", eval_result, 'locale')
      end
    end
    
    def print_load_result(file, exception=nil)
      if exception then
        print("<loadResult file=\"%s\" exceptionType=\"%s\" exceptionMessage=\"%s\"/>", file, exception.class, CGI.escapeHTML(exception.to_s))        
      else
        print("<loadResult file=\"%s\" status=\"OK\"/>", file)        
      end
    end
    
  end
  
  class InspectCommand < Command
    # reference inspection results in order to save them from the GC
    @@references = []
    def self.reference_result(result)
      @@references << result
    end
    def self.clear_references
      @@references = []
    end
    
    def regexp
      /^\s*v(?:ar)?\s+inspect\s+/
    end
    #    
    def execute
      obj = debug_eval(@match.post_match)
      InspectCommand.reference_result(obj)
      @printer.print_inspect(obj)
    end
  end
  
  class LoadCommand < Command  
    def regexp
      /^\s*load\s+/
    end
    
    def execute
      fileName = @match.post_match
      @printer.print_debug("loading file: %s", fileName)
      begin
        load fileName
        @printer.print_load_result(fileName)
      rescue Exception => error
        @printer.print_load_result(fileName, error)
      end
    end
  end
end