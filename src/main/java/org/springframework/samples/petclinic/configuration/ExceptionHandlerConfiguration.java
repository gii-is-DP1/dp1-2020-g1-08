package org.springframework.samples.petclinic.configuration;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.service.exceptions.BadRequestException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * This advice is necessary because MockMvc is not a real servlet environment,
 * therefore it does not redirect error responses to [ErrorController], which
 * produces validation response. So we need to fake it in tests. It's not ideal,
 * but at least we can use classic MockMvc tests for testing error response +
 * document it.
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandlerConfiguration {
    // add any exceptions/validations/binding problems

    @ExceptionHandler(Exception.class)
    public ResponseEntity defaultErrorHandler(HttpServletRequest request, Exception ex) {
        ResponseEntity resp = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error: " + ex.getMessage());
        return resp;
    }

    @ExceptionHandler({ BadRequestException.class, MissingServletRequestParameterException.class })
    public ResponseEntity badRequestErrorHandler(HttpServletRequest request, Exception ex) {
        ResponseEntity resp = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad request: " + ex.getMessage());
        return resp;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public void requestHandlingNoHandlerFound(HttpServletResponse r) throws IOException {
        log.info("ASDFAWERGAERG");
        r.sendRedirect("/");

    }
}
