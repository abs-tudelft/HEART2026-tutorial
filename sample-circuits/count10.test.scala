//> using scala "2.13.14"
//> using dep "com.github.rameloni::tywaves-chisel-api:0.4.2-chiseltrace-SNAPSHOT"
//> using dep "org.chipsalliance::chisel:6.4.3-tywaves-chiseltrace-SNAPSHOT"
//> using plugin "org.chipsalliance:::chisel-plugin:6.4.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"
//> using dep "org.scalatest::scalatest:3.2.18"

// Source: https://github.com/jarlb/tywaves-chisel repository, examples folder

import chisel3._
import circt.stage._
import chisel3.util._
import tywaves.simulator._
import tywaves.simulator.simulatorSettings._

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers


class Adder extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(8.W))
    val b = Input(UInt(8.W))
    val y = Output(UInt(8.W))
  })

  io.y := io.a + io.b;
}

class Register extends Module {
  val io = IO(new Bundle {
    val d = Input(UInt(8.W))
    val q = Output(UInt(8.W))
  })

  val reg = RegInit(0.U);
  reg := io.d
  io.q := reg
}

class Count10 extends Module {
  val io = IO(new Bundle {
    val dout = Output(UInt(8.W))
  })


  val add = Module(new Adder())
  val reg = Module(new Register())

  val count = reg.io.q

  add.io.a := count
  add.io.b := 1.U

  val result = add.io.y

  val next = Mux(count === 9.U, 0.U, result)
  reg.io.d := next

  io.dout := count
}

class Count10Test extends AnyFunSpec with Matchers {

  describe("ChiselTraceDebugger") {
    it("runs count10 correctly") {
      import ChiselTraceDebugger._


      val chiselStage = new ChiselStage(true)
      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new BlackBoxTester()),
          circt.stage.FirtoolOption("-g"),
          circt.stage.FirtoolOption("--emit-hgldd"),
        ),
      )

      simulate(new Count10(), Seq(VcdTrace, WithTywavesWaveforms(true)), simName = "runs_Count10_correctly_launch_tywaves") {
        dut =>
            dut.clock.stepUntil(sentinelPort = dut.io.dout, sentinelValue = 9, maxCycles = 10)
      }
    }
  }

}