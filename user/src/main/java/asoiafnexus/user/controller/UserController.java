package asoiafnexus.user.controller;

import asoiafnexus.user.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

  @GetMapping
  public User getProfile() {
    return new User("Faust", 1500.00, Collections.emptyList(), 0, 0, 0);
  }
}
