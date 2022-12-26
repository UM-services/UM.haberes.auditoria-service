/**
 * 
 */
package ar.edu.um.haberes.aud.rest.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class Log extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 688201238410752537L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long logId;

	private Long legajoId;

	private String ipAddress;
	
	@Column(name = "_when")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime when;

	private String what;

}
