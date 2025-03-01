package top.niunaijun.blackobfuscator.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Constructor;
import java.util.Random;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_ANNOTATION;
import static org.objectweb.asm.Opcodes.ACC_ENUM;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM9;

public class EncodeClassVisitor extends ClassVisitor {
    private String owner;
    private boolean on;
    private String data_name;
    private String index_name;
    private final Random random = new Random();
    private final Class<? extends EncodeMethodVisitor> encodeMethodVisitorClass;

    public EncodeClassVisitor(int api, ClassVisitor classVisitor, Class<? extends EncodeMethodVisitor> encodeMethodVisitorClass) {
        super(api, classVisitor);
        this.encodeMethodVisitorClass = encodeMethodVisitorClass;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        on = (access & ACC_ABSTRACT) == 0 && (access & ACC_INTERFACE) == 0 && (access & ACC_ENUM) == 0 && (access & ACC_ANNOTATION) == 0;
        System.out.println("encoding:" + name);

        this.owner = name;
        if (encodeMethodVisitorClass == EncodePlanAMethodVisitor.class && on) {
            data_name = "d_" + CommonUtil.getRandomString();
            index_name = "i_" + CommonUtil.getRandomString();
            FieldVisitor fv = cv.visitField(ACC_PUBLIC + ACC_STATIC, data_name, "[B", null, null);
            fv.visitEnd();
            fv = cv.visitField(ACC_PUBLIC + ACC_STATIC, index_name, "I", null, null);
            fv.visitEnd();
        }
    }

    @Override
    public void visitSource(String source, String debug) {
        super.visitSource("null", "null");
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        try {
            Constructor<? extends EncodeMethodVisitor> constructor = encodeMethodVisitorClass.getConstructor(int.class, MethodVisitor.class);
            EncodeMethodVisitor encodeMethodVisitor = constructor.newInstance(ASM9, methodVisitor);
            encodeMethodVisitor.setOn(on);
            encodeMethodVisitor.setData_name(data_name);
            encodeMethodVisitor.setIndex_name(index_name);
            encodeMethodVisitor.setOwner(owner);
            encodeMethodVisitor.setRandom(random);
            return encodeMethodVisitor;
        } catch (Exception e) {
            e.printStackTrace();
            return methodVisitor;
        }
    }
}