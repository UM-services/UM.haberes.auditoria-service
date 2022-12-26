/**
 * 
 */
package ar.edu.um.haberes.aud.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.haberes.aud.rest.model.Log;
import ar.edu.um.haberes.aud.rest.service.LogService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/log")
public class LogController {
	@Autowired
	private LogService service;
	
	@PostMapping("/")
	public ResponseEntity<Log> add(@RequestBody Log log) {
		return new ResponseEntity<Log>(service.add(log), HttpStatus.OK);
	}
}
