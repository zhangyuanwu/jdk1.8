package java.lang.annotation;

/**
 * 注释类型{@code java.lang.annotation.Repeatable}用于指示其声明(meta-)注释的注释类型是<em>可重复的</em>.
 * {@code @Repeatable}的值表示可重复注释类型的<em>包含注释类型</em>.
 *
 * @since 1.8 9.6 Annotation Types 9.7 Annotations
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Repeatable {
	/**
	 * 表示可重复注释类型的<em>包含注释类型</em>.
	 *
	 * @return 包含注释类型
	 */
	Class<? extends Annotation> value();
}
