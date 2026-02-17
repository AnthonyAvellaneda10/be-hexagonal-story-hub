package com.uni.pe.storyhub.application.util;

import com.uni.pe.storyhub.infrastructure.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Component
public class ImageValidator {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/webp", "image/svg+xml");
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "webp", "svg");

    /**
     * Validates a single image file.
     * 
     * @param file      The file to validate.
     * @param fieldName Descriptive name of the field for error messages.
     */
    public void validate(MultipartFile file, String fieldName) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("La imagen (" + fieldName + ") es obligatoria", 0, 400);
        }

        // Validate size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("El tamaño de la imagen '" + fieldName + "' no debe exceder los 5MB", 0, 400);
        }

        // Validate Content-Type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException("El formato de '" + fieldName + "' no es una imagen válida o no está soportado",
                    0, 400);
        }

        // Validate Extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException("El archivo '" + fieldName + "' debe tener una extensión válida", 0, 400);
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("Extensión de archivo '" + extension + "' no permitida para '" + fieldName
                    + "'. Solo se admiten jpg, jpeg, png, webp y svg", 0, 400);
        }
    }

    /**
     * Validates that exactly one file is present in a list.
     * 
     * @param files     List of files.
     * @param fieldName Descriptive name of the field for error messages.
     */
    public void validateSingle(List<MultipartFile> files, String fieldName) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException("La imagen (" + fieldName + ") es obligatoria", 0, 400);
        }
        if (files.size() > 1) {
            throw new BusinessException("Solo se permite subir una imagen a la vez para " + fieldName, 0, 400);
        }
        validate(files.get(0), fieldName);
    }
}
