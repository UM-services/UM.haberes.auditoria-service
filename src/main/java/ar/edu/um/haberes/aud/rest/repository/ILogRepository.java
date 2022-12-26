/**
 * 
 */
package ar.edu.um.haberes.aud.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.haberes.aud.rest.model.Log;

/**
 * @author daniel
 *
 */
@Repository
public interface ILogRepository extends JpaRepository<Log, Long> {

}
