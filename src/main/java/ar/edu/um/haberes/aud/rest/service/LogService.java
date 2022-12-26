/**
 * 
 */
package ar.edu.um.haberes.aud.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.haberes.aud.rest.model.Log;
import ar.edu.um.haberes.aud.rest.repository.ILogRepository;

/**
 * @author daniel
 *
 */
@Service
public class LogService {
	@Autowired
	private ILogRepository repository;

	public Log add(Log log) {
		repository.save(log);
		return log;
	}
}
