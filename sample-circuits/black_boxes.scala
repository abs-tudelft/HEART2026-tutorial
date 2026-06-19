//> using scala "2.13.14"
//> using dep "com.github.rameloni::tywaves-chisel-api:0.4.2-chiseltrace-SNAPSHOT"
//> using dep "org.chipsalliance::chisel:6.4.3-tywaves-chiseltrace-SNAPSHOT"
//> using plugin "org.chipsalliance:::chisel-plugin:6.4.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"
//> using dep "org.scalatest::scalatest:3.2.18"

// Source: https://github.com/jarlb/tywaves-chisel repository, examples folder

// DO NOT EDIT THE ORTHER OF THESE IMPORTS (it will be solved in future versions)
import chisel3._
import chisel3.util.HasBlackBoxInline
import tywaves.simulator._
import tywaves.simulator.ParametricSimulator._
import tywaves.simulator.simulatorSettings._
import circt.stage.ChiselStage
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
//import _root_.circt.stage.ChiselStage
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class BlackBoxXOR extends BlackBox with HasBlackBoxInline {
  val io = IO(new Bundle {
    val in1 = Input(UInt(64.W))
    val in2 = Input(UInt(64.W))
    val out = Output(UInt(64.W))
  })
  setInline("BlackBoxXOR.v",
    """module BlackBoxXOR(
      |    input  [63:0] in1,
      |    input  [63:0] in2,
      |    output [63:0] out
      |);
      |assign out = in1 ^ in2;
      |endmodule
    """.stripMargin)
}

class BlackBoxTester extends Module {
  val io = IO(new Bundle {
    val out = Output(UInt(64.W))
  })
  val blbox = Module(new BlackBoxXOR)
  blbox.io.in1 := 0.U
  blbox.io.in2 := 0.U
  io.out := blbox.io.out
}

class RegFileTest extends AnyFunSpec with Matchers {
  describe("ParametricSimulator") {
    it("runs BlackBoxTester correctly") {
      import TywavesSimulator._

      val chiselStage = new ChiselStage(true)
      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new BlackBoxTester()),
          circt.stage.FirtoolOption("-g"),
          circt.stage.FirtoolOption("--emit-hgldd"),
        ),
      )
      println("Hello, world!")
      simulate(new BlackBoxTester(), Seq(VcdTrace, WithTywavesWaveforms(true))) { dut =>
        dut.clock.step(5)
      }
    }
  }
}
