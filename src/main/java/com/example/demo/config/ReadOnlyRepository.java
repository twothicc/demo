package com.example.demo.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The ANNOTATION filter type includes/excludes classes in the component scans with the given annotation.
 * For instance, if excludeFilters is specified to be ReadOnlyRepository, ComponentScan will exclude classes
 * marked with @ReadOnlyRepository
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ReadOnlyRepository {
}
