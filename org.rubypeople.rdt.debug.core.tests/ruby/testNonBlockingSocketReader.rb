require 'socket'

threads = []

a = 1
threads << Thread.new {


  loop {
    a = a + 1  
  }
}

puts "Ruby-version: #{RUBY_VERSION}"
puts "Acception on port 12134"
server = TCPServer.new('localhost', 12134)
socket = server.accept
threads << Thread.new {
  loop {
    sleep 0.1 # non blocking
    puts "calling select"
    newData, x, y = IO.select( [socket], nil, nil, 0.001 )   # blocking
    next unless newData    
    next unless newData.length > 0
    puts "Found data on #{newData[0]}"
    line = newData[0].gets.chomp!
    socket.puts "#{a}"    
  }
}

threads.each { |t| t.join }








