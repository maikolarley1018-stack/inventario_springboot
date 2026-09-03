package co.edu.sena.inventario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Error de formato JSON o Enum inválido
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> manejarErrorJson(HttpMessageNotReadableException e) {
        String mensaje = e.getMessage();
        if (mensaje != null && mensaje.contains("Prioridad")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Prioridad no válida. Las opciones permitidas son: BAJA, MEDIA, ALTA, URGENTE.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("El cuerpo de la petición (JSON) está mal formado o contiene un valor inválido.");
    }

    // 2. Método HTTP no soportado (Ejemplo: Usar PUT en lugar de POST o en rutas
    // incompletas)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> manejarMetodoNoSoportado(HttpRequestMethodNotSupportedException e) {
        String mensaje = "El método HTTP '" + e.getMethod() + "' no está permitido para la ruta consultada.\n\n" +
                "Ejemplos de uso correcto:\n" +
                "- Para crear un pedido usa: POST /pedidos\n" +
                "- Para despachar un pedido existente usa: PUT /pedidos/{id}/despachar (Ejemplo: /pedidos/1/despachar)";

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(mensaje);
    }

    // 3. Ruta no encontrada (Error 404)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> manejarRutaNoEncontrada(NoResourceFoundException e) {
        String mensaje = "La ruta consultada '" + e.getResourcePath() + "' no existe.\n\n" +
                "Rutas disponibles en el sistema:\n" +
                "- GET  /pedidos\n" +
                "- GET  /pedidos/{id}\n" +
                "- POST /pedidos\n" +
                "- PUT  /pedidos/{id}/confirmar\n" +
                "- PUT  /pedidos/{id}/cancelar\n" +
                "- PUT  /pedidos/{id}/despachar\n" +
                "- GET  /pedidos/urgentes\n" +
                "- GET  /pedidos/resumen\n" +
                "- GET  /pedidos/siguiente";

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje);
    }

    // 4. Parámetro obligatorio faltante
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> manejarParametroFaltante(MissingServletRequestParameterException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Falta el parámetro requerido en la consulta: '" + e.getParameterName() + "'.");
    }
}