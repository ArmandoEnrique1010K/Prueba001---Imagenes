package com.prueba01.service;

import com.prueba01.dto.GaleriaDto;
import com.prueba01.entity.GaleriaEntity;
import com.prueba01.repository.GaleriaRepository;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GaleriaServiceImpl implements GaleriaService {

    @Autowired
    private GaleriaRepository galeriaRepository;

    @Autowired
    private ContenedorImagenesImpl contenedorImagenesImpl;

    @Override
    public List<GaleriaDto> getAllRegistros() {
        List<GaleriaEntity> listGaleriaEntity = galeriaRepository.findAll();
        List<GaleriaDto> list = new ArrayList<>();
        for (GaleriaEntity galeriaEntity : listGaleriaEntity) {
            list.add(GaleriaDto.builder()
                    .id(galeriaEntity.getId())
                    .nombre(galeriaEntity.getNombre())
                    .descripcion(galeriaEntity.getDescripcion())
                    .estado(galeriaEntity.getEstado())
                    .rutaImagen(galeriaEntity.getRutaImagen())
                    .imagen(galeriaEntity.getImagen())
                    .build()
            );
        }
        return list;
    }

    @Override
    public GaleriaDto getRegistro(Long id) {
        GaleriaEntity galeriaEntity = galeriaRepository.findById(id).orElse(null);

        if (null == galeriaEntity) {
            return null;
        }

        return GaleriaDto.builder()
                .id(galeriaEntity.getId())
                .nombre(galeriaEntity.getNombre())
                .descripcion(galeriaEntity.getDescripcion())
                .estado(galeriaEntity.getEstado())
                .rutaImagen(galeriaEntity.getRutaImagen())
                .imagen(galeriaEntity.getImagen())
                .build();
    }

    @Override
    public GaleriaDto createRegistro(GaleriaDto registro) {
        GaleriaEntity galeriaEntity = GaleriaEntity.builder()
                .nombre(registro.getNombre())
                .descripcion(registro.getDescripcion())
                .estado(Boolean.TRUE)
                .rutaImagen(registro.getRutaImagen())
                .imagen(registro.getImagen())
                .build();

        String rutaImagen = contenedorImagenesImpl.almacenarUnaImagen(registro.getImagen());
        registro.setRutaImagen(rutaImagen);
        galeriaRepository.save(galeriaEntity);
        registro.setId(galeriaEntity.getId());
        return registro;
    }

    @Override
    public GaleriaDto updateRegistro(GaleriaDto registro) {
        GaleriaEntity galeriaEntity = galeriaRepository.findById(registro.getId()).orElse(null);

        if (galeriaEntity == null) {
            // Modelo no encontrado, puedes lanzar una excepción o manejar el caso en consecuencia
            return null;
        }
        
        // Si la imagen no está vacía, eliminamos la imagen anterior y almacenamos la nueva
        if (!registro.getImagen().isEmpty()) {
            contenedorImagenesImpl.eliminarImagen(galeriaEntity.getRutaImagen());
            String rutaImagen = contenedorImagenesImpl.almacenarUnaImagen(registro.getImagen());
            registro.setRutaImagen(rutaImagen);
        } else {
            // Si la imagen está vacía, conservamos la imagen actual en la GaleriaDto
            registro.setImagen(galeriaEntity.getImagen());
            registro.setRutaImagen(galeriaEntity.getRutaImagen());
        }

        // Actualizar los campos de galeriaEntity con los valores del GaleriaDto
        galeriaEntity.setNombre(registro.getNombre());
        galeriaEntity.setDescripcion(registro.getDescripcion());
        galeriaEntity.setRutaImagen(registro.getRutaImagen());
        galeriaEntity.setEstado(Boolean.TRUE);

        galeriaRepository.save(galeriaEntity);
        return registro;
    }

    @Override
    public void changeStateFalseRegistro(Long id) {
        GaleriaEntity galeriaEntity = galeriaRepository.findById(id).orElse(null);
        
        if(galeriaEntity != null){
            galeriaEntity.setEstado(Boolean.FALSE);
            galeriaRepository.save(galeriaEntity);
        }
    }

    @Override
    public void deleteRegistro(Long id) {
        GaleriaEntity galeriaEntity = galeriaRepository.findById(id).orElse(null);
        
        if(galeriaEntity != null){
            // Obtener la ruta de la imagen asociada
            String rutaImagen = galeriaEntity.getRutaImagen();
            // Eliminar físicamente el archivo de la imagen
            if (rutaImagen != null && !rutaImagen.isEmpty()){
                File archivoImagen = new File(rutaImagen);
                if (archivoImagen.exists()){
                    archivoImagen.delete();
                }
            }
            // Eliminar el registro de la base de datos
            galeriaRepository.delete(galeriaEntity);
        }
    }

}



























