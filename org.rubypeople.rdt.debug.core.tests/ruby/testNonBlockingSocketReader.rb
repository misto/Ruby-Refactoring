require 'socket'

threads = []

a = 1
threads << Thread.new {
  loop {
    a = a + 1  
  }
}

puts "Acception on port 12134"
server = TCPServer.new('localhost', 12134)
socket = server.accept

threads << Thread.new {
  loop {
    sleep 0.1 # non blocking
    newData, x, y = IO.select( [socket], nil, nil, 0.001 )   # blocking
    next unless newData
    line = newData[0].gets.chomp!
    socket.puts "#{a}"    
  }
}

threads.each { |t| t.join }








