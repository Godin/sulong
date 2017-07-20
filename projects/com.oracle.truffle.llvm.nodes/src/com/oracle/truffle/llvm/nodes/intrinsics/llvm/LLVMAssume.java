package com.oracle.truffle.llvm.nodes.intrinsics.llvm;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.llvm.runtime.nodes.api.LLVMExpressionNode;

@NodeChild(type = LLVMExpressionNode.class, value = "cond")
public abstract class LLVMAssume extends LLVMBuiltin {
  @Specialization
  public Object executeVoid(@SuppressWarnings("unused") boolean cond) {
    return null;
  }
}
