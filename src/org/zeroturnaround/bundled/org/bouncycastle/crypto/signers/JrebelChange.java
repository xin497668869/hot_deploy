package org.zeroturnaround.bundled.org.bouncycastle.crypto.signers;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;
import static jdk.internal.org.objectweb.asm.Opcodes.ICONST_1;
import static jdk.internal.org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

public class JrebelChange {

    public static void main(String[] args) throws Exception {
        loadFirst();
    }

    public static void loadFirst() throws Exception {
        try (URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{ClassLoader.getSystemClassLoader().getResource("jrebel6/jrebel.jar")})) {

            Class<?> aClass = urlClassLoader.loadClass("com.zeroturnaround.javarebel.dxe");

            ClassReader classReader = new ClassReader(getClassByte(urlClassLoader, aClass.getName()));
            ClassWriter classWriter = new ClassWriter(classReader, COMPUTE_MAXS);
            classReader.accept(new MyGeneralClassAdapter(classWriter), ClassReader.EXPAND_FRAMES);
            File parent = new File("G:\\workspaces\\idea\\维护\\hot_deploy\\hot_deploy\\resources\\jrebel6");

            FileUtils.copyInputStreamToFile(new ByteArrayInputStream(classWriter.toByteArray()), new File(parent.getAbsolutePath() + File.separator + "dxe.class"));
        }
        /*
        protected final Class<?> defineClass(String name, byte[] b, int off, int len)
        throws ClassFormatError
    {
        return defineClass(name, b, off, len, null);
    }
         */
//        Method defineClass = ClassLoader.getSystemClassLoader().getClass().getMethod("defineClass", byte[].class, int.class, int.class);
//        defineClass.setAccessible(true);
//        byte[] bytes = classWriter.toByteArray();
//        defineClass.invoke(ClassLoader.getSystemClassLoader(), bytes, 0, bytes.length);
//
    }

    static class MyGeneralClassAdapter extends ClassVisitor {

        public MyGeneralClassAdapter(ClassVisitor cv) {
            super(ASM5);
            this.cv = cv;
        }

        public void setCp(ClassVisitor cv) {
            this.cv = cv;
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return super.visitField(access, name, desc, signature, value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                                         String signature, String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

            // 当是sayName方法是做对应的修改
            if (name.equals("a") && desc.contains("([B)Z")) {
                MethodVisitor newMv = new ChangeMethodAdapter(mv);
                return newMv;
            } else {
                return mv;
            }
        }

        // 定义一个自己的方法访问类
        class ChangeMethodAdapter extends MethodVisitor {
            public boolean arraylength = false;
            public boolean aaload      = false;

            public ChangeMethodAdapter(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitParameter(String name, int access) {
            }

            @Override
            public AnnotationVisitor visitAnnotationDefault() {
                return super.visitAnnotationDefault();
            }

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return super.visitAnnotation(desc, visible);
            }

            @Override
            public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
                return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
            }

            @Override
            public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
                return super.visitParameterAnnotation(parameter, desc, visible);
            }

            @Override
            public void visitAttribute(Attribute attr) {
            }

            @Override
            public void visitCode() {
                super.visitCode();
                super.visitInsn(ICONST_1);
                super.visitInsn(IRETURN);
                super.visitEnd();
            }

//    @Override
//    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
//        System.out.println(startSign + "  " + "visitFrame(" + paint(type) + "," + paint(nLocal) + "," + paint(local) + "," + paint(nStack) + "," + paint(stack) + ")");
//        startSign++;
//        super.visitFrame(type, nLocal, local, nStack, stack);
//    }

            @Override
            public void visitInsn(int opcode) {
            }

            @Override
            public void visitIntInsn(int opcode, int operand) {
            }

            @Override
            public void visitVarInsn(int opcode, int var) {
            }

            @Override
            public void visitTypeInsn(int opcode, String type) {
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            }

            @Override
            public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
            }

            @Override
            public void visitJumpInsn(int opcode, Label label) {
            }

//    @Override
//    public void visitLabel(Label label) {
//        System.out.println(startSign + "  "+"visitLabel(" + paint(label) + ")");
//        startSign++;
//        super.visitLabel(label);
//    }

            @Override
            public void visitLdcInsn(Object cst) {
            }

            @Override
            public void visitIincInsn(int var, int increment) {
            }

            @Override
            public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
            }

            @Override
            public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
            }

            @Override
            public void visitMultiANewArrayInsn(String desc, int dims) {
            }

            @Override
            public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
                return super.visitInsnAnnotation(typeRef, typePath, desc, visible);
            }

            @Override
            public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            }

            @Override
            public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
                return super.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
            }

//    @Override
//    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
//        System.out.println(startSign + "  "+"visitLocalVariable(" + paint(name) + "," + paint(desc) + "," + paint(signature) + "," + paint(start) + "," + paint(end) + "," + paint(index) + ")");
//        startSign++;
//        super.visitLocalVariable(name, desc, signature, start, end, index);
//    }

            @Override
            public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
                return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
            }

//    @Override
//    public void visitLineNumber(int line, Label start) {
//        System.out.println(startSign + "  "+"visitLineNumber(" + paint(line) + "," + paint(start) + ")");
//        startSign++;
//        super.visitLineNumber(line, start);
//    }

            //    @Override
//    public void visitMaxs(int maxStack, int maxLocals) {
//        System.out.println(startSign + "  "+"visitMaxs(" + paint(maxStack) + "," + paint(maxLocals) + ")");
//        startSign++;
//        super.visitMaxs(maxStack, maxLocals);
//    }
            private String paint(Object obj) {
                if (obj == null) {
                    return "null";
                } else if (obj.getClass().equals(String.class)) {
                    if (obj.toString().startsWith("FLAG::")) {
                        return obj.toString().substring("FLAG::".length());
                    } else {
                        return "\"" + obj + "\"";
                    }
                } else {
                    return obj + "";
                }
            }

            @Override
            public void visitEnd() {
            }
        }
    }


    public static byte[] getClassByte(ClassLoader classLoader, String name) throws ClassNotFoundException {
        try {
            InputStream resourceAsStream = classLoader.getResourceAsStream(name.replace('.', '/')
                                                                                   + ".class");
            if (resourceAsStream == null) {
                throw new ClassNotFoundException(name + " 类不存在无法进行注入");
            }
            return readClass(
                    resourceAsStream, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] readClass(final InputStream is, boolean close)
            throws IOException {
        if (is == null) {
            throw new IOException("Class not found");
        }
        try {
            byte[] b = new byte[is.available()];
            int len = 0;
            while (true) {
                int n = is.read(b, len, b.length - len);
                if (n == -1) {
                    if (len < b.length) {
                        byte[] c = new byte[len];
                        System.arraycopy(b, 0, c, 0, len);
                        b = c;
                    }
                    return b;
                }
                len += n;
                if (len == b.length) {
                    int last = is.read();
                    if (last < 0) {
                        return b;
                    }
                    byte[] c = new byte[b.length + 1000];
                    System.arraycopy(b, 0, c, 0, len);
                    c[len++] = (byte) last;
                    b = c;
                }
            }
        } finally {
            if (close) {
                is.close();
            }
        }
    }
}
