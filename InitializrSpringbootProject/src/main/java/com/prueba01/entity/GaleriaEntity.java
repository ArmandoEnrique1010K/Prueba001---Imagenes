package com.prueba01.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Builder
@Entity
@Table(name="Tabla_imagenes")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class GaleriaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Long id;
    
    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "estado")
    private Boolean estado;
    
    /* PARA LAS IMAGENES */
    @Column(name = "rutaImagen")
    private String rutaImagen;
    @Transient
    private MultipartFile imagen;

}
