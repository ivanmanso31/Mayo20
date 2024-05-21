package com.softtek.Mayo20;

import com.softtek.Mayo20.modelo.Mascota;
import com.softtek.Mayo20.modelo.Propietario;
import com.softtek.Mayo20.repo.MascotaRepository;
import com.softtek.Mayo20.repo.MascotaRepositoryImpl;
import com.softtek.Mayo20.servicio.ExternalService;
import com.softtek.Mayo20.servicio.ExternalServiceImpl;
import com.softtek.Mayo20.servicio.MascotaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class MascotaService01JUnitTest {

    MascotaService mascotaService;
    MascotaRepository mascotaRepository;
    ExternalService externalService;

    @BeforeEach
    void setUp() {
        mascotaRepository = new MascotaRepositoryImpl();
        externalService = new ExternalServiceImpl();
        mascotaService = new MascotaService(mascotaRepository, externalService);
    }

    @Test
    @DisplayName("Registrar mascota correctamente")
    void testRegistrarMascotaCorrectamente() {

        Propietario propietario = new Propietario("Dany", "Lima", "987654321");
        Mascota mascota = new Mascota();
        mascota.setNombre("Garfield");
        mascota.setPropietario(propietario);

        //Act(Actuar)
        Mascota registrada = mascotaService.registrarMascota(mascota);

        //Assert(Afirmar) : JUnit

        //Verifica que el objeto no es null.
        assertNotNull(registrada, "La mascota registrada no debería ser null.");

        //Verifica que dos referencias apuntan al mismo objeto, util para confirmar que la instancia no ha sido clonada o modificada inesperadamente.
        assertSame(mascota, registrada, "La mascota registrada deberia ser la misma que la ingresada.");

        //Verifica que el valor esperado coincide con el actual.
        assertEquals("Garfield", registrada.getNombre(), "El nombre de la mascota registrada deberia ser 'Garfield'.");

        //Confirma que el propietario de la mascota registrada es el mismo que se proporcionó.
        assertSame(propietario, registrada.getPropietario(), "El propietario de la mascota registrada deberia ser el mismo que el ingresado.");

        //Comprueba los detalles del propietario para garantizar la precisión de los datos.
        assertEquals("Dany", registrada.getPropietario().getNombre(), "El nombre del propietario deberia ser 'Dany'.");
        assertEquals("Lima", registrada.getPropietario().getCiudad(), "La ciudad del propietario deberia ser 'Lima'.");
        assertEquals("987654321", registrada.getPropietario().getTelefono(), "El teléfono del propietario deberia ser '987654321'.");

        //Comprobar con más aserciones de JUnit S
        assertTrue(registrada.getId() > 1, "ID debe ser positivo");

        //Verificación adicional agrupando varias aserciones. Todas deben pasar, o el test fallará.
        assertAll("Propiedades de la mascota",
                () -> assertEquals("Garfield", registrada.getNombre(), "El nombre deberia coincidir."),
                () -> assertNotNull(registrada.getPropietario(), "El propietario no debe ser null."),
                () -> assertEquals("Dany", registrada.getPropietario().getNombre(), "El nombre del propiestrario debe ser 'Dany'."),
                () -> assertEquals("Lima", registrada.getPropietario().getCiudad(), "La ciudad del propiestrario debe ser 'Lima'."),
                () -> assertEquals("987654321", registrada.getPropietario().getTelefono(), "El teléfono del propietario deberia ser '987654321'.")
        );

        //Comprueba que las siguientes operaciones no lanzan excepciones, lo cual es útil para confirmar que las propiedades esenciales estan presentes.
        assertDoesNotThrow(() -> registrada.getNombre(), "Obtener el nombre de la mascota no debería lanzar una excepción");
        assertDoesNotThrow(() -> registrada.getPropietario().getCiudad(), "Obtener la ciudad del propietario no debería lanzar una excepción");

    }



}
