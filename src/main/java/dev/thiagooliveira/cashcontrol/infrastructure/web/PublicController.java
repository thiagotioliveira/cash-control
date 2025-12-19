package dev.thiagooliveira.cashcontrol.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/public")
public class PublicController {

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public void ok() {}
}
