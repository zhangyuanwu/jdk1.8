package com.sun.source.util;

import java.io.IOException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;

@jdk.Exported
public abstract class JavacTask implements CompilationTask {
	/**
	 * Get the {@code JavacTask} for a {@code ProcessingEnvironment}. If the
	 * compiler is being invoked using a
	 * {@link javax.tools.JavaCompiler.CompilationTask CompilationTask}, then
	 * that task will be returned.
	 *
	 * @param processingEnvironment
	 *            the processing environment
	 * @return the {@code JavacTask} for a {@code ProcessingEnvironment}
	 * @since 1.8
	 */
	public static JavacTask instance(ProcessingEnvironment processingEnvironment) {
		if (!processingEnvironment.getClass().getName().equals("com.sun.tools.javac.processing.JavacProcessingEnvironment")) {
			throw new IllegalArgumentException();
		}
		Context c = ((JavacProcessingEnvironment) processingEnvironment).getContext();
		JavacTask t = c.get(JavacTask.class);
		return (t != null) ? t : new BasicJavacTask(c, true);
	}

	public abstract Iterable<? extends CompilationUnitTree> parse() throws IOException;

	public abstract Iterable<? extends Element> analyze() throws IOException;

	public abstract Iterable<? extends JavaFileObject> generate() throws IOException;

	public abstract void setTaskListener(TaskListener taskListener);

	/**
	 * The specified listener will receive notification of events describing the
	 * progress of this compilation task.
	 *
	 * This method may be called at any time before or during the compilation.
	 *
	 * @throws IllegalStateException
	 *             if the specified listener has already been added.
	 * @since 1.8
	 */
	public abstract void addTaskListener(TaskListener taskListener);

	/**
	 * The specified listener will no longer receive notification of events
	 * describing the progress of this compilation task.
	 *
	 * This method may be called at any time before or during the compilation.
	 *
	 * @since 1.8
	 */
	public abstract void removeTaskListener(TaskListener taskListener);

	/**
	 * Get a type mirror of the tree node determined by the specified path. This
	 * method has been superceded by methods on {@link com.sun.source.util.Trees
	 * Trees}.
	 *
	 * @see com.sun.source.util.Trees#getTypeMirror
	 */
	public abstract TypeMirror getTypeMirror(Iterable<? extends Tree> path);

	public abstract Elements getElements();

	public abstract Types getTypes();
}
