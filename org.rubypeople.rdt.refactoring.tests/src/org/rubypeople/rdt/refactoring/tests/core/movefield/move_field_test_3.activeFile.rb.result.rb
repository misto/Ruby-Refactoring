require 'includedFile'

class TestKlasse
  
  def initialize
    @field = ATargetClass.new
  end
  
  
  def five_plus arg
    5 + arg
  end
  
  def calculate
    @field.var = five_plus 1
  end
  
  def use_var
    @field.var = @field.var + "."
  end
  
  def var
    @field.var
  end
  public :var
  
end

t = TestKlasse.new
t.calculate
puts t.var