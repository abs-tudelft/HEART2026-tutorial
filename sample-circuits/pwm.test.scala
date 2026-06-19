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

class Counter extends Module {
    val io = IO(new Bundle {
        val full_cycle = Input(UInt(32.W))
        val duty_cycle = Input(UInt(32.W))
        val pwm_out = Output(Bool())
    })

    val count = RegInit(1.U(32.W))
    val gen_signal = RegInit(false.B)

    when(count < io.duty_cycle) {
        gen_signal := true.B
        count := count + 1.U
    } .elsewhen(count < io.full_cycle) {
        gen_signal := false.B
        count := count + 1.U
    } .otherwise {
        gen_signal := true.B
        count := 1.U
    }

    io.  := gen_signal
}

class PWM extends Module {
    val io = IO(new Bundle {
        val full_cycle_count = Input(UInt(32.W))
        val duty_cycle_count = Input(UInt(32.W))
        val load_values = Input(Bool())
        val pwm_out = Output(Bool())
    })

    val counter_target = RegInit(20.U(32.W))
    val duty_cycle = RegInit(10.U(32.W))

    when(io.load_values) {
        counter_target := io.full_cycle_count
        duty_cycle := io.duty_cycle_count
    }

    val counter = Module(new Counter())

    counter.io.full_cycle := counter_target
    counter.io.duty_cycle := duty_cycle
    io.pwm_out := counter.io.pwm_out
}

class PWMTest extends AnyFunSpec with Matchers {
  /*describe("ParametricSimulator") {
    it("runs PWM correctly") {

      val chiselStage = new ChiselStage(true)
      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new PWM()),
          circt.stage.FirtoolOption("-g"),
          circt.stage.FirtoolOption("--emit-hgldd"),
        ),
      )
      println("Hello, world!")
      simulate(new PWM(), Seq(VcdTrace, SaveWorkdirFile("gcdWorkdir"))) { dut =>
        dut.io.full_cycle_count.poke(20.U)
        dut.io.duty_cycle_count.poke(10.U)
        dut.io.load_values.poke(true.B)
        dut.clock.step()
        dut.io.load_values.poke(false.B)
        dut.clock.step(40)
      }
    }
  }*/

  describe("TywavesSimulator") {
    it("runs PWM correctly") {
      import ChiselTraceDebugger._


      val chiselStage = new ChiselStage(true)
      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new PWM()),
          circt.stage.FirtoolOption("-g"),
          circt.stage.FirtoolOption("--emit-hgldd"),
        ),
      )

      simulate(new PWM(), Seq(VcdTrace, WithTywavesWaveforms(true)), simName = "runs_pwm_correctly_launch_tywaves") {
        dut =>
            dut.io.full_cycle_count.poke(20.U)
            dut.io.duty_cycle_count.poke(10.U)
            dut.io.load_values.poke(true.B)
            dut.clock.step()
            dut.io.load_values.poke(false.B)
            dut.clock.step(40)
      }
    }
  }

}
