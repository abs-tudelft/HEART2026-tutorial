//> using scala "2.13.14"
//> using dep "com.github.rameloni::tywaves-chisel-api:0.4.2-chiseltrace-SNAPSHOT"
//> using dep "org.chipsalliance::chisel:6.4.3-tywaves-chiseltrace-SNAPSHOT"
//> using plugin "org.chipsalliance:::chisel-plugin:6.4.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"
//> using dep "org.scalatest::scalatest:3.2.18"

// Source: https://github.com/jarlb/tywaves-chisel repository, examples folders

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

// This is a simple circuit that allows for a visualisation of control flow reconstruction
// and dynamic data flow reconstruction
class ControlFlowExampleTop extends Module {
  val io = IO(new Bundle {
    val addr1 = Input(UInt(1.W))
    val addr2 = Input(UInt(1.W))
    val muxSel = Input(Bool())
    val branchSel = Input(UInt(2.W))
    val wData = Input(UInt(32.W))
    val out = Output(UInt(32.W))
  })

  val regs = Reg(Vec(2, Vec(2, UInt(32.W))))
  val altOutput = WireDefault(0.U)

  when (io.branchSel === 0.U) {
    regs(io.addr1)(io.addr2) := io.wData
  } .elsewhen (io.branchSel === 1.U) {
    regs(io.addr1)(io.addr2) := io.wData + 1.U
  } .elsewhen (io.branchSel === 2.U) {
    regs(io.addr1)(io.addr2) := io.wData + 2.U
  } .otherwise {
    regs(io.addr1)(io.addr2) := io.wData + 3.U
  }
  
  io.out :=  Mux(io.muxSel, altOutput, regs(io.addr1)(io.addr2))
}

// Use the following to demonstrate a limitation of the FIRRTL based reconstruction. The problem is that Chisel will inline regsOut,
// causing a this line to not be present on the DPDG!
// val regsOut = regs(io.addr1)(io.addr2)

class ControlFlowExampleTopTest extends AnyFunSpec with Matchers {
  describe("ParametricSimulator") {
    it("runs ControlFlowExampleTop correctly") {
      import ChiselTraceDebugger._

      val chiselStage = new ChiselStage(true, true)
      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new ControlFlowExampleTop()),
          circt.stage.FirtoolOption("-g"),
          circt.stage.FirtoolOption("--emit-hgldd"),
        ),
      )
      println("Hello, world!")
      simulate(new ControlFlowExampleTop(), Seq(VcdTrace, WithTywavesWaveforms(true))) { dut =>
        dut.io.addr1.poke(1.U)
        dut.io.addr2.poke(1.U)
        dut.io.muxSel.poke(false.B)
        dut.io.branchSel.poke(2.U)
        dut.io.wData.poke(1.U)
        dut.clock.step(4)
      }
    }
  }
}
