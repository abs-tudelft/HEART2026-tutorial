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

class RegFile extends Module {
    val io = IO(new Bundle {
        val r_addr = Input(UInt(2.W))
        val w_addr = Input(UInt(2.W))
        val w_data = Input(UInt(32.W))
        val w_en = Input(Bool())
        val r_data = Output(UInt(32.W))
    })

    val registers = RegInit(VecInit(Seq.fill(4)(0.U(32.W))))

    when(io.w_en) {
      registers(io.w_addr) := io.w_data
    }

    io.r_data := registers(io.r_addr)
}

class RegFileTester extends Module {
    val io = IO(new Bundle {
      val en = Input(Bool())
      val res = Output(UInt(32.W))
    })

    val regfile = Module(new RegFile())
    val counter = RegInit(0.U(2.W))

    when(io.en) {
      counter := counter + 1.U
    }

    regfile.io.r_addr := counter
    regfile.io.w_addr := counter
    regfile.io.w_en := io.en

    regfile.io.w_data := regfile.io.r_data + 1.U

    io.res := regfile.io.r_data
}

class RegFileTest extends AnyFunSpec with Matchers {
  describe("ParametricSimulator") {
    it("runs RegFileTester correctly") {
      import ChiselTraceDebugger._

      val chiselStage = new ChiselStage(true, true)
      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new RegFileTester()),
          circt.stage.FirtoolOption("-g"),
          circt.stage.FirtoolOption("--emit-hgldd"),
        ),
      )
      println("Hello, world!")
      simulate(new RegFileTester(), Seq(VcdTrace, WithTywavesWaveforms(true))) { dut =>
        // dut.clock.step()
        dut.io.en.poke(true.B)
        dut.clock.step(40)
        dut.io.res.expect(0.U)
      }
    }
  }
}
