import tydi_lib._
import chisel3._

object MyTypes {
    /** Bit(64) type, defined in pack0 */
    def generated_0_7_AudkORtF_29 = UInt(64.W)
    assert(this.generated_0_7_AudkORtF_29.getWidth == 64)

    /** Bit(64) type, defined in pack0 */
    def generated_0_7_CTh3cRpJ_27 = UInt(64.W)
    assert(this.generated_0_7_CTh3cRpJ_27.getWidth == 64)
}


/** Stream, defined in pack0. */
class Generated_114_139_TSuzlpzQ_3 extends PhysicalStreamDetailed(e=new NumberGroup, n=1, d=1, c=1, r=false, u=Null())

object Generated_114_139_TSuzlpzQ_3 {
    def apply(): Generated_114_139_TSuzlpzQ_3 = Wire(new Generated_114_139_TSuzlpzQ_3())
}

/** Stream, defined in pack0. */
class Generated_562_581_8ln94DFm_11 extends PhysicalStreamDetailed(e=new Stats, n=1, d=1, c=1, r=false, u=Null())

object Generated_562_581_8ln94DFm_11 {
    def apply(): Generated_562_581_8ln94DFm_11 = Wire(new Generated_562_581_8ln94DFm_11())
}

/** Stream, defined in pack0. */
class Generated_347_366_mtBGmgUU_7 extends PhysicalStreamDetailed(e=new Stats, n=1, d=1, c=1, r=false, u=Null())

object Generated_347_366_mtBGmgUU_7 {
    def apply(): Generated_347_366_mtBGmgUU_7 = Wire(new Generated_347_366_mtBGmgUU_7())
}

/** Stream, defined in pack0. */
class Generated_392_417_1PCyUfwW_9 extends PhysicalStreamDetailed(e=new NumberGroup, n=1, d=1, c=1, r=false, u=Null())

object Generated_392_417_1PCyUfwW_9 {
    def apply(): Generated_392_417_1PCyUfwW_9 = Wire(new Generated_392_417_1PCyUfwW_9())
}

/** Stream, defined in pack0. */
class Generated_607_632_HlxXfYFN_13 extends PhysicalStreamDetailed(e=new NumberGroup, n=1, d=1, c=1, r=false, u=Null())

object Generated_607_632_HlxXfYFN_13 {
    def apply(): Generated_607_632_HlxXfYFN_13 = Wire(new Generated_607_632_HlxXfYFN_13())
}

/** Stream, defined in pack0. */
class Generated_165_190_BDoT0FmX_5 extends PhysicalStreamDetailed(e=new NumberGroup, n=1, d=1, c=1, r=false, u=Null())

object Generated_165_190_BDoT0FmX_5 {
    def apply(): Generated_165_190_BDoT0FmX_5 = Wire(new Generated_165_190_BDoT0FmX_5())
}

/** Group element, defined in pack0. */
class NumberGroup extends Group {
    val time = MyTypes.generated_0_7_CTh3cRpJ_27
    val value = MyTypes.generated_0_7_AudkORtF_29
}

/** Group element, defined in pack0. */
class Stats extends Group {
    val average = MyTypes.generated_0_7_CTh3cRpJ_27
    val max = MyTypes.generated_0_7_CTh3cRpJ_27
    val min = MyTypes.generated_0_7_CTh3cRpJ_27
    val sum = MyTypes.generated_0_7_CTh3cRpJ_27
}

/** Stream, defined in pack0. */
class Generated_0_30_73CrM0DN_30 extends PhysicalStreamDetailed(e=new Stats, n=1, d=1, c=1, r=false, u=Null())

/** Stream, defined in pack0. */
class Generated_0_36_JI5PTYzg_24 extends PhysicalStreamDetailed(e=new NumberGroup, n=1, d=1, c=1, r=false, u=Null())

/**
 * Streamlet, defined in pack1.
 */
class NonNegativeFilter_interface extends TydiModule {
    /** Stream of [[in]] with input direction. */
    val inStream = Generated_165_190_BDoT0FmX_5().flip
    /** IO of [[inStream]] with input direction. */
    val in = inStream.toPhysical
    /** Stream of [[out]] with output direction. */
    val outStream = Generated_114_139_TSuzlpzQ_3()
    /** IO of [[outStream]] with output direction. */
    val out = outStream.toPhysical
}

/**
 * Streamlet, defined in pack1.
 */
class PipelineExample_interface extends TydiModule {
    /** Stream of [[in]] with input direction. */
    val inStream = Generated_607_632_HlxXfYFN_13().flip
    /** IO of [[inStream]] with input direction. */
    val in = inStream.toPhysical
    /** Stream of [[out]] with output direction. */
    val outStream = Generated_562_581_8ln94DFm_11()
    /** IO of [[outStream]] with output direction. */
    val out = outStream.toPhysical
}

/**
 * Streamlet, defined in pack1.
 */
class Reducer_interface extends TydiModule {
    /** Stream of [[in]] with input direction. */
    val inStream = Generated_392_417_1PCyUfwW_9().flip
    /** IO of [[inStream]] with input direction. */
    val in = inStream.toPhysical
    /** Stream of [[out]] with output direction. */
    val outStream = Generated_347_366_mtBGmgUU_7()
    /** IO of [[outStream]] with output direction. */
    val out = outStream.toPhysical
}

/**
 * Implementation, defined in pack1.
 */
class NonNegativeFilter extends NonNegativeFilter_interface {}

/**
 * Implementation, defined in pack1.
 */
class PipelineExample extends PipelineExample_interface {
    // Modules
    val filter = Module(new NonNegativeFilter)
    val reducer = Module(new Reducer)

    // Connections
    reducer.in := filter.out
    out := reducer.out
    filter.in := in
}

/**
 * Implementation, defined in pack1.
 */
class Reducer extends Reducer_interface {}
