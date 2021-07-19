// package com.docker.first.controllers;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.web.bind.annotation.*;

// import java.util.*;
// import com.docker.first.models.*;
// import com.docker.first.dao.UserDto;

// import com.crs.authentication.configuration.JwtTokenUtil;
// import com.crs.authentication.dao.TokenDto;
// import com.crs.authentication.dao.UserDto;
// import com.crs.authentication.models.*;
// import com.crs.authentication.repositories.UowService;
// import com.crs.authentication.services.MailService;

// @RestController
// @RequestMapping("api/auth")
// public class AuthController extends SuperController<User, Long> {

//     public UowService uow;
//     protected final JwtTokenUtil jwtTokenUtil;

//     protected final MailService mailService;

//     @Value("${com.crs.authentication.pwd_token_expiration}")
//     private int pwd_token_expiration;

//     @Autowired
//     private BCryptPasswordEncoder bCryptPasswordEncoder;

//     public AuthController(UowService uow, JwtTokenUtil jwtTokenUtil, MailService mailService) {
//         super(uow.users);
//         this.jwtTokenUtil = jwtTokenUtil;
//         this.mailService= mailService;
//         this.uow = uow;
//     }
 
//     @PostMapping("/register")
//     public ResponseEntity<?> register(@RequestBody User model) {
//         Optional<User> userExist = uow.users.findByEmail(model.getEmail());

//         if (userExist.isPresent() == true) {
//             return ResponseEntity.ok(Map.of("code", -1, "message", "Error"));
//         }
//         model.setActive(Boolean.FALSE);
//         model.setPassword(bCryptPasswordEncoder.encode(model.getPassword()));
//         User user = uow.users.save(model);

//         Map<String, Object> claims = new HashMap<>();
//         claims.put("email", user.getEmail());
//         claims.put("creation",  new Date());

//         //Password change token
//         String token= jwtTokenUtil.doGenerateToken(claims,user.getEmail());
//         mailService.register(user,token);


//         return ResponseEntity.ok(Map.of("code", 1, "message", "Successful"));

//     }

//     @PostMapping("/checkToken")
//     public ResponseEntity<?> checkToken(@RequestBody TokenDto tokenDto) {
//         if(tokenDto.value == null || tokenDto.value.trim().isEmpty() || !jwtTokenUtil.validateToken(tokenDto.value))
//             return ResponseEntity.ok(Map.of("code", -1, "message", "Token not valid"));

//         return ResponseEntity.ok(Map.of("code", 1, "role", jwtTokenUtil.getByClaim(tokenDto.value,"role"),"email", jwtTokenUtil.getByClaim(tokenDto.value,"email")));
//     }

//     @PostMapping("/login")
//     public ResponseEntity<?> login(@RequestBody UserDto model) {
//         Optional<User> op = uow.users.findByEmail(model.email);

//         if (op.isPresent() == false) {
//             return ResponseEntity.ok(Map.of("code", -1, "message", "Error"));
//         }

//         User user = op.get();

//         // if (!bCryptPasswordEncoder.matches(model.password,user.getPassword()) || !user.isActive())
//         if (!user.getPassword().equals(model.password) || !user.isActive())
//         {
//             return ResponseEntity.ok(Map.of("code", -2, "message", "Error"));
//         }

//         Map<String, Object> claims = new HashMap<>();
//         claims.put("email", user.getEmail());
//         claims.put("role", user.getRole());
//         claims.put("id", user.getId());

//         String token = jwtTokenUtil.doGenerateToken(claims, user.getEmail());

//         return ResponseEntity.ok(Map.of("token", token, "user", user, "message", "Successful"));
//     }

//     @PostMapping("/changePassword/{token}")
//     public ResponseEntity<?> changePassword(@RequestBody UserDto model,@PathVariable("token") String token) {

//         if(token == null || token.trim().isEmpty() ||
//             !jwtTokenUtil.getByClaim(token,"email").toString().equals(model.email)
//         )
//             return ResponseEntity.ok(Map.of("code", -1, "message", "Token not valid"));

//         //Token creation date
//         Calendar cToken = Calendar.getInstance();
//         cToken.setTimeInMillis((Long)jwtTokenUtil.getByClaim(token,"creation"));
//         cToken.add(Calendar.DATE,pwd_token_expiration);

//         //New Date
//         Date now = new Date();
//         Calendar cNow = Calendar.getInstance();
//         cNow.setTime(now);

//         if(cToken.before(cNow))
//             return ResponseEntity.ok(Map.of("code", -1, "message", "Token not valid"));

//         Optional<User> op = uow.users.findByEmail(model.email);

//         if (op.isPresent() == false)
//             return ResponseEntity.ok(Map.of("code", -1, "message", "Error"));

//         User user = op.get();
//         user.setPassword(bCryptPasswordEncoder.encode(model.password));
//         user.setActive(Boolean.TRUE);

//         repository.save(user);

//         return ResponseEntity.ok(Map.of("code", 1, "message", "Successful"));
//     }

//     @PostMapping("/forgetPassword/{email}")
//     public ResponseEntity<?> forgetPassword(@PathVariable("email") String email) {

//         if (email == null || email.trim().isEmpty() || !email.contains("@") || !email.contains(".")) {
//             return ResponseEntity.ok(Map.of("code", -1, "message", "Format email not satisfied"));
//         }

//         Optional<User> op = uow.users.findByEmail(email);
//         User user = op.get();
//         if (user == null) {
//             return ResponseEntity.ok(Map.of("code", -1, "message", "Error"));
//         }


//         Map<String, Object> claims = new HashMap<>();
//         claims.put("email", user.getEmail());
//         claims.put("creation",  new Date());

//         String token= jwtTokenUtil.doGenerateToken(claims,user.getEmail());

//         mailService.forgetPassword(user,token);

//         return ResponseEntity.ok(Map.of("code", 1, "message", "Success"));
//     }

//     @PostMapping("/disableCredentials/{email}")
//     public ResponseEntity<?> disableCredentials(@PathVariable("email") String email) {

//         if (email == null || email.trim().isEmpty() || !email.contains("@") || !email.contains(".")) {
//             return ResponseEntity.ok(Map.of("code", -1, "message", "Format email not satisfied"));
//         }

//         Optional<User> op = uow.users.findByEmail(email);
//         User user = op.get();
//         if (user == null) {
//             return ResponseEntity.ok(Map.of("code", -1, "message", "Error"));
//         }

//         user.setActive(false);
//         repository.save(user);

//         return ResponseEntity.ok(Map.of("code", 1, "message", "Success"));
//     }


// }