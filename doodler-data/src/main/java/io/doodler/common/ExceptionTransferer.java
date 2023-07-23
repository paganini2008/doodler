package io.doodler.common;

/**
 * @Description: ExceptionTransferer
 * @Author: Fred Feng
 * @Date: 18/01/2023
 * @Version 1.0.0
 */
@FunctionalInterface
public interface ExceptionTransferer {

	/**
	 * Transfer original exception to business exception that developer designed ahead
	 * 
	 * @param e
	 * @return
	 */
	Throwable transfer(Throwable e);
}