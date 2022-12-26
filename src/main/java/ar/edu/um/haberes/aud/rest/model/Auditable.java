/**
 * 
 */
package ar.edu.um.haberes.aud.rest.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author daniel
 *
 */
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable {
	@CreatedDate
	@Column(name = "created", nullable = false, updatable = false)
	private LocalDateTime created;
	
	@LastModifiedDate
	@Column(name = "updated")
	private LocalDateTime updated;
}
