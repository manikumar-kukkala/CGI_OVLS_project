package com.cgi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgi.model.Documents;
import com.cgi.repository.DocumentsRepository;

@RestController
	@RequestMapping("/documents")
	public class DocumentsController {

	    @Autowired
	    private DocumentsRepository documentsRepository;

	    @PostMapping
	    public Documents uploadDocument(@RequestBody Documents document) {
	        return documentsRepository.save(document);
	    }

	    @GetMapping("/{id}")
	    public Documents getDocument(@PathVariable int id) {
	        return documentsRepository.findById((long) id)
	                .orElseThrow(() -> new RuntimeException("Document not found"));
	    }
	}


