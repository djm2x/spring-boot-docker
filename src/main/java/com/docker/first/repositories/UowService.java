package com.docker.first.repositories;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UowService {
	@Autowired public UsersRepository users;
}
