package com.docker.first.controllers;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.docker.first.repositories.GenericRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public class SuperController<T extends Serializable, ID> {

    protected final GenericRepository<T, ID> repository;

    public SuperController(GenericRepository<T, ID> repository) {
        this.repository = repository ;
    }

    @GetMapping("/getAll/{startIndex}/{pageSize}/{sortBy}/{sortDir}")
    public ResponseEntity<?> GetAll(@PathVariable int startIndex, @PathVariable int pageSize, @PathVariable String sortBy, @PathVariable String sortDir) {

        Sort sort = Sort.by(sortDir == "desc" ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);

        Page<T> query = repository.findAll(PageRequest.of(startIndex, pageSize, sort));

        List<T> list = query.getContent();

        Long count = query.getTotalElements();

        return ResponseEntity.ok(Map.of("count", count, "list", list));
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(){
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable ID id){

        Optional<T> model = repository.findById(id);

        if(model.isPresent() == false){
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(model.get());
    }

    @PutMapping("/put/{id}")
    public ResponseEntity<?> put(@PathVariable ID id, @RequestBody T model){

        Optional<T> optional = repository.findById(id);

        if(optional.isPresent() == false){
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        
        T o = repository.save(model);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/post")
    public ResponseEntity<?> post(@RequestBody T model){
        try {
            T o = repository.saveAndFlush(model);
            return ResponseEntity.ok(o);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/patch/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patch(@PathVariable ID id, @RequestBody JsonPatch patch) {

        Optional<T> optional = repository.findById(id);

        if (optional.isPresent() == false) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            T model = optional.get();

            JsonNode patched = patch.apply(objectMapper.convertValue(model, JsonNode.class));

            objectMapper.treeToValue(patched, model.getClass());

            repository.save(model);

        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable ID id){
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            return ResponseEntity.ok(Boolean.FALSE);
        }
        
        return ResponseEntity.ok(Boolean.TRUE);
    }
}
