package util;

import org.sunbird.keys.JsonKey;
import play.libs.typedmap.TypedKey;

/**
 * Provides typed attributes for HTTP request context.
 *
 * <p>This class defines constants for typed keys used to store and retrieve request-scoped
 * attributes such as user ID, context information, and authentication details.
 */
public class Attrs {

    /** Typed key for user identifier. */
    public static final TypedKey<String> USER_ID = TypedKey.<String>create(JsonKey.USER_ID);

    /** Typed key for request context. */
    public static final TypedKey<String> CONTEXT = TypedKey.<String>create(JsonKey.CONTEXT);

    /** Typed key for managed-for attribute. */
    public static final TypedKey<String> MANAGED_FOR = TypedKey.<String>create(JsonKey.MANAGED_FOR);

    /** Typed key for request start time. */
    public static final TypedKey<String> START_TIME = TypedKey.<String>create(JsonKey.START_TIME);

    /** Typed key for master key authentication flag. */
    public static final TypedKey<String> AUTH_WITH_MASTER_KEY =
            TypedKey.<String>create(JsonKey.AUTH_WITH_MASTER_KEY);

    /** Typed key for authentication requirement flag. */
    public static final TypedKey<String> IS_AUTH_REQ = TypedKey.<String>create(JsonKey.IS_AUTH_REQ);

    /** Typed key for X-Request-ID header. */
    public static final TypedKey<String> X_REQUEST_ID = TypedKey.<String>create(JsonKey.X_REQUEST_ID);

    private Attrs() {
        // Utility class, no instantiation
    }
}
