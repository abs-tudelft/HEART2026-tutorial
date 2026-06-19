//> using scala "2.13.14"
//> using dep "com.github.rameloni::tywaves-chisel-api:0.4.2-chiseltrace-SNAPSHOTace-SNAPSHOT"
//> using dep "org.chipsalliance::chisel:6.4.3-tywaves-chiseltrace-SNAPSHOT"
//> using plugin "org.chipsalliance:::chisel-plugin:6.4.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"
//> using dep "org.scalatest::scalatest:3.2.18"

// Source: https://github.com/jarlb/tywaves-chisel repository, examples folder

// DO NOT EDIT THE ORTHER OF THESE IMPORTS (it will be solved in future versions)
import chisel3._
import chisel3.experimental.{HasTypeAlias, RecordAlias}
import tywaves.simulator._
import tywaves.simulator.ParametricSimulator._
import tywaves.simulator.simulatorSettings._
import circt.stage.ChiselStage
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
//import _root_.circt.stage.ChiselStage
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class AliasedIO extends Bundle with HasTypeAlias {
  override def aliasName = RecordAlias("AliasedIO")
  val in = Flipped(UInt(8.W))
  val out = UInt(8.W)
}

class AliasExample extends Module {
  val io = IO(new AliasedIO)

  io.out := io.in
}

class AliasExampleTest extends AnyFunSpec with Matchers {
  describe("ParametricSimulator") {
    it("runs AliasExample correctly") {
      import ChiselTraceDebugger._

      val chiselStage = new ChiselStage(true)
      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new AliasExample()),
          circt.stage.FirtoolOption("-g"),
          circt.stage.FirtoolOption("--emit-hgldd"),
        ),
      )
      println("Hello, world!")
      simulate(new AliasExample(), Seq(VcdTrace, WithTywavesWaveforms(true))) { dut =>
        dut.io.in.poke(1.U)
        dut.clock.step()
      }
    }
  }
}
