require 'test/unit'

class OpTest < Test::Unit::TestCase
  def test_
    var = false
    assert_equal(false, var)
    var ||= true
    assert_equal(true, var)
    var &&= false
    assert_equal(false, var)
  end
end
