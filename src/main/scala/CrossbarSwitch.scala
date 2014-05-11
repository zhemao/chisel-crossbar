package ChiselCrossbar

import Chisel._

class CrossbarSwitch(
        val fwidth: Int, val bwidth: Int,
        val m: Int, val n: Int) extends Module {
    /**
     * fw_left is the forward input
     * bw_left is the backward output
     * fw_bottom is the forward output
     * bw_bottom is the backward input
     * select determines the switching pattern
     *     there is one uint for each bottom port,
     *     the value of which corresponds to one of the left ports
     */
    val io = new Bundle {
        val fw_left = Vec.fill(m){ UInt(INPUT, fwidth) }
        val bw_left = Vec.fill(m){ UInt(OUTPUT, bwidth) }
        val fw_bottom = Vec.fill(n){ UInt(OUTPUT, fwidth) }
        val bw_bottom = Vec.fill(n){ UInt(INPUT, bwidth) }
        val select = Vec.fill(n){ UInt(INPUT, log2Up(m)) }
    }

    val cells = Vec.fill(n) { Vec.fill(m) {
        Module(new SwitchCell(fwidth, bwidth)).io }}
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

        if (i == n - 1) {
            cur_cell.bw_right := UInt(0)
        } else {
            val right_cell = cells(i + 1)(j)
            cur_cell.bw_right := right_cell.bw_left
        }

        if (j == 0) {
            cur_cell.fw_top := UInt(0)
        } else {
            val top_cell = cells(i)(j - 1)
            cur_cell.fw_top := top_cell.fw_bottom
        }

        if (j == m - 1) {
            cur_cell.bw_bottom := io.bw_bottom(i)
        } else {
            val bottom_cell = cells(i)(j + 1)
            cur_cell.bw_bottom := bottom_cell.bw_top
        }

        cur_cell.sel := select_onehot(i)(j)
    }

    for (i <- 0 until n) {
        io.fw_bottom(i) := cells(i)(m - 1).fw_bottom
    }

    for (i <- 0 until m) {
        io.bw_left(i) := cells(0)(i).bw_left
    }
}

class CrossbarSwitchTest(c: CrossbarSwitch) extends Tester(c) {
    val fw_left = new Array[BigInt](c.m)
    val bw_bottom = new Array[BigInt](c.n)

    for (i <- 0 until c.m) {
        fw_left(i) = rnd.nextInt(1 << c.fwidth)
    }

    for (i <- 0 until c.n) {
        bw_bottom(i) = rnd.nextInt(1 << c.bwidth)
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
    poke(c.io.bw_bottom, bw_bottom)
    poke(c.io.select, select)

    step(1)

    val bw_left = Range(0, c.m).map {
        i => val s = select.indexOf(i)
        if (s < 0 || s >= c.n) {
            BigInt(0)
        } else {
            bw_bottom(s)
        }
    }.toArray

    val fw_bottom = (0 until c.n).map {
        i => if (i < c.m) {
            fw_left(select(i).intValue)
        } else {
            BigInt(0)
        }
    }.toArray

    expect(c.io.bw_left, bw_left)
    expect(c.io.fw_bottom, fw_bottom)
}
