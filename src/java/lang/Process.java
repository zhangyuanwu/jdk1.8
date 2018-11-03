package java.lang;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public abstract class Process {
	public abstract OutputStream getOutputStream();

	public abstract InputStream getInputStream();

	public abstract InputStream getErrorStream();

	public abstract int waitFor() throws InterruptedException;

	/**
	 * 如果需要,导致当前线程等待,直到此{@code Process}对象表示的子进程终止,或者指定的等待时间过去.
	 * <p>
	 * 如果子进程已经终止,则此方法立即返回值{@code true}.
	 * 如果进程尚未终止且超时值小于或等于零,则此方法立即返回值{@code false}.
	 * <p>
	 * 此方法的默认实现轮询{@code exitValue}以检查进程是否已终止. 强烈建议使用此类的具体实现来使用更高效的实现来覆盖此方法.
	 *
	 * @param timeout
	 *            最长等待时间
	 * @param unit
	 *            {@code timeout}参数的时间单位
	 * @return {@code true}如果子进程已退出,则{@code false}如果在子进程退出之前等待时间已过去.
	 * @throws InterruptedException
	 *             如果当前线程在等待时被中断
	 * @throws NullPointerException
	 *             如果unit为null
	 * @since 1.8
	 */
	public boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
		long startTime = System.nanoTime();
		long rem = unit.toNanos(timeout);

		do {
			try {
				exitValue();
				return true;
			} catch (IllegalThreadStateException ex) {
				if (rem > 0) {
					Thread.sleep(Math.min(TimeUnit.NANOSECONDS.toMillis(rem) + 1, 100));
				}
			}
			rem = unit.toNanos(timeout) - (System.nanoTime() - startTime);
		} while (rem > 0);
		return false;
	}

	public abstract int exitValue();

	public abstract void destroy();

	/**
	 * 杀死子进程. 由此{@code Process}对象表示的子进程被强制终止.
	 * <p>
	 * 此方法的默认实现调用{@link #destroy},因此可能不会强制终止该进程. 强烈建议使用此类的具体实现来使用兼容的实现来覆盖此方法.
	 * 在{@link ProcessBuilder #start}和{@link Runtime #exec}返回的{@code Process}对象上调用此方法将强制终止该进程.
	 * <p>
	 * 注意：子进程可能不会立即终止.
	 * 即{@code isAlive()}可能会在调用{@code destroyForcibly()}后的短暂时间内返回true.
	 * 如果需要,可以将此方法链接到{@code waitFor()}.
	 *
	 * @return 表示要强制销毁的子进程的{@code Process}对象.
	 * @since 1.8
	 */
	public Process destroyForcibly() {
		destroy();
		return this;
	}

	/**
	 * 测试此{@code Process}表示的子进程是否处于活动状态.
	 *
	 * @return {@code true}如果此{@code Process}对象表示的子进程尚未终止.
	 * @since 1.8
	 */
	public boolean isAlive() {
		try {
			exitValue();
			return false;
		} catch (IllegalThreadStateException e) {
			return true;
		}
	}
}
