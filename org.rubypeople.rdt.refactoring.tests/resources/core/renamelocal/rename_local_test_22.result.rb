require 'test/unit'

class OpTest < Test::Unit::TestCase
  def test_
    funny_operator_test = false
    assert_equal(false, funny_operator_test)
    funny_operator_test ||= true
    assert_equal(true, funny_operator_test)
    funny_operator_test &&= false
    assert_equal(false, funny_operator_test)
  end
end
