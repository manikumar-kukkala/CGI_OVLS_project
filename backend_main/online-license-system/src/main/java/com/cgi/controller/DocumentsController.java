package com.cgi.controller;

import com.cgi.model.Documents;
import com.cgi.repository.DocumentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST,
		RequestMethod.PUT, RequestMethod.OPTIONS })
@RestController
@RequestMapping("/documents") // final paths: /documents, /documents/{id}, /documents/update/{id}
public class DocumentsController {

	@Autowired
	private DocumentsRepository documentsRepository;

	// Create
	@PostMapping
	public ResponseEntity<Documents> create(@RequestBody Documents body) {
		Documents d = new Documents();
		d.setAddressProof(body.getAddressProof());
		d.setIdProof(body.getIdProof());
		d.setPhoto(body.getPhoto());
		Documents saved = documentsRepository.save(d);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	// Read
	@GetMapping("/{id}")
	public Documents get(@PathVariable Long id) {
		return documentsRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
	}

	// Update (support BOTH /{id} and /update/{id} so your current frontend keeps
	// working)
	@PutMapping({ "/{id}", "/update/{id}" })
	public Documents update(@PathVariable Long id, @RequestBody Documents body) {
		Documents d = documentsRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));

		if (body.getIdProof() != null)
			d.setIdProof(body.getIdProof());
		if (body.getPhoto() != null)
			d.setPhoto(body.getPhoto());
		if (body.getAddressProof() != null)
			d.setAddressProof(body.getAddressProof());

		return documentsRepository.save(d);
	}
}
