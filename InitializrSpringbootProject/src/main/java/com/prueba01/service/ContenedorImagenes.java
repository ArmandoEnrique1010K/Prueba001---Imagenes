package com.prueba01.service;

import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ContenedorImagenes {

    public void iniciarContenedorImagenes();

    public String almacenarUnaImagen(MultipartFile imagen, String nombre);

    public Path cargarImagen(String nombreImagen);

    public Resource cargarComoRecurso(String nombreImagen);

    public void eliminarImagen(String nombreImagen);
    
}
