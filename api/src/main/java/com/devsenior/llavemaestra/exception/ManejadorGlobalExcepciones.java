package com.devsenior.llavemaestra.exception;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import com.devsenior.llavemaestra.dto.ErrorResponse;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Hay datos inválidos en la petición");
        return responder(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonInvalido(HttpMessageNotReadableException ex) {
        return responder(HttpStatus.BAD_REQUEST, "El cuerpo de la petición tiene un formato inválido");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> parametroInvalido(MethodArgumentTypeMismatchException ex) {
        return responder(HttpStatus.BAD_REQUEST, "El parámetro '" + ex.getName() + "' tiene un formato inválido");
    }

    @ExceptionHandler(DatoInvalidoException.class)
    public ResponseEntity<ErrorResponse> datoInvalido(DatoInvalidoException ex) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> cuentaDesactivada(DisabledException ex) {
        return responder(HttpStatus.UNAUTHORIZED, "Tu cuenta está desactivada. Comunícate con la administración.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> credenciales(BadCredentialsException ex) {
        return responder(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> authException(AuthenticationException ex) {
        return responder(HttpStatus.UNAUTHORIZED, ex.getMessage() != null ? ex.getMessage() : "No autenticado");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accesoDenegado(AccessDeniedException ex) {
        return responder(HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta acción");
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException ex) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> reglaNegocio(ReglaNegocioException ex) {
        return responder(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorResponse> conflicto(ConflictoException ex) {
        return responder(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> datoDuplicado(DataIntegrityViolationException ex) {
        return responder(HttpStatus.CONFLICT, "Ya existe un registro con esos datos");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> responseStatus(ResponseStatusException ex) {
        return responder(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason());
    }

    private ResponseEntity<ErrorResponse> responder(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado)
                .body(new ErrorResponse(estado.value(), mensaje != null ? mensaje : estado.getReasonPhrase(), LocalDateTime.now()));
    }
}
