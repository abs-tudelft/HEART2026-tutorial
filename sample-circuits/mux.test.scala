//> using scala "2.13.14"
//> using dep "com.github.rameloni::tywaves-chisel-api:0.4.2-chiseltrace-SNAPSHOT"
//> using dep "org.chipsalliance::chisel:6.4.3-tywaves-chiseltrace-SNAPSHOT"
//> using plugin "org.chipsalliance:::chisel-plugin:6.4.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"
//> using dep "org.scalatest::scalatest:3.2.18"

// Source: https://github.com/jarlb/tywaves-chisel repository, examples folder

// DO NOT EDIT THE ORTHER OF THESE IMPORTS (it will be solved in future versions)
import chisel3._
import tywaves.simulator._
import tywaves.simulator.ParametricSimulator._
import tywaves.simulator.simulatorSettings._
import circt.stage.ChiselStage
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
//import _root_.circt.stage.ChiselStage
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

// Bundle definition for compound signals
class DataBundle extends Bundle {
  val a = UInt(8.W)
  val b = Bool()
  val c = UInt(4.W)
}

class MuxExample extends Module {
  val io = IO(new Bundle {
    val sel = Input(Bool())
    val in0 = Input(new DataBundle())
    val in1 = Input(new DataBundle())
    val out = Output(new DataBundle())
  })

  // Single MUX operating on the entire compound signal
  io.out := Mux(io.sel, io.in1, io.in0)
}

class MuxExampleTest extends AnyFunSpec with Matchers {
  describe("ParametricSimulator") {
    it("runs MuxExample correctly") {
      import TywavesSimulator._

      val chiselStage = new ChiselStage(true)
      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new MuxExample()),
          circt.stage.FirtoolOption("-g"),
          circt.stage.FirtoolOption("--emit-hgldd"),
        ),
      )
      println("Hello, world!")
      simulate(new MuxExample(), Seq(VcdTrace, WithTywavesWaveforms(true))) { dut =>
        dut.clock.step()
        dut.io.sel.poke(true.B)
        dut.io.in0.a.poke(10.U)  
        dut.io.in0.b.poke(false.B)
        dut.io.in0.c.poke(3.U)
        
        dut.io.in1.a.poke(20.U)
        dut.io.in1.b.poke(true.B)
        dut.io.in1.c.poke(7.U)
        dut.clock.step(40)
      }
    }
  }
}
