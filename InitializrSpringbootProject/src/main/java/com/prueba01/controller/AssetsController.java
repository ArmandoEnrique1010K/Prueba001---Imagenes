package com.prueba01.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prueba01.service.ContenedorImagenesImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/assets")
public class AssetsController {

	@Autowired
	private ContenedorImagenesImpl contenedorImagenesImpl;
	
        // SI EL USUARIO INTENTA ABRIR LA IMAGEN EN UNA NUEVA PESTAÑA, ENTONCES VA A MOSTRAR
        
        // UN CODIGO BINARIO:
        /*
	@GetMapping("/{filename:.+}")
	public Resource obtenerRecurso(@PathVariable("filename") String filename) {
		return contenedorImagenesImpl.cargarComoRecurso(filename);
	}
        */
        
        // LA IMAGEN EN TAMAÑO COMPLETO:
        @GetMapping("/{filename:.+}")
        public ResponseEntity<Resource> obtenerRecurso(@PathVariable("filename") String filename) {
        Resource recurso = contenedorImagenesImpl.cargarComoRecurso(filename);
        return ResponseEntity.ok()
                // Ajusta el tipo de contenido según el formato de tus imágenes
                .contentType(MediaType.IMAGE_JPEG)
                .contentType(MediaType.IMAGE_PNG)
                .contentType(MediaType.IMAGE_GIF)
                .body(recurso);
        }

}
