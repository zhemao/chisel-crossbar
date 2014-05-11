# Chisel Crossbar

This package contains a generic implementation of a bi-directional crossbar
switch in the Chisel hardware description language. You can include it in
your own Chisel or Verilog projects.

To install, run "sbt publish-local" and then add the following dependency
line to your build.sbt file.

    "com.mao.howard" %% "chisel-crossbar" % "0.1-SNAPSHOT"

The file CrossbarSwitch.scala has some comments explaining how it should be
used.

## Example Usage

    // this creates a switch with a forward path of width 8
    // a backward path of width 16
    // 4 ports on the 'left'
    // and 8 ports on the 'bottom'
    val switch = Module(new CrossbarSwitch(8, 16, 4, 6)

    // this sets up the switch so that port 2 on the left
    // and port 4 on the bottom are connected to each other
    switch.io.fw_left(2) := UInt(22)
    switch.io.bw_bottom(4) := UInt(54)
    switch.io.select(4) := UInt(2)

## License

Copyright © 2014 Howard Zhehao Mao

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this 
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
