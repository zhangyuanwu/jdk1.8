package java.lang.reflect;

import sun.reflect.CallerSensitive;
import sun.reflect.ConstructorAccessor;
import sun.reflect.Reflection;
import sun.reflect.annotation.TypeAnnotation;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.repository.ConstructorRepository;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.scope.ConstructorScope;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;

public final class Constructor<T> extends Executable {
	private Class<T> clazz;
	private int slot;
	private Class<?>[] parameterTypes;
	private Class<?>[] exceptionTypes;
	private int modifiers;
	private transient String signature;
	private transient ConstructorRepository genericInfo;
	private byte[] annotations;
	private byte[] parameterAnnotations;

	private GenericsFactory getFactory() {
		return CoreReflectionFactory.make(this, ConstructorScope.make(this));
	}

	@Override
	ConstructorRepository getGenericInfo() {
		if (genericInfo == null) {
			genericInfo = ConstructorRepository.make(getSignature(), getFactory());
		}
		return genericInfo;
	}

	private volatile ConstructorAccessor constructorAccessor;
	private Constructor<T> root;

	@Override
	Executable getRoot() {
		return root;
	}

	Constructor(Class<T> declaringClass, Class<?>[] parameterTypes, Class<?>[] checkedExceptions, int modifiers, int slot, String signature, byte[] annotations, byte[] parameterAnnotations) {
		this.clazz = declaringClass;
		this.parameterTypes = parameterTypes;
		this.exceptionTypes = checkedExceptions;
		this.modifiers = modifiers;
		this.slot = slot;
		this.signature = signature;
		this.annotations = annotations;
		this.parameterAnnotations = parameterAnnotations;
	}

	Constructor<T> copy() {
		if (this.root != null) {
			throw new IllegalArgumentException("Can not copy a non-root Constructor");
		}
		Constructor<T> res = new Constructor<>(clazz, parameterTypes, exceptionTypes, modifiers, slot, signature, annotations, parameterAnnotations);
		res.root = this;
		res.constructorAccessor = constructorAccessor;
		return res;
	}

	@Override
	boolean hasGenericInformation() {
		return (getSignature() != null);
	}

	@Override
	byte[] getAnnotationBytes() {
		return annotations;
	}

	@Override
	public Class<T> getDeclaringClass() {
		return clazz;
	}

	@Override
	public String getName() {
		return getDeclaringClass().getName();
	}

	@Override
	public int getModifiers() {
		return modifiers;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public TypeVariable<Constructor<T>>[] getTypeParameters() {
		if (getSignature() != null) {
			return (TypeVariable<Constructor<T>>[]) getGenericInfo().getTypeParameters();
		} else {
			return new TypeVariable[0];
		}
	}

	@Override
	public Class<?>[] getParameterTypes() {
		return parameterTypes.clone();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.8
	 */
	@Override
	public int getParameterCount() {
		return parameterTypes.length;
	}

	@Override
	public Type[] getGenericParameterTypes() {
		return super.getGenericParameterTypes();
	}

	@Override
	public Class<?>[] getExceptionTypes() {
		return exceptionTypes.clone();
	}

	@Override
	public Type[] getGenericExceptionTypes() {
		return super.getGenericExceptionTypes();
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj != null) && (obj instanceof Constructor)) {
			Constructor<?> other = (Constructor<?>) obj;
			if (getDeclaringClass() == other.getDeclaringClass()) {
				return equalParamTypes(parameterTypes, other.parameterTypes);
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getDeclaringClass().getName().hashCode();
	}

	@Override
	public String toString() {
		return sharedToString(Modifier.constructorModifiers(), false, parameterTypes, exceptionTypes);
	}

	@Override
	void specificToStringHeader(StringBuilder sb) {
		sb.append(getDeclaringClass().getTypeName());
	}

	@Override
	public String toGenericString() {
		return sharedToGenericString(Modifier.constructorModifiers(), false);
	}

	@Override
	void specificToGenericStringHeader(StringBuilder sb) {
		specificToStringHeader(sb);
	}

	@CallerSensitive
	public T newInstance(Object... initargs) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (!override) {
			if (!Reflection.quickCheckMemberAccess(clazz, modifiers)) {
				Class<?> caller = Reflection.getCallerClass();
				checkAccess(caller, clazz, null, modifiers);
			}
		}
		if ((clazz.getModifiers() & Modifier.ENUM) != 0) {
			throw new IllegalArgumentException("Cannot reflectively create enum objects");
		}
		ConstructorAccessor ca = constructorAccessor;
		if (ca == null) {
			ca = acquireConstructorAccessor();
		}
		@SuppressWarnings("unchecked")
		T inst = (T) ca.newInstance(initargs);
		return inst;
	}

	@Override
	public boolean isVarArgs() {
		return super.isVarArgs();
	}

	@Override
	public boolean isSynthetic() {
		return super.isSynthetic();
	}

	private ConstructorAccessor acquireConstructorAccessor() {
		ConstructorAccessor tmp = null;
		if (root != null) {
			tmp = root.getConstructorAccessor();
		}
		if (tmp != null) {
			constructorAccessor = tmp;
		} else {
			tmp = reflectionFactory.newConstructorAccessor(this);
			setConstructorAccessor(tmp);
		}
		return tmp;
	}

	ConstructorAccessor getConstructorAccessor() {
		return constructorAccessor;
	}

	void setConstructorAccessor(ConstructorAccessor accessor) {
		constructorAccessor = accessor;
		if (root != null) {
			root.setConstructorAccessor(accessor);
		}
	}

	int getSlot() {
		return slot;
	}

	String getSignature() {
		return signature;
	}

	byte[] getRawAnnotations() {
		return annotations;
	}

	byte[] getRawParameterAnnotations() {
		return parameterAnnotations;
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return super.getAnnotation(annotationClass);
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return super.getDeclaredAnnotations();
	}

	@Override
	public Annotation[][] getParameterAnnotations() {
		return sharedGetParameterAnnotations(parameterTypes, parameterAnnotations);
	}

	@Override
	void handleParameterNumberMismatch(int resultLength, int numParameters) {
		Class<?> declaringClass = getDeclaringClass();
		if (declaringClass.isEnum() || declaringClass.isAnonymousClass() || declaringClass.isLocalClass()) {
			return;
		} else {
			if (!declaringClass.isMemberClass() || (declaringClass.isMemberClass() && ((declaringClass.getModifiers() & Modifier.STATIC) == 0) && ((resultLength + 1) != numParameters))) {
				throw new AnnotationFormatError("Parameter annotations don't match number of parameters");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.8
	 */
	@Override
	public AnnotatedType getAnnotatedReturnType() {
		return getAnnotatedReturnType0(getDeclaringClass());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.8
	 */
	@Override
	public AnnotatedType getAnnotatedReceiverType() {
		if (getDeclaringClass().getEnclosingClass() == null) {
			return super.getAnnotatedReceiverType();
		}
		return TypeAnnotationParser.buildAnnotatedType(getTypeAnnotationBytes0(), sun.misc.SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), this, getDeclaringClass(), getDeclaringClass().getEnclosingClass(), TypeAnnotation.TypeAnnotationTarget.METHOD_RECEIVER);
	}
}
