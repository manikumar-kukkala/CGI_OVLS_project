package com.cgi.controller;

import com.cgi.model.Documents;
import com.cgi.repository.DocumentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/documents") // <-- class-level prefix
public class DocumentsController {

	@Autowired
	private DocumentsRepository documentsRepository;

	@PostMapping
	public Documents create(@RequestBody Documents body) {
		Documents d = new Documents();
		d.setAddressProof(body.getAddressProof());
		d.setIdProof(body.getIdProof());
		d.setPhoto(body.getPhoto());
		return documentsRepository.save(d);
	}

	@GetMapping("/{id}")
	public Documents get(@PathVariable Long id) {
		return documentsRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
	}

	// IMPORTANT: do NOT add "documents/" here — final path becomes
	// /documents/update/{id}
	@PutMapping("/update/{id}")
	public ResponseEntity<Documents> update(@PathVariable Long id, @RequestBody Documents body) {
		Documents d = documentsRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
		if (body.getIdProof() != null)
			d.setIdProof(body.getIdProof());
		if (body.getPhoto() != null)
			d.setPhoto(body.getPhoto());
		if (body.getAddressProof() != null)
			d.setAddressProof(body.getAddressProof());
		return ResponseEntity.ok(documentsRepository.save(d));
	}
}
