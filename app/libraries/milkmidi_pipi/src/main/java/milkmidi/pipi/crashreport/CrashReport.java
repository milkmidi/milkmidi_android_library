package milkmidi.pipi.crashreport;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CrashReport {
	String hostURL();
	String localFordName();
//	boolean debug();
	String reportExtension() default ".txt";
//	REPORTER_EXTENSION
}
