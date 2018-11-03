package java.lang.management;

import javax.management.openmbean.CompositeData;
import sun.management.LockInfoCompositeData;

public class LockInfo {
	private String className;
	private int identityHashCode;

	public LockInfo(String className, int identityHashCode) {
		if (className == null) {
			throw new NullPointerException("Parameter className cannot be null");
		}
		this.className = className;
		this.identityHashCode = identityHashCode;
	}

	LockInfo(Object lock) {
		className = lock.getClass().getName();
		identityHashCode = System.identityHashCode(lock);
	}

	public String getClassName() {
		return className;
	}

	public int getIdentityHashCode() {
		return identityHashCode;
	}

	/**
	 * 返回由给定{@code CompositeData}表示的{@code LockInfo}对象.
	 * 给定的{@code CompositeData}必须包含以下属性: <blockquote> <table border summary="The
	 * attributes and the types the given CompositeData contains">
	 * <tr>
	 * <th align=left>属性名称</th>
	 * <th align=left>类型</th>
	 * </tr>
	 * <tr>
	 * <td>className</td>
	 * <td><tt>java.lang.String</tt></td>
	 * </tr>
	 * <tr>
	 * <td>identityHashCode</td>
	 * <td><tt>java.lang.Integer</tt></td>
	 * </tr>
	 * </table>
	 * </blockquote>
	 *
	 * @param cd
	 *            代表{@code LockInfo}的{@code CompositeData}
	 * @throws IllegalArgumentException
	 *             如果{@code cd}不代表具有上述属性的{@code LockInfo}.
	 * @return 如果{@code cd}不是{@code null},则由{@code cd}表示的{@code LockInfo}对象;
	 *         {@code null}否则.
	 * @since 1.8
	 */
	public static LockInfo from(CompositeData cd) {
		if (cd == null) {
			return null;
		}
		if (cd instanceof LockInfoCompositeData) {
			return ((LockInfoCompositeData) cd).getLockInfo();
		} else {
			return LockInfoCompositeData.toLockInfo(cd);
		}
	}

	@Override
	public String toString() {
		return className + '@' + Integer.toHexString(identityHashCode);
	}
}
