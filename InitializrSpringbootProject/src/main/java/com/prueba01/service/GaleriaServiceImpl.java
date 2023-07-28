package com.prueba01.service;

import com.prueba01.dto.GaleriaDto;
import com.prueba01.entity.GaleriaEntity;
import com.prueba01.repository.GaleriaRepository;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class GaleriaServiceImpl implements GaleriaService {

    @Autowired
    private GaleriaRepository galeriaRepository;

    @Autowired
    private ContenedorImagenesImpl contenedorImagenesImpl;

    private String generarUUIDComoNombre(String extension) {
        UUID uuid = UUID.randomUUID();
        return uuid.toString() + extension;
    }

    private String obtenerExtension(String nombreOriginal) {
        int extensionIndex = nombreOriginal.lastIndexOf('.');
        return (extensionIndex != -1) ? nombreOriginal.substring(extensionIndex) : "";
    }

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

        // Obtener la extensión del nombre original de la imagen
        String extension = obtenerExtension(registro.getImagen().getOriginalFilename());
        
        // Generar un nuevo nombre único para la imagen utilizando UUID y su extensión
        String nuevoNombreImagen = generarUUIDComoNombre(extension);
        
        // Almacenar la imagen con el nuevo nombre único y obtener la ruta donde se guardó
        String rutaImagen = contenedorImagenesImpl.almacenarUnaImagen(registro.getImagen(), nuevoNombreImagen);

        // Configurar los datos para la entidad
        GaleriaEntity galeriaEntity = GaleriaEntity.builder()
                .nombre(registro.getNombre())
                .descripcion(registro.getDescripcion())
                .estado(Boolean.TRUE)
                .rutaImagen(/*registro.getRutaImagen()*/ rutaImagen)
                // .imagen(registro.getImagen())
                .build();

        // Guardar la entidad en la base de datos
        galeriaRepository.save(galeriaEntity);

        // Actualizar el objeto GaleriaDto con el ID generado y la ruta de la imagen
        registro.setId(galeriaEntity.getId());
        registro.setRutaImagen(rutaImagen);

        return registro;
    }
    

@Override
public GaleriaDto updateRegistro(GaleriaDto registro) {
    GaleriaEntity galeriaEntity = galeriaRepository.findById(registro.getId()).orElse(null);

    if (galeriaEntity == null) {
        // Modelo no encontrado, puedes lanzar una excepción o manejar el caso en consecuencia
        return null;
    }

    /*
    // Si el archivo subido no es de tipo image, entonces no se va a guardar el registro.
    if(!registro.getImagen().getContentType().startsWith("image/")){
        return null;
    }
*/
    
    // Si la imagen no está vacía y es diferente a la imagen actual, eliminar la imagen anterior y almacenar la nueva
    if (registro.getImagen() != null && !registro.getImagen().isEmpty() && !registro.getImagen().equals(galeriaEntity.getImagen())) {
        contenedorImagenesImpl.eliminarImagen(galeriaEntity.getRutaImagen());

        // Almacenar la nueva imagen con un nuevo nombre único basado en UUID y su extensión
        String extension = obtenerExtension(registro.getImagen().getOriginalFilename());
        String nuevoNombreImagen = generarUUIDComoNombre(extension);
        String rutaImagen = contenedorImagenesImpl.almacenarUnaImagen(registro.getImagen(), nuevoNombreImagen);

        registro.setRutaImagen(rutaImagen);
    } else {
        // Si la imagen está vacía o es igual a la imagen actual, conservar la imagen actual en la GaleriaDto
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

        if (galeriaEntity != null) {
            galeriaEntity.setEstado(Boolean.FALSE);
            galeriaRepository.save(galeriaEntity);
        }
    }

    @Override
    public void deleteRegistro(Long id) {
        GaleriaEntity galeriaEntity = galeriaRepository.findById(id).orElse(null);

        if (galeriaEntity != null) {
            // Obtener la ruta de la imagen asociada
            String rutaImagen = galeriaEntity.getRutaImagen();
            // Eliminar físicamente el archivo de la imagen
            if (rutaImagen != null && !rutaImagen.isEmpty()) {
                File archivoImagen = new File(rutaImagen);
                if (archivoImagen.exists()) {
                    archivoImagen.delete();
                }
            }
            // Eliminar el registro de la base de datos
            galeriaRepository.delete(galeriaEntity);
        }
    }

}
