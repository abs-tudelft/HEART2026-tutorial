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

// This file is supposed to be a simple test case to test the compound signal splitting in the PDG extractor.
// There is no other functionality in here.

class ExampleSignal extends Bundle {
  val addr = Input(UInt(2.W))
  val addr2 = Input(UInt(1.W))
  val data = Vec(3, new Bundle {
    val x = Input(UInt(32.W))
    val y = Output(UInt(32.W))
  })
}

class ExampleRegFile extends Module {
  val io = IO(new Bundle {
    val signal1 = Vec(2, new ExampleSignal())
  })

  // Test case 1: Full path connection (Input -> Output)
  io.signal1(0).data(0).y := io.signal1(0).data(0).x
  
  
  // Test case 2: Compound signal connections (Inputs -> Outputs)
  for (i <- 0 until 3) {
    // Connect first port's inputs to second port's outputs
    io.signal1(0).data(i).y := io.signal1(0).data(i).x
    io.signal1(1).data(i).y := io.signal1(0).data(i).x
  }
}

class ExampleRegFileTester extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(32.W))
    val out = Output(UInt(32.W))
  })

  val regFile = Module(new ExampleRegFile())
  
  // Connect external input to first port's data(0).x
  regFile.io.signal1(0).data(0).x := io.in
  
  // Connect second port's data(2).y to output
  io.out := regFile.io.signal1(1).data(2).y
  
  // Connect remaining inputs to defaults
  regFile.io.signal1.foreach { sig =>
    sig.addr := 0.U
    sig.addr2 := 0.U
    sig.data.foreach(_.x := 0.U)
  }
}

class RegFileTest extends AnyFunSpec with Matchers {
  describe("ParametricSimulator") {
    it("runs ExampleRegFileTester correctly") {
      import TywavesSimulator._

      val chiselStage = new ChiselStage(true)
      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new ExampleRegFileTester()),
          circt.stage.FirtoolOption("-g"),
          circt.stage.FirtoolOption("--emit-hgldd"),
        ),
      )
      println("Hello, world!")
      simulate(new ExampleRegFileTester(), Seq(VcdTrace, WithTywavesWaveforms(true))) { dut =>
        dut.clock.step()
        dut.io.in.poke(1.U)
        dut.clock.step(40)
      }
    }
  }
}
