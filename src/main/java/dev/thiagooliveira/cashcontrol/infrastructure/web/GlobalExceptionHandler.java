package dev.thiagooliveira.cashcontrol.infrastructure.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoHandlerFoundException.class)
    public RedirectView handleNoHandlerFoundException() {
        return new RedirectView("/protected");
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public RedirectView handleNoResourceFoundException() {
        return new RedirectView("/protected");
    }
}
