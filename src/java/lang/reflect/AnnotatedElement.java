package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.AnnotationType;

public interface AnnotatedElement {
	default boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		return getAnnotation(annotationClass) != null;
	}

	<T extends Annotation> T getAnnotation(Class<T> annotationClass);

	Annotation[] getAnnotations();

	/**
	 * 返回与此元素<em>关联</em>的注释. 如果此元素没有与<em>关联</em>的注释,则返回值为长度为0的数组.
	 * 此方法与{@link #getAnnotation(Class)}之间的区别在于此方法检测其参数是否为<em>可重复注释类型</em>(JLS
	 * 9.6),如果是,则尝试查找一个或多个 通过"查看"容器注释来注释该类型. 此方法的调用者可以自由修改返回的数组;
	 * 它对返回给其他调用者的数组没有影响.
	 *
	 * @implSpec 默认实现首先调用{@link #getDeclaredAnnotationsByType(Class)}作为参数传递{@code annotationClass}.
	 *           如果返回的数组的长度大于零,则返回该数组.
	 *           如果返回的数组为零长度并且此{@code AnnotatedElement}是一个类且参数类型是可继承的注释类型,并且此{@code AnnotatedElement}的超类是非null,则返回的结果是结果
	 *           在{@code annotationClass}作为参数的超类上调用{@link #getAnnotationsByType(Class)}.
	 *           否则,返回零长度数组.
	 * @param <T>
	 *            要查询的注释的类型,如果存在则返回
	 * @param annotationClass
	 *            与注释类型对应的Class对象
	 * @return 如果与此元素关联,则指定注释类型的所有此元素的注释,否则为长度为零的数组
	 * @throws NullPointerException
	 *             如果给定的注释类为null
	 * @since 1.8
	 */
	default <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
		T[] result = getDeclaredAnnotationsByType(annotationClass);
		if ((result.length == 0) && (this instanceof Class) && AnnotationType.getInstance(annotationClass).isInherited()) {
			Class<?> superClass = ((Class<?>) this).getSuperclass();
			if (superClass != null) {
				result = superClass.getAnnotationsByType(annotationClass);
			}
		}
		return result;
	}

	/**
	 * 如果此类注释<em>直接存在</em>,则返回此元素对指定类型的注释,否则返回null. 此方法忽略继承的注释.
	 * (如果此元素上没有直接存在注释,则返回null.)
	 *
	 * @implSpec 默认实现首先执行空检查,然后循环遍历{@link #getDeclaredAnnotations}的结果,返回其注释类型与参数类型匹配的第一个注释.
	 * @param <T>
	 *            要查询的注释的类型,如果直接存在则返回
	 * @param annotationClass
	 *            与注释类型对应的Class对象
	 * @return 如果直接出现在此元素上,则此元素的指定注释类型的注释,否则为null
	 * @throws NullPointerException
	 *             如果给定的注释类为null
	 * @since 1.8
	 */
	default <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
		Objects.requireNonNull(annotationClass);
		for (Annotation annotation : getDeclaredAnnotations()) {
			if (annotationClass.equals(annotation.annotationType())) {
				return annotationClass.cast(annotation);
			}
		}
		return null;
	}

	/**
	 * 如果此类注释是<em>直接存在</em>或<em>间接存在</em>,则返回此元素对指定类型的注释. 此方法忽略继承的注释.
	 * 如果此元素上没有直接或间接存在的指定注释,则返回值为长度为0的数组.
	 * 此方法与{@link #getDeclaredAnnotation(Class)}之间的区别在于此方法检测其参数是否为<em>可重复注释类型</em>(JLS
	 * 9.6),如果是,则尝试查找一个或多个 通过“查看”容器注释(如果存在)注释该类型的注释. 此方法的调用者可以自由修改返回的数组;
	 * 它对返回给其他调用者的数组没有影响.
	 *
	 * @implSpec 默认实现可以调用{@link #getDeclaredAnnotation(Class)}一次或多次以查找直接存在的注释,并且如果注释类型是可重复的,则查找容器注释.
	 *           如果发现注释类型{@code annotationClass}的注释直接和间接存在,则将调用{@link #getDeclaredAnnotations()}以确定返回数组中元素的顺序.
	 *           <p>
	 *           或者,默认实现可以一次调用{@link #getDeclaredAnnotations()}并且检查返回的数组以直接和间接地呈现注释.
	 *           调用{@link #getDeclaredAnnotations()}的结果假定与调用{@link #getDeclaredAnnotation(Class)}的结果一致.
	 * @param <T>
	 *            要查询的注释的类型,如果直接或间接存在则返回
	 * @param annotationClass
	 *            与注释类型对应的Class对象
	 * @return 如果直接或间接出现在此元素上,则指定注释类型的所有此元素的注释,否则为长度为零的数组
	 * @throws NullPointerException
	 *             如果给定的注释类为null
	 * @since 1.8
	 */
	default <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
		Objects.requireNonNull(annotationClass);
		return AnnotationSupport.getDirectlyAndIndirectlyPresent(Arrays.stream(getDeclaredAnnotations()).collect(Collectors.toMap(Annotation::annotationType, Function.identity(), ((first, second) -> first), LinkedHashMap::new)), annotationClass);
	}

	Annotation[] getDeclaredAnnotations();
}
