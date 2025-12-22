package org.sunbird.cassandraannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field or parameter as part of the Cassandra clustering key.
 * 
 * <p>Clustering keys determine the sort order of data within a partition.
 * This annotation is used at runtime to identify clustering key components
 * when mapping Java objects to Cassandra tables.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ClusteringKey {}

