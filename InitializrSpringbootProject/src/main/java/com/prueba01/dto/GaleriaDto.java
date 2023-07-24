package com.prueba01.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GaleriaDto {
    
    private Long id;
    
    @Size(min = 5, message = "Debe contener al menos 5 caracteres")
    private String nombre;
    
    @NotBlank(message = "Este campo no puede estar en blanco")
    private String descripcion;
    
    private Boolean estado;
    private String rutaImagen;
    private MultipartFile imagen;
    
}
