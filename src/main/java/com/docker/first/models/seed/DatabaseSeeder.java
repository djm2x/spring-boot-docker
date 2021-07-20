package com.docker.first.models.seed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.github.javafaker.Faker;
import com.docker.first.models.*;
import com.docker.first.repositories.UowService;

@Component
public class DatabaseSeeder /* implements ApplicationRunner */ {

    // private Logger logger = Logger.getLogger(DatabaseSeeder.class);
    @Autowired
    private UowService uow;

    // @PersistenceContext
	// EntityManager entityManager;

    private final Faker faker = new Faker(new Locale("fr"));

    // public void run(ApplicationArguments args) {
    // AddUsers();
    // }

    @EventListener
    public void seed(ApplicationReadyEvent event) {
        if (uow.users.count() > 0) {
            return;
        }

        AddUsers();
    }

    public DatabaseSeeder AddUsers() {
        List<User> list = Arrays.asList(
            new User(1L, "dj", "m2x", "dj-m2x@hotmail.com","123", true,"admin")
        );
        uow.users.saveAll(list);
        return this;
    }
}
