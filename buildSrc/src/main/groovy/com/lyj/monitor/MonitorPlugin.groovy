package com.lyj.monitor

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES


public class MonitorPlugin extends Transform implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(this)
    }

    @Override
    String getName() {
        return "Monitor"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        def startTime = System.currentTimeMillis()
        MonitorLog.d("start transform time = " + startTime.toString())
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                if (directoryInput.file.isDirectory()) {
                    directoryInput.file.eachFileRecurse { File file ->
                        def name = file.name
                        if (name.endsWith(".class") && !name.startsWith("R\$") &&
                                !"R.class".equals(name) && !"BuildConfig.class".equals(name)) {
                            MonitorLog.d(name + " is changing...")
                            ClassReader cr = new ClassReader(file.bytes)
                            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                            ClassVisitor cv = new MonitorClassVisitor(cw)

                            cr.accept(cv, ClassReader.EXPAND_FRAMES)

                            byte[] code = cw.toByteArray()
                            FileOutputStream fos = new FileOutputStream(file.parentFile.absolutePath + File.separator + name)
                            fos.write(code)
                            fos.close()
                        }
                    }
                }
                def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
            input.jarInputs.each { JarInput jarInput ->
                /**
                 * 重名名输出文件,因为可能同名,会覆盖
                 */
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }

                File tmpFile = null
                if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
                    JarFile jarFile = new JarFile(jarInput.file)
                    Enumeration enumeration = jarFile.entries()
                    tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_trace.jar")
                    //避免上次的缓存被重复插入
                    if (tmpFile.exists()) {
                        tmpFile.delete()
                    }
                    JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
                    //用于保存
                    ArrayList<String> processorList = new ArrayList<>()
                    while (enumeration.hasMoreElements()) {
                        JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                        String entryName = jarEntry.getName()
                        ZipEntry zipEntry = new ZipEntry(entryName)
                        //println "MeetyouCost entryName :" + entryName
                        InputStream inputStream = jarFile.getInputStream(jarEntry)
                        //如果是inject文件就跳过

                        //插桩class
                        if (entryName.endsWith(".class") && !entryName.contains("R\$") &&
                                !entryName.contains("R.class") && !entryName.contains("BuildConfig.class")) {
                            //class文件处理
                            jarOutputStream.putNextEntry(zipEntry)
                            ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                            def className = entryName.split(".class")[0]
                            ClassVisitor cv = new MonitorClassVisitor(classWriter)
                            classReader.accept(cv, EXPAND_FRAMES)
                            byte[] code = classWriter.toByteArray()
                            jarOutputStream.write(code)

                        } else if (entryName.contains("META-INF/services/javax.annotation.processing.Processor")) {
                            if (!processorList.contains(entryName)) {
                                processorList.add(entryName)
                                jarOutputStream.putNextEntry(zipEntry)
                                jarOutputStream.write(IOUtils.toByteArray(inputStream))
                            } else {
                                println "duplicate entry:" + entryName
                            }
                        } else {

                            jarOutputStream.putNextEntry(zipEntry)
                            jarOutputStream.write(IOUtils.toByteArray(inputStream))
                        }

                        jarOutputStream.closeEntry()
                    }
                    //写入inject注解

                    //结束
                    jarOutputStream.close()
                    jarFile.close()
                }

                //处理jar进行字节码注入处理 TODO

                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                if (tmpFile == null) {
                    FileUtils.copyFile(jarInput.file, dest)
                } else {
                    FileUtils.copyFile(tmpFile, dest)
                    tmpFile.delete()
                }
            }
        }
        MonitorLog.d("end transform time = " + (System.currentTimeMillis() - startTime).toString())
    }
}
