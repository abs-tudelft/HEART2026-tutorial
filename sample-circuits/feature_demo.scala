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

class ExamplePipeline extends Module {
    val io = IO(new Bundle {
        val valid_in = Input(Bool())
        val in1 = Input(UInt(8.W))
        val in2 = Input(UInt(8.W))
        val out = Output(UInt(8.W))
        val valid_out = Output(Bool())
    })

    io.valid_out := RegNext(RegNext(io.valid_in))
    val p_registers = RegInit(VecInit(Seq.fill(4)(0.U(8.W))))

    p_registers(0) := io.in1 ^ "b10101010".U
    p_registers(1) := io.in2 ^ "b10101010".U

    p_registers(2) := p_registers(0) + 1.U
    p_registers(3) := p_registers(1) + 1.U

    val in1_biggest = p_registers(2) > p_registers(3)
    io.out := Mux(in1_biggest, p_registers(2), p_registers(3))
}

class PipelineTester extends Module {
    val io = IO(new Bundle {
        val en = Input(Bool())
        val in1 = Input(UInt(8.W))
        val in2 = Input(UInt(8.W))
        val w_addr = Input(UInt(2.W))
        val out = Output(UInt(16.W))
    })
    val regfile = RegInit(VecInit(Seq.fill(4)(0.U(8.W))))

    val pipeline = Module(new ExamplePipeline())
    pipeline.io.valid_in := io.en
    pipeline.io.in1 := io.in1
    pipeline.io.in2 := io.in2

    io.out := regfile(io.w_addr)

    when(pipeline.io.valid_out) {
        regfile(io.w_addr) := regfile(io.w_addr) + pipeline.io.out
    }
}

class PipelineTesterTest extends AnyFunSpec with Matchers {
  describe("ParametricSimulator") {
    it("runs PipelineTester correctly") {
      import TywavesSimulator._

      val chiselStage = new ChiselStage(true)
      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new PipelineTester()),
          circt.stage.FirtoolOption("-g"),
          circt.stage.FirtoolOption("--emit-hgldd"),
        ),
      )
      println("Hello, world!")
      simulate(new PipelineTester(), Seq(VcdTrace, WithTywavesWaveforms(true))) { dut =>
        dut.clock.step(1)
        dut.io.en.poke(true.B)
        dut.io.in1.poke(1.U)
        dut.io.in2.poke(2.U)
        dut.io.w_addr.poke(1.U)
        dut.clock.step()
        dut.io.en.poke(false.B)
        dut.clock.step(3)
      }
    }
  }
}
