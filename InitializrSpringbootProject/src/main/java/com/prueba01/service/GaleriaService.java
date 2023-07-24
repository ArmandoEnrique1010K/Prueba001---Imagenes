package com.prueba01.service;

import com.prueba01.dto.GaleriaDto;
import java.util.List;

public interface GaleriaService {
    
    public List<GaleriaDto> getAllRegistros();
    
    public GaleriaDto getRegistro(Long id);
    
    public GaleriaDto createRegistro(GaleriaDto registro);
    
    public GaleriaDto updateRegistro(GaleriaDto registro);
    
    // CAMBIAR EL ESTADO TRUE A FALSE
    public void changeStateFalseRegistro(Long id);
    
    // ELIMINAR DEFINITIVAMENTE
    public void deleteRegistro(Long id);
    
}
