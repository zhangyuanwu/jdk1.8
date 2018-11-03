package java.lang.annotation;

/**
 * 指示可以从本机代码引用定义常量值的字段. 注释可以用作生成本机头文件的工具的提示,以确定是否需要头文件,如果需要,它应该包含哪些声明.
 *
 * @since 1.8
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Native {
}
