//> using scala "2.13.14"
//> using dep "com.github.rameloni::tywaves-chisel-api:0.4.2-chiseltrace-SNAPSHOT"
//> using dep "org.chipsalliance::chisel:6.4.3-tywaves-chiseltrace-SNAPSHOT"
//> using plugin "org.chipsalliance:::chisel-plugin:6.4.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"
//> using dep "org.scalatest::scalatest:3.2.18"

// Source: https://github.com/jarlb/tywaves-chisel repository, examples folder

// DO NOT EDIT THE ORTHER OF THESE IMPORTS (it will be solved in future versions)
import chisel3._
import chisel3.util.{SRAMInterface, SRAM}
import tywaves.simulator._
import tywaves.simulator.ParametricSimulator._
import tywaves.simulator.simulatorSettings._
import circt.stage.ChiselStage
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
//import _root_.circt.stage.ChiselStage
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

// Example partially taken from the Chisel Documentation
class SRAMTopModule extends Module {
  // Declare a 1 read, 1 write ported SRAM with 8-bit UInt data members
  val mem = SRAM(1024, UInt(8.W), 1, 1, 0)

  // Whenever we want to read from the first read port
  mem.readPorts(0).address := 100.U
  mem.readPorts(0).enable := true.B

  // Read data is returned one cycle after enable is driven
  val foo = WireInit(UInt(8.W), mem.readPorts(0).data)

  // Whenever we want to write to the second write port
  mem.writePorts(0).address := 5.U
  mem.writePorts(0).enable := true.B
  mem.writePorts(0).data := 12.U
}

class RegFileTest extends AnyFunSpec with Matchers {
  describe("ParametricSimulator") {
    it("runs SRAMTopModule correctly") {
      import TywavesSimulator._

      val chiselStage = new ChiselStage(true)
      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new SRAMTopModule()),
          circt.stage.FirtoolOption("-g"),
          circt.stage.FirtoolOption("--emit-hgldd"),
        ),
      )
      println("Hello, world!")
      simulate(new SRAMTopModule(), Seq(VcdTrace, WithTywavesWaveforms(true))) { dut =>
        dut.clock.step(40)
      }
    }
  }
}
