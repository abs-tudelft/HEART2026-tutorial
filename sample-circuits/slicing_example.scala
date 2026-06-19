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

class SlicingExample extends Module {
  val io = IO(new Bundle {
    val out = Output(UInt(8.W))
  })

  val x = RegInit(0.U(8.W))
  when(x > 4.U) {
    x := 0.U
  }.elsewhen(x > 0.U) {
    x := x + 2.U
  }.otherwise {
    x := x + 1.U
  }

  io.out := x
}

class SlicingExampleTest extends AnyFunSpec with Matchers {
  describe("ParametricSimulator") {
    it("runs SlicingExample correctly") {
      import TywavesSimulator._

      val chiselStage = new ChiselStage(true)
      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new SlicingExample()),
          circt.stage.FirtoolOption("-g"),
          circt.stage.FirtoolOption("--emit-hgldd"),
        ),
      )
      println("Hello, world!")
      simulate(new SlicingExample(), Seq(VcdTrace, WithTywavesWaveforms(true))) { dut =>
        dut.clock.step(3)
      }
    }
  }
}
