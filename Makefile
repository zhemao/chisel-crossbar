%.vcd: src/main/scala/%.scala
	sbt "run $(notdir $(basename $<)) --genHarness --compile --test --vcd --backend c"

%.v: src/main/scala/%.scala
	sbt "run $(notdir $(basename $<)) --compile --backend fpga"

clean:
	rm -f RegisterSet CrossbarSwitch *.vcd *.v *.cpp *.o *.h
