package l1j.server;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import l1j.server.CustomLoaders.DefaultLoader;



class Annotations {
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ java.lang.annotation.ElementType.FIELD })
	static @interface Configure {
		public abstract String file();

		public abstract String key();

		public abstract boolean allowDefaultValue() default true;

		public abstract Class<? extends CustomLoaders.CustomLoader> loader() default DefaultLoader.class;

		public abstract boolean isOptional() default false;
	}
}