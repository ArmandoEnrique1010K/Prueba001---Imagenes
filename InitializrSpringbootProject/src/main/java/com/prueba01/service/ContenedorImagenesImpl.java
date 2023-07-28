package com.prueba01.service;

import com.prueba01.exception.AlmacenException;
import com.prueba01.exception.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ContenedorImagenesImpl implements ContenedorImagenes {

    @Value("${storage.location}")
    private String storageLocation;

    @PostConstruct
    @Override
    public void iniciarContenedorImagenes() {
        try {
            Files.createDirectories(Paths.get(storageLocation));
        } catch (IOException exception) {
            throw new AlmacenException("Error al inicializar la ubicación en el almacen de imagenes");
        }
    }

    @Override
    public String almacenarUnaImagen(MultipartFile imagen, String nombre) {
        // String nombreImagen = imagen.getOriginalFilename();
        if (imagen.isEmpty()) {
            throw new AlmacenException("No se puede almacenar un archivo vacio");
        }
        
        // Verificar si el tipo de archivo es válido (solo admitir JPG, PNG y GIF)
        String contentType = imagen.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new AlmacenException("Tipo de archivo no válido. Solo se admiten imágenes (JPG, PNG, GIF)");
        }
        
        try {

            // Obtener la extensión del archivo original
            String extension = obtenerExtension(imagen.getOriginalFilename());

            // Generar un código aleatorio usando UUID
            String codigoAleatorio = generarUUIDComoNombre("");

            // Generar el nuevo nombre usando el código aleatorio, el nombre original y la extensión del archivo original
            String nuevoNombre = codigoAleatorio + " - " + obtenerNombreOriginal(imagen.getOriginalFilename()) + extension;

            // Copiar la imagen al servidor con el nuevo nombre
            InputStream inputStream = imagen.getInputStream();
            Files.copy(inputStream, Paths.get(storageLocation).resolve(nuevoNombre), StandardCopyOption.REPLACE_EXISTING);

            // Retornar el nombre de la imagen almacenada
            return nuevoNombre;

        } catch (IOException exception) {
            throw new AlmacenException("Error al almacenar la imagen ", exception);
        }
    }

    // Función para generar un codigo aleatorio
    private String generarUUIDComoNombre(String extension) {
        UUID uuid = UUID.randomUUID();
        return uuid.toString() + extension;
    }

    // Función para obtener la extensión del nombre original de la imagen
    private String obtenerExtension(String nombreOriginal) {
        int extensionIndex = nombreOriginal.lastIndexOf('.');
        return (extensionIndex != -1) ? nombreOriginal.substring(extensionIndex) : "";
    }

    // Función para obtener el nombre original de la imagen (sin extensión)
    private String obtenerNombreOriginal(String nombreOriginal) {
        int extensionIndex = nombreOriginal.lastIndexOf('.');
        return (extensionIndex != -1) ? nombreOriginal.substring(0, extensionIndex) : nombreOriginal;
    }

    @Override
    public Path cargarImagen(String nombreImagen) {
        Path imagenPath = Paths.get(storageLocation).resolve(nombreImagen);
        if (!Files.exists(imagenPath)) {
            throw new FileNotFoundException("No se pudo encontrar el archivo " + nombreImagen);
        }
        return imagenPath;
    }

    @Override
    public Resource cargarComoRecurso(String nombreImagen) {
        try {
            Path imagen = cargarImagen(nombreImagen);
            Resource recurso = new UrlResource(imagen.toUri());

            if (recurso.exists() || recurso.isReadable()) {
                return recurso;
            } else {
                throw new FileNotFoundException("No se pudo encontrar el archivo " + nombreImagen);
            }
        } catch (MalformedURLException exception) {
            throw new FileNotFoundException("No se pudo encontrar el archivo " + nombreImagen, exception);
        }
    }

    @Override
    public void eliminarImagen(String nombreImagen) {
        Path imagen = cargarImagen(nombreImagen);
        try {
            FileSystemUtils.deleteRecursively(imagen);
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }

}
