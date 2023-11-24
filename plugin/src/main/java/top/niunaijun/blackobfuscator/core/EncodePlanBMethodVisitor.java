package top.niunaijun.blackobfuscator.core;

import org.objectweb.asm.MethodVisitor;

import java.util.Base64;

import static org.objectweb.asm.Opcodes.BASTORE;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.NEWARRAY;
import static org.objectweb.asm.Opcodes.T_BYTE;

public class EncodePlanBMethodVisitor extends EncodePlanAMethodVisitor {

    public EncodePlanBMethodVisitor(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        if (cst instanceof String) {
            String target = (String) cst;
            if (target.length() == 0) {
                mv.visitLdcInsn(cst);
            } else {
                mv.visitTypeInsn(NEW, "java/lang/String");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESTATIC, "java/util/Base64", "getDecoder", "()Ljava/util/Base64$Decoder;", false);
                byte[] encode = Base64.getEncoder().encode(target.getBytes());
                mv.visitLdcInsn(encode.length);
                mv.visitIntInsn(NEWARRAY, T_BYTE);
                mv.visitInsn(DUP);
                for (int index = 0; index < encode.length; index++) {
                    mv.visitLdcInsn(index);
                    mv.visitIntInsn(BIPUSH, encode[index]);
                    mv.visitInsn(BASTORE);
                    if (index < encode.length - 1) {
                        mv.visitInsn(DUP);
                    }
                }
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Base64$Decoder", "decode", "([B)[B", false);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V", false);
            }
        } else {
            super.visitLdcInsn(cst);
        }
    }
}