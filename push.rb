#!/usr/bin/ruby

require 'rubygems'
require 'typhoeus'

file = File.open("test.push", "rb")
checkin = file.read
puts checkin

params = {'secret' => 'TIXBVE3J4QB1B2GNA0T3GQY3A1XG3ENOI0YR0KJJQRSS1ZQW',
'checkin' =>  checkin
}
x = Typhoeus::Request.post('http://localhost:8888/handlepush', :params => params)

puts x.code

# Uncomment this if you want output in a file
# File.open('out.htm', 'w') { |f| f.write x.body }

