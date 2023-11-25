package top.niunaijun.blackobfuscator.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.objectweb.asm.Opcodes.ASM9;

public class Operator {

    public static void run(File input_jar, File output_jar, boolean delete_input) {
        try {
            ZipFileHelper zipFileHelper = new ZipFileHelper(input_jar, output_jar);
            List<String> zip_entry_names = zipFileHelper.new_zip_entry_names;
            for (String entry_name : zip_entry_names) {
                if (entry_name.endsWith(".class")) {
                    InputStream entryInputStream = zipFileHelper.getEntryInputStream(entry_name, true);
                    byte[] ret = start(entryInputStream);
                    zipFileHelper.add_entry(entry_name, ret);
                }
            }
            zipFileHelper.commit();
            if (delete_input) {
                input_jar.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void run(File file) {
        if (file.isDirectory()) {
            FileFilter fileFilter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory() || pathname.getName().endsWith(".class");
                }
            };
            File[] files = file.listFiles(fileFilter);
            if (files != null) {
                for (File listFile : files) {
                    run(listFile);
                }
            }
        } else {
            start(file);
        }
    }

    private static void start(File classFile) {
        try {
            FileInputStream fileInputStream = new FileInputStream(classFile);
            byte[] ret = start(fileInputStream);
            FileOutputStream fileOutputStream = new FileOutputStream(classFile);
            fileOutputStream.write(ret);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] start(InputStream inputStream) throws IOException {
        byte[] bytes = ZipFileHelper.inputStreamToByteArray(inputStream);
        return start(bytes);
    }

    private static byte[] start(byte[] classByteCode) {
        byte[] back_up = new byte[classByteCode.length];
        System.arraycopy(classByteCode, 0, back_up, 0, back_up.length);
        ClassReader classReader = new ClassReader(classByteCode);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassVisitor classVisitor = new EncodeClassVisitor(ASM9, classWriter, EncodePlanBMethodVisitor.class);
        classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);
        try {
            return classWriter.toByteArray();
        } catch (Exception exception) {
            exception.printStackTrace();
            return back_up;
        }
    }
}