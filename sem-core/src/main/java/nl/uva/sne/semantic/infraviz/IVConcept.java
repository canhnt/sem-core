package nl.uva.sne.semantic.infraviz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface IVConcept {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface getter {}
}
