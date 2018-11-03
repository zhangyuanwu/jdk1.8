package java.lang.annotation;

public enum ElementType {
	TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE,
	/**
	 * 输入参数声明
	 *
	 * @since 1.8
	 */
	TYPE_PARAMETER,
	/**
	 * 使用一种类型
	 *
	 * @since 1.8
	 */
	TYPE_USE
}
