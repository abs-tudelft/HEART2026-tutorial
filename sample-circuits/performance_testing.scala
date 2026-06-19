//> using scala "2.13.14"
//> using dep "com.github.rameloni::tywaves-chisel-api:0.4.2-chiseltrace-SNAPSHOT"
//> using dep "org.chipsalliance::chisel:6.4.3-tywaves-chiseltrace-SNAPSHOT"
//> using plugin "org.chipsalliance:::chisel-plugin:6.4.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"
//> using dep "org.scalatest::scalatest:3.2.18"

// Source: https://github.com/jarlb/tywaves-chisel repository, examples folder

// DO NOT EDIT THE ORTHER OF THESE IMPORTS (it will be solved in future versions)
import chisel3._
import chisel3.util.log2Ceil
import tywaves.simulator._
import tywaves.simulator.ParametricSimulator._
import tywaves.simulator.simulatorSettings._
import circt.stage.ChiselStage
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
//import _root_.circt.stage.ChiselStage
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class AdderTree4096(bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(Vec(4096, UInt(bitWidth.W)))
    val out = Output(UInt((bitWidth + 12).W))
  })
  
  // Pipeline stages (12 levels for 4096 inputs)
  val level1 = RegNext(VecInit.tabulate(2048) { i => 
    io.in(2*i) +& io.in(2*i + 1)
  })
  
  val level2 = RegNext(VecInit.tabulate(1024) { i => 
    level1(2*i) +& level1(2*i + 1)
  })
  
  val level3 = RegNext(VecInit.tabulate(512) { i => 
    level2(2*i) +& level2(2*i + 1)
  })
  
  val level4 = RegNext(VecInit.tabulate(256) { i => 
    level3(2*i) +& level3(2*i + 1)
  })
  
  val level5 = RegNext(VecInit.tabulate(128) { i => 
    level4(2*i) +& level4(2*i + 1)
  })
  
  val level6 = RegNext(VecInit.tabulate(64) { i => 
    level5(2*i) +& level5(2*i + 1)
  })
  
  val level7 = RegNext(VecInit.tabulate(32) { i => 
    level6(2*i) +& level6(2*i + 1)
  })
  
  val level8 = RegNext(VecInit.tabulate(16) { i => 
    level7(2*i) +& level7(2*i + 1)
  })
  
  val level9 = RegNext(VecInit.tabulate(8) { i => 
    level8(2*i) +& level8(2*i + 1)
  })

  val level10 = RegNext(VecInit.tabulate(4) { i => 
    level9(2*i) +& level9(2*i + 1)
  })

  val level11 = RegNext(VecInit.tabulate(2) { i => 
    level10(2*i) +& level10(2*i + 1)
  })
  
  val level12 = RegNext(level11(0) +& level11(1))
  
  io.out := level12
}

class AdderTree2048(bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(Vec(2048, UInt(bitWidth.W)))
    val out = Output(UInt((bitWidth + 11).W)) 
  })
  
  // Pipeline stages (11 levels for 2048 inputs)
  val level1 = RegNext(VecInit.tabulate(1024) { i => 
    io.in(2*i) +& io.in(2*i + 1)
  })
  
  val level2 = RegNext(VecInit.tabulate(512) { i => 
    level1(2*i) +& level1(2*i + 1)
  })
  
  val level3 = RegNext(VecInit.tabulate(256) { i => 
    level2(2*i) +& level2(2*i + 1)
  })
  
  val level4 = RegNext(VecInit.tabulate(128) { i => 
    level3(2*i) +& level3(2*i + 1)
  })
  
  val level5 = RegNext(VecInit.tabulate(64) { i => 
    level4(2*i) +& level4(2*i + 1)
  })
  
  val level6 = RegNext(VecInit.tabulate(32) { i => 
    level5(2*i) +& level5(2*i + 1)
  })
  
  val level7 = RegNext(VecInit.tabulate(16) { i => 
    level6(2*i) +& level6(2*i + 1)
  })
  
  val level8 = RegNext(VecInit.tabulate(8) { i => 
    level7(2*i) +& level7(2*i + 1)
  })
  
  val level9 = RegNext(VecInit.tabulate(4) { i => 
    level8(2*i) +& level8(2*i + 1)
  })

  val level10 = RegNext(VecInit.tabulate(2) { i => 
    level9(2*i) +& level9(2*i + 1)
  })
  
  val level11 = RegNext(level10(0) +& level10(1))
  
  io.out := level11
}

class AdderTree1024(bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(Vec(1024, UInt(bitWidth.W)))
    val out = Output(UInt((bitWidth + 10).W))
  })
  
  // Pipeline stages (10 levels for 1024 inputs)
  val level1 = RegNext(VecInit.tabulate(512) { i => 
    io.in(2*i) +& io.in(2*i + 1)
  })
  
  val level2 = RegNext(VecInit.tabulate(256) { i => 
    level1(2*i) +& level1(2*i + 1)
  })
  
  val level3 = RegNext(VecInit.tabulate(128) { i => 
    level2(2*i) +& level2(2*i + 1)
  })
  
  val level4 = RegNext(VecInit.tabulate(64) { i => 
    level3(2*i) +& level3(2*i + 1)
  })
  
  val level5 = RegNext(VecInit.tabulate(32) { i => 
    level4(2*i) +& level4(2*i + 1)
  })
  
  val level6 = RegNext(VecInit.tabulate(16) { i => 
    level5(2*i) +& level5(2*i + 1)
  })
  
  val level7 = RegNext(VecInit.tabulate(8) { i => 
    level6(2*i) +& level6(2*i + 1)
  })
  
  val level8 = RegNext(VecInit.tabulate(4) { i => 
    level7(2*i) +& level7(2*i + 1)
  })
  
  val level9 = RegNext(VecInit.tabulate(2) { i => 
    level8(2*i) +& level8(2*i + 1)
  })
  
  val level10 = RegNext(level9(0) +& level9(1))
  
  io.out := level10
}

class AdderTree512(bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(Vec(512, UInt(bitWidth.W)))
    val out = Output(UInt((bitWidth + 9).W))
  })
  
  // Pipeline stages (9 levels for 512 inputs)
  val level1 = RegNext(VecInit.tabulate(256) { i => 
    io.in(2*i) +& io.in(2*i + 1)
  })
  
  val level2 = RegNext(VecInit.tabulate(128) { i => 
    level1(2*i) +& level1(2*i + 1)
  })
  
  val level3 = RegNext(VecInit.tabulate(64) { i => 
    level2(2*i) +& level2(2*i + 1)
  })
  
  val level4 = RegNext(VecInit.tabulate(32) { i => 
    level3(2*i) +& level3(2*i + 1)
  })
  
  val level5 = RegNext(VecInit.tabulate(16) { i => 
    level4(2*i) +& level4(2*i + 1)
  })
  
  val level6 = RegNext(VecInit.tabulate(8) { i => 
    level5(2*i) +& level5(2*i + 1)
  })
  
  val level7 = RegNext(VecInit.tabulate(4) { i => 
    level6(2*i) +& level6(2*i + 1)
  })
  
  val level8 = RegNext(VecInit.tabulate(2) { i => 
    level7(2*i) +& level7(2*i + 1)
  })
  
  val level9 = RegNext(level8(0) +& level8(1))
  
  io.out := level9
}

class AdderTree256(bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(Vec(256, UInt(bitWidth.W)))
    val out = Output(UInt((bitWidth + 8).W))
  })
  
  // Pipeline stages (8 levels for 256 inputs)
  val level1 = RegNext(VecInit.tabulate(128) { i => 
    io.in(2*i) +& io.in(2*i + 1)
  })
  
  val level2 = RegNext(VecInit.tabulate(64) { i => 
    level1(2*i) +& level1(2*i + 1)
  })
  
  val level3 = RegNext(VecInit.tabulate(32) { i => 
    level2(2*i) +& level2(2*i + 1)
  })
  
  val level4 = RegNext(VecInit.tabulate(16) { i => 
    level3(2*i) +& level3(2*i + 1)
  })
  
  val level5 = RegNext(VecInit.tabulate(8) { i => 
    level4(2*i) +& level4(2*i + 1)
  })
  
  val level6 = RegNext(VecInit.tabulate(4) { i => 
    level5(2*i) +& level5(2*i + 1)
  })
  
  val level7 = RegNext(VecInit.tabulate(2) { i => 
    level6(2*i) +& level6(2*i + 1)
  })
  
  val level8 = RegNext(level7(0) +& level7(1))
  
  io.out := level8
}

class AdderTree128(bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(Vec(128, UInt(bitWidth.W)))
    val out = Output(UInt((bitWidth + 7).W))
  })
  
  // Pipeline stages (7 levels for 128 inputs)
  val level1 = RegNext(VecInit.tabulate(64) { i => 
    io.in(2*i) +& io.in(2*i + 1)
  })
  
  val level2 = RegNext(VecInit.tabulate(32) { i => 
    level1(2*i) +& level1(2*i + 1)
  })
  
  val level3 = RegNext(VecInit.tabulate(16) { i => 
    level2(2*i) +& level2(2*i + 1)
  })
  
  val level4 = RegNext(VecInit.tabulate(8) { i => 
    level3(2*i) +& level3(2*i + 1)
  })
  
  val level5 = RegNext(VecInit.tabulate(4) { i => 
    level4(2*i) +& level4(2*i + 1)
  })
  
  val level6 = RegNext(VecInit.tabulate(2) { i => 
    level5(2*i) +& level5(2*i + 1)
  })
  
  val level7 = RegNext(level6(0) +& level6(1))
  
  io.out := level7
}

class AdderTree64(bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(Vec(64, UInt(bitWidth.W)))
    val out = Output(UInt((bitWidth + 6).W))
  })
  
  // Pipeline stages (6 levels for 64 inputs)
  val level1 = RegNext(VecInit.tabulate(32) { i => 
    io.in(2*i) +& io.in(2*i + 1)
  })
  
  val level2 = RegNext(VecInit.tabulate(16) { i => 
    level1(2*i) +& level1(2*i + 1)
  })
  
  val level3 = RegNext(VecInit.tabulate(8) { i => 
    level2(2*i) +& level2(2*i + 1)
  })
  
  val level4 = RegNext(VecInit.tabulate(4) { i => 
    level3(2*i) +& level3(2*i + 1)
  })
  
  val level5 = RegNext(VecInit.tabulate(2) { i => 
    level4(2*i) +& level4(2*i + 1)
  })
  
  val level6 = RegNext(level5(0) +& level5(1))
  
  io.out := level6
}

class AdderTree32(bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(Vec(32, UInt(bitWidth.W)))
    val out = Output(UInt((bitWidth + 5).W))
  })
  
  // Pipeline stages (5 levels for 32 inputs)
  val level1 = RegNext(VecInit.tabulate(16) { i => 
    io.in(2*i) +& io.in(2*i + 1)
  })
  
  val level2 = RegNext(VecInit.tabulate(8) { i => 
    level1(2*i) +& level1(2*i + 1)
  })
  
  val level3 = RegNext(VecInit.tabulate(4) { i => 
    level2(2*i) +& level2(2*i + 1)
  })
  
  val level4 = RegNext(VecInit.tabulate(2) { i => 
    level3(2*i) +& level3(2*i + 1)
  })

  val level5 = RegNext(level4(0) +& level4(1))
  
  io.out := level5
}

class AdderTree16(bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(Vec(16, UInt(bitWidth.W)))
    val out = Output(UInt((bitWidth + 4).W))
  })
  
  // Pipeline stages (4 levels for 16 inputs)
  val level1 = RegNext(VecInit.tabulate(8) { i => 
    io.in(2*i) +& io.in(2*i + 1)
  })
  
  val level2 = RegNext(VecInit.tabulate(4) { i => 
    level1(2*i) +& level1(2*i + 1)
  })
  
  val level3 = RegNext(VecInit.tabulate(2) { i => 
    level2(2*i) +& level2(2*i + 1)
  })

  val level4 = RegNext(level3(0) +& level3(1))
  
  io.out := level4
}

class AdderTree8(bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(Vec(8, UInt(bitWidth.W)))
    val out = Output(UInt((bitWidth + 3).W))
  })
  
  // Pipeline stages (3 levels for 8 inputs)
  val level1 = RegNext(VecInit.tabulate(4) { i => 
    io.in(2*i) +& io.in(2*i + 1)
  })
  
  val level2 = RegNext(VecInit.tabulate(2) { i => 
    level1(2*i) +& level1(2*i + 1)
  })

  val level3 = RegNext(level2(0) +& level2(1))
  
  io.out := level3
}

class AdderTree4(bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(Vec(4, UInt(bitWidth.W)))
    val out = Output(UInt((bitWidth + 2).W))
  })
  
  // Pipeline stages (2 levels for 4 inputs)
  val level1 = RegNext(VecInit.tabulate(2) { i => 
    io.in(2*i) +& io.in(2*i + 1)
  })

  val level2 = RegNext(level1(0) +& level1(1))
  
  io.out := level2
}

class AdderTree2(bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(Vec(2, UInt(bitWidth.W)))
    val out = Output(UInt((bitWidth + 1).W))
  })
  
  // Pipeline stages (1 level for 2 inputs)
  val level1 = RegNext(io.in(0) +& io.in(1))
  
  io.out := level1
}

// I know this way of doing it is a bit weird, because you could just parametrize the whole thing.
// However, Tywaves has trouble reconstructing the types, resulting in the entire bitvector being dumped on
// the DPDG nodes. This, in turn results in massive slowdown in the simulation data injection part of ChiselTrace.
// Not ideal for performance measurements....
// We avoid this problem by being more explicit about the design.
class AdderTree(nInputs: Int, bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(Vec(nInputs, UInt(bitWidth.W)))
    val out = Output(UInt((bitWidth + log2Ceil(nInputs + 1)).W))
  })

  nInputs match {
    case 4096 => {
      val adder = Module(new AdderTree4096(bitWidth))
      io <> adder.io
    }
    case 2048 => {
      val adder = Module(new AdderTree2048(bitWidth))
      io <> adder.io
    }
    case 1024 => {
      val adder = Module(new AdderTree1024(bitWidth))
      io <> adder.io
    }
    case 512 => {
      val adder = Module(new AdderTree512(bitWidth))
      io <> adder.io
    }
    case 256 => {
      val adder = Module(new AdderTree256(bitWidth))
      io <> adder.io
    }
    case 128 => {
      val adder = Module(new AdderTree128(bitWidth))
      io <> adder.io
    }
    case 64 => {
      val adder = Module(new AdderTree64(bitWidth))
      io <> adder.io
    }
    case 32 => {
      val adder = Module(new AdderTree32(bitWidth))
      io <> adder.io
    }
    case 16 => {
      val adder = Module(new AdderTree16(bitWidth))
      io <> adder.io
    }
    case 8 => {
      val adder = Module(new AdderTree8(bitWidth))
      io <> adder.io
    }
    case 4 => {
      val adder = Module(new AdderTree4(bitWidth))
      io <> adder.io
    }
    case 2 => {
      val adder = Module(new AdderTree2(bitWidth))
      io <> adder.io
    }

    case _ => throw new Exception("Unsupported input width")
  }
}

class AdderTreeTester extends AnyFunSpec with Matchers {
  describe("ParametricSimulator") {
    it("runs AdderTree correctly") {
      import ChiselTraceDebugger._

      val nInputs = 2048 
      // for (i <- 0 until 100) {
      val chiselStage = new ChiselStage(true)
      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new AdderTree(nInputs, 1)),
          circt.stage.FirtoolOption("-g"),
          circt.stage.FirtoolOption("--emit-hgldd"),
        ),
      )
      // }
      println("Hello, world!")
      // for (i <- 0 until 100) {
        simulate(new AdderTree(nInputs, 1), Seq(VcdTrace, WithTywavesWaveforms(true))) { dut =>
          val testValues = List.fill(nInputs)(1) // Just set every input to 1
          testValues.zipWithIndex.foreach{
            case (value, idx) => dut.io.in(idx).poke(value)
          }
          dut.clock.step(50)
        }
      // }
    }
  }
}
