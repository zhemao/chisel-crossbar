package ChiselCrossbar

import Chisel._

object CrossbarMain {
    def main(args: Array[String]): Unit = {
        val testArgs = args.slice(1, args.length)
        args(0) match {
            case "SwitchCell" => chiselMainTest(testArgs,
                () => Module(new SwitchCell(16, 8))) {
                    c => new SwitchCellTest(c)
                }
            case "CrossbarSwitch" => chiselMainTest(testArgs,
                () => Module(new CrossbarSwitch(8, 8, 6, 8))) {
                    c => new CrossbarSwitchTest(c)
                }
            case "OnewayCrossbarSwitch" => chiselMainTest(testArgs,
                () => Module(new OnewayCrossbarSwitch(8, 6, 8))) {
                    c => new OnewayCrossbarSwitchTest(c)
                }
        }
    }
}
