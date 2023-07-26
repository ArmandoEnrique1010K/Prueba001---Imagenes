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
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ContenedorImagenesImpl implements ContenedorImagenes{
    
    @Value("${storage.location}")
    private String storageLocation;
    
    @PostConstruct
    @Override
    public void iniciarContenedorImagenes() {
        try {
            Files.createDirectories(Paths.get(storageLocation));
        } catch (IOException exception){
            throw new AlmacenException("Error al inicializar la ubicación en el almacen de imagenes");
        }
    }

    @Override
    public String almacenarUnaImagen(MultipartFile imagen, String nombre) {
        // String nombreImagen = imagen.getOriginalFilename();
        if(imagen.isEmpty()){
            throw new AlmacenException("No se puede almacenar un archivo vacio");
        }
        
        try {
            
            // Generar el nuevo nombre usando el parámetro 'name'.
            String nuevoNombre = nombre;
            
            // Copiar la imagen al servidor con el nuevo nombre
            InputStream inputStream = imagen.getInputStream();
            Files.copy(inputStream, Paths.get(storageLocation).resolve(nuevoNombre), StandardCopyOption.REPLACE_EXISTING);
            
            // Retornar el nombre de la imagen almacenada
            return nuevoNombre;

        } catch (IOException exception){
            throw new AlmacenException("Error al almacenar la imagen " , exception);
        }
    }

    @Override
    public Path cargarImagen(String nombreImagen) {
        Path imagenPath = Paths.get(storageLocation).resolve(nombreImagen);
        if (!Files.exists(imagenPath)){
            throw new FileNotFoundException("No se pudo encontrar el archivo " + nombreImagen);
        }
        return imagenPath;
    }

    @Override
    public Resource cargarComoRecurso(String nombreImagen) {
        try {
            Path imagen = cargarImagen(nombreImagen);
            Resource recurso = new UrlResource(imagen.toUri());
            
            if(recurso.exists() || recurso.isReadable()) {
                return recurso;
            } else {
                throw new FileNotFoundException("No se pudo encontrar el archivo " + nombreImagen);
            }
        } catch (MalformedURLException exception){
            throw new FileNotFoundException("No se pudo encontrar el archivo " + nombreImagen, exception);
        }
    }

    @Override
    public void eliminarImagen(String nombreImagen) {
        Path imagen = cargarImagen(nombreImagen);
        try {
            FileSystemUtils.deleteRecursively(imagen);
        } catch (Exception exception){
            System.out.println(exception);
        }
    }
    
}





























