package com.prueba01.controller;

import com.prueba01.dto.GaleriaDto;
import com.prueba01.entity.GaleriaEntity;
import com.prueba01.service.ContenedorImagenesImpl;
import com.prueba01.service.GaleriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class GaleriaController {

    @Autowired
    private GaleriaService galeriaService;

    @Autowired
    private ContenedorImagenesImpl contenedorImagenesImpl;

    // MOSTRAR TODAS LAS IMAGENES
    @GetMapping("/")
    public String listarImagenes(Model modelo) {
        modelo.addAttribute("galeria_imagenes", galeriaService.getAllRegistros());
        return "index.html";
    }

    @GetMapping("/{id}")
    public String mostrarUnaImagen(@PathVariable("id") Long id, Model modelo) {
        var varUnRegistro = galeriaService.getRegistro(id);
        modelo.addAttribute("caracteristicas_imagen", varUnRegistro);
        return "plantilla.html";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioDeAñadir(Model modelo) {
        GaleriaEntity modeloEntity = new GaleriaEntity();
        modelo.addAttribute("datos_imagen", modeloEntity);
        return "form.html";
    }

    @PostMapping("/nuevo")
    public String guardarModelo(@ModelAttribute("datos_imagen")
            @Validated GaleriaDto objetogaleria,
            BindingResult bindingResult, Model modelo) {

        if (bindingResult.hasErrors() || objetogaleria.getImagen().isEmpty()
                || objetogaleria.getNombre().isEmpty() || objetogaleria.getDescripcion().isEmpty()) {
            modelo.addAttribute("datos_imagen", objetogaleria);
            return "form.html";
        }

        String rutaImagen = contenedorImagenesImpl.almacenarUnaImagen(objetogaleria.getImagen());
        objetogaleria.setRutaImagen(rutaImagen);
        galeriaService.createRegistro(objetogaleria);
        return "redirect:/";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioDeEditar(@PathVariable Long id, Model modelo) {
        var varUnRegistro = galeriaService.getRegistro(id);
        modelo.addAttribute("parametros_imagen", varUnRegistro);
        return "formeditar.html";
    }

    @PostMapping("/{id}/editar")
    public String guardarDatosModelo(@PathVariable Long id, @ModelAttribute("parametros_imagen")
            @Validated GaleriaDto objetogaleria, BindingResult bindingResult,
            @RequestParam("imagen") MultipartFile imagen, Model modelo) {

        // Si hay errores de validación, volvemos a mostrar el formulario de edición con los mensajes de error
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("parametros_imagen", objetogaleria);
            return "formeditar.html";
        }
        
        // Si no hay errores de validación y se seleccionó una nueva imagen, actualizamos el modelo
        if(!imagen.isEmpty()){
            objetogaleria.setImagen(imagen);
        } else {
            // Si no se seleccionó una nueva imagen, establecemos la imagen actual del modelo
            // para evitar que se borre la ruta de la imagen anterior al actualizar el modelo
            GaleriaDto objetoActual = galeriaService.getRegistro(id);
            objetogaleria.setImagen(objetoActual.getImagen());
        }
        
        // Si no hay errores de validación, actualizamos el modelo
        GaleriaDto objetoActualizado = galeriaService.updateRegistro(objetogaleria);
        
        if (objetoActualizado == null){
            // Modelo no encontrado, puedes manejar el caso en consecuencia (por ejemplo, mostrar un mensaje de error)
            return "redirect:/";
        }
        
        return "redirect:/";

    }

    @GetMapping("/{id}/cambioestadoafalse")
    public String cambiarEstadoTrueaFalse(@PathVariable Long id){
        galeriaService.changeStateFalseRegistro(id);
        return "redirect:/";
    }
    
    @GetMapping("/{id}/eliminardefinitivamente")
    public String eliminarDefinitivamente(@PathVariable Long id){
        GaleriaDto galeriaEntity = galeriaService.getRegistro(id);
        galeriaService.deleteRegistro(id);
        contenedorImagenesImpl.eliminarImagen(galeriaEntity.getRutaImagen());
        return "redirect:/";
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
