#Getting started
Ximplementation-spring has a core Spring `BeanPostProcessor` class `org.ximplementation.spring.ImplementeeBeanCreationPostProcessor`, it is used for creating <i>ximplementation</i> Spring beans.

Simply add the following content

`<bean class="org.ximplementation.spring.ImplementeeBeanCreationPostProcessor"/>`

to `applicationContext.xml`, then your Spring project will be able to support multiple dependency injection and more ximplementation features.

## Example
You can write Spring components like this:

	@Component
	public class Controller
	{
		@Autowired
		private Service service;
		
		public String handle(Number number)
		{
			return this.service.handle(number);
		}
	}
	
	public interface Service
	{
		String handle(Number number);
	}
	
	@Component
	public class ServiceImplDefault implements Service
	{
		public String handle(Number number){...}
	}
	
	@Component
	public class ServiceImplAnother implements Service
	{
		@Validity("isValid")
		public String handle(Number number){...}
	
		public boolean isValid(Number number){ return number.intValue() > 0; }
	}
	
	@Component
	@Implementor(Service.class)
	public class ServiceImplInteger
	{
		@Implement
		public String handle(Integer number){...}
	}

The `Controller.service` will be injected successfully though there are two instances, and its `handle` method invocation will be delegated to `ServiceImplInteger` when the parameter type is `Integer`, to `ServiceImplAnother` when the parameter is greater than `0`, to `ServiceImplDefault` otherwise.