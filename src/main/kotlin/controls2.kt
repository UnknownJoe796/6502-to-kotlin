//package com.ivieleague.decompiler6502tokotlin.hand
//
//sealed interface SuperBlock {
//    class JustABlock(
//        val block: AssemblyBlock
//    ): SuperBlock
//
//    class IfElse(
//        val leading: SuperBlock,
//        val thenNode: SuperBlock,
//        val elseNode: SuperBlock? = null,
//    ): SuperBlock {
//        val startLabel: String = TODO()
//        val endLabel: String = TODO()
//    }
//
//    class Loop(
//        val body: SuperBlock,
//    ): SuperBlock {
//        val startLabel: String = TODO()
//        val endLabel: String? = TODO()
//    }
//
////    class ConditionalBreak(
////        val content: List<ControlFlowNode>,
////    ): ControlFlowNode
////
////    class ConditionalContinue(
////        val content: List<ControlFlowNode>,
////    ): ControlFlowNode
//}
//
//fun List<AssemblyBlock>.controlFlow(): SuperBlock {
//    this.first().dom
//}