package ChiselCrossbar

import Chisel._

class SwitchCell(val fwidth: Int, val bwidth: Int)  extends Module {
    val io = new Bundle {
        val fw_left   = UInt(INPUT, fwidth)
        val bw_left   = UInt(OUTPUT, bwidth)
        val fw_top    = UInt(INPUT, fwidth)
        val bw_top    = UInt(OUTPUT, bwidth)
        val fw_bottom = UInt(OUTPUT, fwidth)
        val bw_bottom = UInt(INPUT, bwidth)
        val fw_right  = UInt(OUTPUT, fwidth)
        val bw_right  = UInt(INPUT, bwidth)
        val sel = Bool(INPUT)
    }

    when (io.sel) {
        io.fw_bottom := io.fw_left
        io.bw_left := io.bw_bottom
        io.fw_right := UInt(0)
        io.bw_top := UInt(0)
    } .otherwise {
        io.fw_right := io.fw_left
        io.bw_left := io.bw_right
        io.fw_bottom := io.fw_top
        io.bw_top := io.bw_bottom
    }
}

class SwitchCellTest(c: SwitchCell) extends Tester(c) {
    val left = rnd.nextInt(1 << c.fwidth)
    val top = rnd.nextInt(1 << c.fwidth)
    val right = rnd.nextInt(1 << c.bwidth)
    val bottom = rnd.nextInt(1 << c.bwidth)

    poke(c.io.fw_left, left)
    poke(c.io.bw_right, right)
    poke(c.io.fw_top, top)
    poke(c.io.bw_bottom, bottom)
    poke(c.io.sel, 0)

    step(1)

    expect(c.io.fw_right, left)
    expect(c.io.bw_left, right)
    expect(c.io.fw_bottom, top)
    expect(c.io.bw_top, bottom)

    poke(c.io.sel, 1)

    step(1)

    expect(c.io.fw_right, 0)
    expect(c.io.bw_left, bottom)
    expect(c.io.fw_bottom, left)
    expect(c.io.bw_top, 0)
}
