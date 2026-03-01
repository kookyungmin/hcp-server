package net.happykoo.hcp.adapter.in.web.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority(T(net.happykoo.hcp.adapter.in.web.auth.PermissionCode).INSTANCE_WRITE)")
public @interface ServerInstanceWritePermission {

}
