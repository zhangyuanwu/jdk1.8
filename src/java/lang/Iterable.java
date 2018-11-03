package java.lang;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public interface Iterable<T> {
	Iterator<T> iterator();

	/**
	 * 对{@code Iterable}的每个元素执行给定操作,直到处理完所有元素或操作引发异常.
	 * 除非实现类另有指定,否则操作按迭代顺序执行（如果指定了迭代顺序）. 操作抛出的异常将转发给调用者.
	 * <p>
	 * 默认实现的行为就像是:
	 * 
	 * <pre>
	 * {@code
	 *     for (T t : this)
	 *         action.accept(t);
	 * }
	 * </pre>
	 *
	 * @param action
	 *            要为每个元素执行的操作
	 * @throws NullPointerException
	 *             如果指定的操作为null
	 * @since 1.8
	 */
	default void forEach(Consumer<? super T> action) {
		Objects.requireNonNull(action);
		for (T t : this) {
			action.accept(t);
		}
	}

	/**
	 * 在{@code Iterable}描述的元素上创建{@link Spliterator}.
	 * 默认实现从iterable的{@code Iterator}创建<em><a href=
	 * "Spliterator.html#binding">早期绑定</a></em>分裂器.
	 * spliterator继承了iterable迭代器的<em>fail-fast </em>属性.
	 *
	 * 通常应该覆盖默认实现. 默认实现返回的分裂器具有较差的分割能力,未分级,并且不报告任何分裂器特征. 实现类几乎总能提供更好的实现.
	 *
	 * @return 关于此{@code Iterable}描述的元素的{@code Spliterator}.
	 * @since 1.8
	 */
	default Spliterator<T> spliterator() {
		return Spliterators.spliteratorUnknownSize(iterator(), 0);
	}
}
