
package com.ecommerce.catalogo.exception;

import com.ecommerce.catalogo.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Producto no encontrado
    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<ApiError> manejarNoEncontrado(
            ProductoNotFoundException ex,
            HttpServletRequest request) {

        return construir(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request,
                null
        );
    }

    // SKU duplicado
    @ExceptionHandler(SkuDuplicadoException.class)
    public ResponseEntity<ApiError> manejarSkuDuplicado(
            SkuDuplicadoException ex,
            HttpServletRequest request) {

        return construir(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request,
                null
        );
    }

    // Restricciones de PostgreSQL
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> manejarIntegridad(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.warn(
                "Violacion de integridad de datos",
                ex
        );

        return construir(
                HttpStatus.CONFLICT,
                "La operacion viola una restriccion de integridad",
                request,
                null
        );
    }

    // Solicitudes invalidas
    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<ApiError> manejarSolicitudInvalida(
            SolicitudInvalidaException ex,
            HttpServletRequest request) {

        return construir(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request,
                null
        );
    }

    // Archivos vacios o formatos no permitidos
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> manejarArgumentoInvalido(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return construir(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request,
                null
        );
    }

    // Imagenes que superan el limite de carga
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> manejarArchivoGrande(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request) {

        return construir(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "La imagen supera el tamano maximo permitido de 5 MB",
                request,
                null
        );
    }

    // Errores de lectura o almacenamiento
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiError> manejarArchivo(
            IOException ex,
            HttpServletRequest request) {

        log.error(
                "Error procesando un archivo en {}",
                request.getRequestURI(),
                ex
        );

        return construir(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "No se pudo procesar el archivo solicitado",
                request,
                null
        );
    }

    // Errores de validacion de DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> manejarValidacion(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errores = new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(fe -> errores.merge(
                        fe.getField(),
                        String.valueOf(fe.getDefaultMessage()),
                        (a, b) -> a + "; " + b
                ));

        return construir(
                HttpStatus.BAD_REQUEST,
                "La solicitud contiene datos invalidos",
                request,
                errores
        );
    }

    // JSON incorrecto
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> manejarCuerpoIlegible(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return construir(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud es invalido o contiene JSON incorrecto",
                request,
                null
        );
    }

    // Parametros con tipos incorrectos
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> manejarTipoInvalido(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        return construir(
                HttpStatus.BAD_REQUEST,
                "El parametro '" + ex.getName() + "' tiene un valor invalido",
                request,
                null
        );
    }

    // Parametros obligatorios ausentes
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> manejarParametroFaltante(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        return construir(
                HttpStatus.BAD_REQUEST,
                "Falta el parametro obligatorio '" +
                        ex.getParameterName() + "'",
                request,
                null
        );
    }
// Imagen no encontrada
@ExceptionHandler(ImagenNotFoundException.class)
public ResponseEntity<ApiError> manejarImagenNoEncontrada(
        ImagenNotFoundException ex,
        HttpServletRequest request) {

    return construir(
            HttpStatus.NOT_FOUND,
            ex.getMessage(),
            request,
            null
    );
}

    // Imagen que supera el tamaño permitido
    @ExceptionHandler(ImagenDemasiadoGrandeException.class)
    public ResponseEntity<ApiError> manejarImagenGrande(
            ImagenDemasiadoGrandeException ex,
            HttpServletRequest request) {

        return construir(
                HttpStatus.PAYLOAD_TOO_LARGE,
                ex.getMessage(),
                request,
                null
        );
    }

    // Otros errores
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> manejarGenerico(
            Exception ex,
            HttpServletRequest request) {

        if (ex instanceof ErrorResponse errorResponse) {

            HttpStatus status = HttpStatus.resolve(
                    errorResponse.getStatusCode().value()
            );

            if (status == null) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }

            return construir(
                    status,
                    errorResponse.getBody().getDetail(),
                    request,
                    null
            );
        }

        log.error(
                "Error inesperado procesando {}",
                request.getRequestURI(),
                ex
        );

        return construir(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                request,
                null
        );
    }

    // Construir respuesta uniforme
    private ResponseEntity<ApiError> construir(
            HttpStatus status,
            String mensaje,
            HttpServletRequest request,
            Map<String, String> errores) {

        ApiError body = new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                request.getRequestURI(),
                errores
        );

        return ResponseEntity
                .status(status)
                .body(body);
    }
}
