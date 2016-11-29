/**
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *  
  * 	http://www.apache.org/licenses/LICENSE-2.0
  *  
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License. 
  */

package org.ximplementation.spring;

import java.lang.reflect.Field;

import org.springframework.aop.framework.AdvisedSupport;

/**
 * Utility for Spring proxy.
 * 
 * @author earthangry@gmail.com
 * @date 2016-11-28
 *
 */
public class ProxyUtil
{
	/**
	 * The full class name of
	 * {@linkplain org.springframework.aop.framework.JdkDynamicAopProxy}.
	 */
	public static final String JdkDynamicAopProxyName = "org.springframework.aop.framework.JdkDynamicAopProxy";

	/**
	 * The full class name of
	 * {@linkplain org.springframework.aop.framework.Cglib2AopProxy}.
	 */
	public static final String Cglib2AopProxyName = "org.springframework.aop.framework.Cglib2AopProxy";

	/**
	 * The {@linkplain org.springframework.aop.framework.AdvisedSupport advised}
	 * field name in
	 * {@linkplain org.springframework.aop.framework.JdkDynamicAopProxy} .
	 */
	public static final String JdkDynamicAopProxyAdvisedSupportFieldName = "advised";

	/**
	 * The {@linkplain org.springframework.aop.framework.AdvisedSupport advised}
	 * field name in
	 * {@linkplain org.springframework.aop.framework.Cglib2AopProxy}.
	 */
	public static final String Cglib2AopProxyAdvisedSupportFieldName = "advised";

	private static volatile Field springJdkProxyAdvisedField = null;

	private static volatile Field cglib2AopProxyNameAdvisedField = null;

	/**
	 * Peel Spring JDK proxy object and returns the raw bean object.
	 * <p>
	 * The given proxy object should be created by
	 * {@linkplain {@linkplain org.springframework.aop.framework.JdkDynamicAopProxy}
	 * }.
	 * </p>
	 * 
	 * @param jdkProxy
	 * @return The no proxy raw bean object.
	 * @throws ProxyPeelingException
	 */
	public static Object peelSpringJdkProxy(java.lang.reflect.Proxy jdkProxy)
			throws ProxyPeelingException
	{
		java.lang.reflect.InvocationHandler invocationHandler = java.lang.reflect.Proxy
				.getInvocationHandler(jdkProxy);

		Class<?> invocationHandlerClass = invocationHandler.getClass();

		if (!JdkDynamicAopProxyName.equals(invocationHandlerClass.getName()))
			throw new ProxyPeelingException(
					"Peeling is not supported, Proxy [" + jdkProxy
							+ "] is not created by [" + JdkDynamicAopProxyName
							+ "]");

		try
		{
			if (springJdkProxyAdvisedField == null)
			{
				springJdkProxyAdvisedField = invocationHandlerClass
						.getDeclaredField(
								JdkDynamicAopProxyAdvisedSupportFieldName);

				if (!springJdkProxyAdvisedField.isAccessible())
					springJdkProxyAdvisedField.setAccessible(true);
			}

			AdvisedSupport advisedSupport = (AdvisedSupport) springJdkProxyAdvisedField
					.get(invocationHandler);

			return advisedSupport.getTargetSource().getTarget();
		}
		catch (Exception e)
		{
			throw new ProxyPeelingException(e);
		}
	}

	/**
	 * Peel Spring CGLIB proxy object and returns the raw bean object.
	 * <p>
	 * The given proxy object should be created by
	 * {@linkplain org.springframework.aop.framework.Cglib2AopProxy}.
	 * </p>
	 * 
	 * @param cglibProxy
	 * @return The not proxied raw bean object
	 * @throws ProxyPeelingException
	 */
	public static Object peelSpringCglibProxy(
			net.sf.cglib.proxy.Proxy cglibProxy)
			throws ProxyPeelingException
	{
		net.sf.cglib.proxy.InvocationHandler invocationHandler = net.sf.cglib.proxy.Proxy
				.getInvocationHandler(cglibProxy);

		Class<?> invocationHandlerClass = invocationHandler.getClass();

		if (!Cglib2AopProxyName.equals(invocationHandlerClass.getName()))
			throw new ProxyPeelingException(
					"Peeling is not supported, Proxy [" + cglibProxy
							+ "] is not created by [" + Cglib2AopProxyName
							+ "]");

		try
		{
			if (cglib2AopProxyNameAdvisedField == null)
			{
				cglib2AopProxyNameAdvisedField = invocationHandlerClass
						.getDeclaredField(
								Cglib2AopProxyAdvisedSupportFieldName);

				if (!cglib2AopProxyNameAdvisedField.isAccessible())
					cglib2AopProxyNameAdvisedField.setAccessible(true);
			}

			AdvisedSupport advisedSupport = (AdvisedSupport) cglib2AopProxyNameAdvisedField
					.get(invocationHandler);

			return advisedSupport.getTargetSource().getTarget();
		}
		catch (Exception e)
		{
			throw new ProxyPeelingException(e);
		}
	}
}