EXECUTABLES = CrossbarSwitch OnewayCrossbarSwitch

test: $(addsuffix .vcd, $(EXECUTABLES))

verilog: $(addsuffix .v, $(EXECUTABLES))

%.vcd: src/main/scala/%.scala
	sbt "run $(notdir $(basename $<)) --genHarness --compile --test --vcd --backend c"

%.v: src/main/scala/%.scala
	sbt "run $(notdir $(basename $<)) --compile --backend fpga"

clean:
	rm -f SwitchCell $(EXECUTABLES) *.vcd *.v *.cpp *.o *.h
