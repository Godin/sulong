/*
 * Copyright (c) 2017, Oracle and/or its affiliates.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.truffle.llvm.nodes.intrinsics.llvm.arith;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.llvm.nodes.intrinsics.llvm.LLVMBuiltin;
import com.oracle.truffle.llvm.runtime.LLVMAddress;
import com.oracle.truffle.llvm.runtime.memory.LLVMMemory;
import com.oracle.truffle.llvm.runtime.nodes.api.LLVMExpressionNode;

@NodeChildren({@NodeChild(value = "left", type = LLVMExpressionNode.class), @NodeChild(value = "right", type = LLVMExpressionNode.class),
                @NodeChild(value = "target", type = LLVMExpressionNode.class)})
public abstract class LLVMArithmeticWithOverflowI16 extends LLVMBuiltin {

    static void writeResult(LLVMAddress addr, short result, boolean overflow) {
        LLVMMemory.putI32(addr, result);
        LLVMMemory.putI32(addr.getVal() + 4, overflow ? 1 : 0);
    }

    public abstract static class LLVMSAddWithOverflowI16 extends LLVMArithmeticWithOverflowI16 {

        @Specialization
        public LLVMAddress executeDouble(short left, short right, LLVMAddress addr) {
            short result = (short) (left + right);
            boolean overflow = (((left ^ result) & (right ^ result)) << 16) < 0;
            writeResult(addr, result, overflow);
            return addr;
        }
    }

    public abstract static class LLVMSSubWithOverflowI16 extends LLVMArithmeticWithOverflowI16 {

        @Specialization
        public LLVMAddress executeDouble(short left, short right, LLVMAddress addr) {
            short result = (short) (left - right);
            boolean overflow = (((left ^ right) & (left ^ result)) << 16) < 0;
            writeResult(addr, result, overflow);
            return addr;
        }
    }

    public abstract static class LLVMSMulWithOverflowI16 extends LLVMArithmeticWithOverflowI16 {

        @Specialization
        public LLVMAddress executeDouble(short left, short right, LLVMAddress addr) {
            int result = left * right;
            boolean overflow = (short) result != result;
            writeResult(addr, (short) result, overflow);
            return addr;
        }
    }

    public abstract static class LLVMUAddWithOverflowI16 extends LLVMArithmeticWithOverflowI16 {

        @Specialization
        public LLVMAddress executeDouble(short left, short right, LLVMAddress addr) {
            short result = (short) (left + right);
            boolean overflow = ((left & right | left & ~result | right & ~result) << 16) < 0;
            writeResult(addr, result, overflow);
            return addr;
        }
    }

    public abstract static class LLVMUSubWithOverflowI16 extends LLVMArithmeticWithOverflowI16 {

        @Specialization
        public LLVMAddress executeDouble(short left, short right, LLVMAddress addr) {
            short result = (short) (left - right);
            boolean overflow = ((~left & right | ~left & result | right & result) << 16) < 0;
            writeResult(addr, result, overflow);
            return addr;
        }
    }

    public abstract static class LLVMUMulWithOverflowI16 extends LLVMArithmeticWithOverflowI16 {

        @Specialization
        public LLVMAddress executeDouble(short left, short right, LLVMAddress addr) {
            int result = Short.toUnsignedInt(left) * Short.toUnsignedInt(right);
            boolean overflow = Integer.compareUnsigned(0xffff, result) < 0;
            writeResult(addr, (short) result, overflow);
            return addr;
        }
    }

}
