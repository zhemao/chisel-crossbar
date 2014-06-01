package ChiselCrossbar

import Chisel._

class OnewaySwitchCell(val fwidth: Int)  extends Module {
    val io = new Bundle {
        val fw_left   = Bits(INPUT, fwidth)
        val fw_top    = Bits(INPUT, fwidth)
        val fw_bottom = Bits(OUTPUT, fwidth)
        val fw_right  = Bits(OUTPUT, fwidth)
        val sel = Bool(INPUT)
    }

    when (io.sel) {
        io.fw_bottom := io.fw_left
        io.fw_right := Bits(0)
    } .otherwise {
        io.fw_right := io.fw_left
        io.fw_bottom := io.fw_top
    }
}

class OnewayCrossbarSwitch(
        val fwidth: Int, val m: Int, val n: Int) extends Module {

    val io = new Bundle {
        val fw_left = Vec.fill(m){ Bits(INPUT, fwidth) }
        val fw_bottom = Vec.fill(n){ Bits(OUTPUT, fwidth) }
        val select = Vec.fill(n){ UInt(INPUT, log2Up(m)) }
    }

    val cells = Vec.fill(n) { Vec.fill(m) {
        Module(new OnewaySwitchCell(fwidth)).io }}
    val select_onehot = Range(0, n).map {
        i => UIntToOH(io.select(i), m)
    }

    for (i <- 0 until n; j <- 0 until m) {
        val cur_cell = cells(i)(j)

        if (i == 0) {
            cur_cell.fw_left := io.fw_left(j)
        } else {
            val left_cell = cells(i - 1)(j)
            cur_cell.fw_left := left_cell.fw_right
        }

        if (j == 0) {
            cur_cell.fw_top := Bits(0)
        } else {
            val top_cell = cells(i)(j - 1)
            cur_cell.fw_top := top_cell.fw_bottom
        }

        cur_cell.sel := select_onehot(i)(j)
    }

    for (i <- 0 until n) {
        io.fw_bottom(i) := cells(i)(m - 1).fw_bottom
    }
}

class OnewayCrossbarSwitchTest(c: OnewayCrossbarSwitch) extends Tester(c) {
    val fw_left = new Array[BigInt](c.m)

    for (i <- 0 until c.m) {
        fw_left(i) = rnd.nextInt(1 << c.fwidth)
    }

    val base_select = rnd.shuffle(Range(0, c.m).toList)
                         .map { i => BigInt(i) }
                         .toArray

    val select: Array[BigInt] = if (c.m == c.n) {
        base_select
    } else if (c.n < c.m) {
        base_select.slice(0, c.n)
    } else {
        Array.concat(base_select, Array.fill(c.n - c.m){ BigInt(0) })
    }

    poke(c.io.fw_left, fw_left)
    poke(c.io.select, select)
    step(1)

    val fw_bottom = (0 until c.n).map {
        i => if (i < c.m) {
            fw_left(select(i).intValue)
        } else {
            BigInt(0)
        }
    }.toArray

    expect(c.io.fw_bottom, fw_bottom)
}
