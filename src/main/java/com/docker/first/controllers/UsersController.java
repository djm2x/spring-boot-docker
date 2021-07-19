package com.docker.first.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.docker.first.dao.UserDto;
import com.docker.first.models.*;
import com.docker.first.repositories.UowService;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

// @RolesAllowed({ "admin", "user" })
@RestController
@RequestMapping("api/users")
public class UsersController extends SuperController<User, Long> {

    public UowService uow;

    public UsersController(UowService uow) {
        super(uow.users);
        this.uow = uow;
    }

    @GetMapping("/getAll/{startIndex}/{pageSize}/{sortBy}/{sortDir}/{email}")
    // @Override
    public ResponseEntity<?> GetAll(@PathVariable int startIndex, @PathVariable int pageSize,
            @PathVariable String sortBy, @PathVariable String sortDir, @PathVariable String email) {

        Page<User> query = repository
            .findAll((r, q, cb) -> email.equals("*") ? cb.and() : cb.like(r.get("email"), "%"+email+"%"), PageRequest.of(startIndex, pageSize))
            ;

        List<User> list = query.getContent();

        Long count = query.getTotalElements();

        return ResponseEntity.ok(Map.of("count", count, "list", list));
    }

    @GetMapping("/test")
    // @Override
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of("msg", "me & you"));
    }
}