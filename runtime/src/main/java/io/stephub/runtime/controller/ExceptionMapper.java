package io.stephub.runtime.controller;

import io.stephub.runtime.service.ExecutionException;
import io.stephub.runtime.service.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class ExceptionMapper extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleMissingResource(
            final ResourceNotFoundException ex, final HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(ExecutionException.class)
    public void handleExecutionError(
            final ExecutionException ex, final HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.CONFLICT.value(), ex.getMessage());
    }
}