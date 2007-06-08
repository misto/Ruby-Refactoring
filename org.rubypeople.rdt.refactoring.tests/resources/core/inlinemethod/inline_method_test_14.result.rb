require 'test/unit'

class TestTest < Test::Unit::TestCase
  def testBla
    assert_equal <<-EOF, "test\n"
test
EOF
  end
end

t = TestTest.new
assert_equal <<-EOF, "test\n"
test
EOF
