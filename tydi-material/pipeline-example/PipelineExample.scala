package pipeline

import tydi_lib._
import chisel3._
import chisel3.internal.firrtl.Width
import chisel3.util.Counter
import chiseltest.RawTester.test
import circt.stage.ChiselStage.{emitCHIRRTL, emitSystemVerilog}

/** Basic data-types used in groups etc. */
trait PipelineTypes {
  val dataWidth: Width = 64.W
  def signedData: SInt = SInt(dataWidth)
  def unsingedData: UInt = UInt(dataWidth)
}
 
/** A basic Group element with a timestamp and a value. */
class NumberGroup extends Group with PipelineTypes {
  val time: UInt = UInt(64.W)
  val value: SInt = signedData
}
 
/** Statistics output group. */
class Stats extends Group with PipelineTypes {
  val min: UInt = unsingedData
  val max: UInt = unsingedData
  val sum: UInt = unsingedData
  val average: UInt = unsingedData
}

/** A module based on a stream-processing base with input and output streams of type `NumberGroup`.
 * Input and output streams are passthrough-connected by default so only meaningful signals are overridden.  */
class NonNegativeFilter extends SubProcessorBase(new NumberGroup, new NumberGroup) {
  outStream.strb := inStream.strb(0) && inStream.el.value >= 0.S
}

/** Another streaming module that calculates some statistics of the incoming stream. */
class Reducer extends SubProcessorBase(new NumberGroup, new Stats) with PipelineTypes {
  val maxVal: BigInt = BigInt(Long.MaxValue)  // Must work with BigInt or we get an overflow
  val cMin: UInt = RegInit(maxVal.U(dataWidth))
  val cMax: UInt = RegInit(0.U(dataWidth))
  val nValidSamples: Counter = Counter(Int.MaxValue)
  val nSamples: Counter = Counter(Int.MaxValue)
  val cSum: UInt = RegInit(0.U(dataWidth))

  inStream.ready := true.B
  outStream.valid := nSamples.value > 0.U

  when (inStream.valid) {
    val value = inStream.el.value.asUInt
    nSamples.inc()
    when (inStream.strb(0)) {
      cMin := cMin min value
      cMax := cMax max value
      cSum := cSum + value
      nValidSamples.inc()
    }
  }
  outStream.el.sum := cSum
  outStream.el.min := cMin
  outStream.el.max := cMax
  outStream.el.average := Mux(nValidSamples.value > 0.U, cSum/nValidSamples.value, 0.U)
}

/** Using the stream processing modules, connecting them manually. */
/*class PipelineExampleModule extends TydiModule {
  private val numberGroup = new NumberGroup
  private val statsGroup = new Stats

  // Create and connect physical streams following standard with concatenated data bitvector
  val numsIn: PhysicalStream = IO(Flipped(PhysicalStream(numberGroup, 1, c = 7)))
  val statsOut: PhysicalStream = IO(PhysicalStream(statsGroup, 1, c = 7))

  val filter = Module(new NonNegativeFilter())
  filter.in := numsIn
  val reducer = Module(new Reducer())
  reducer.in := filter.out
  statsOut := reducer.out
}*/

/** Using the stream processing modules with chaining syntax.
 * SimpleProcessorBase is similar to SubProcessorBase but does not expose the detailed Stream content signals. */
class PipelineExampleModule extends SimpleProcessorBase(new NumberGroup, new Stats) {
  out := in.processWith(new NonNegativeFilter).processWith(new Reducer())
}

object PipelineExampleModule extends App {
  println("PipelineExample")

  test(new TopLevelModule()) { c =>
    println("Tydi-lang code of PipelineExample")
    println(c.tydiCode)
  }

  println("FIRRTL & Verilog of NonNegativeFilter")
  println(emitCHIRRTL(new NonNegativeFilter()))
  println(emitSystemVerilog(new NonNegativeFilter(), firtoolOpts = firNormalOpts))

  println("FIRRTL & Verilog of Reducer")
  println(emitCHIRRTL(new Reducer()))
  println(emitSystemVerilog(new Reducer(), firtoolOpts = firNormalOpts))

  println("FIRRTL & Verilog of PipelineExampleModule")
  println(emitCHIRRTL(new PipelineExampleModule()))
  println(emitSystemVerilog(new PipelineExampleModule(), firtoolOpts = firNormalOpts))

  println("Done")
}
