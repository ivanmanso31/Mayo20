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

import java.util.Optional;

import static  org.hamcrest.MatcherAssert.assertThat;
import static  org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class MascotaService02HamcrestTest {
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
        // Arrange(Preparar)
        MascotaRepository mascotaRepository = new MascotaRepositoryImpl();
        ExternalService externalService = new ExternalServiceImpl();
        mascotaService = new MascotaService(mascotaRepository, externalService);

        Propietario propietario = new Propietario("Dany", "Lima", "987654321");
        Mascota mascota = new Mascota();
        mascota.setNombre("Garfield");
        mascota.setPropietario(propietario);

        //Act(Actuar)
        Mascota registrada = mascotaService.registrarMascota(mascota);

        //Assert(Afirmar) : Hamcrest

        //Verificar las propiedades de la mascota registrada.
        assertThat(registrada, is(notNullValue()));
        assertThat(registrada.getNombre(), is(equalTo("Garfield")));
        assertThat(registrada.getPropietario(), is(notNullValue()));
        assertThat(registrada.getPropietario().getNombre(), is(equalTo("Dany")));
        assertThat(registrada.getPropietario().getCiudad(), is(equalTo("Lima")));
        assertThat(registrada.getPropietario().getTelefono(), is(equalTo("987654321")));
        assertThat(registrada, is(sameInstance(mascota)));
        assertThat(registrada, is(notNullValue()));

        // Verificar las propiedades del propietario con Hamcrest
        assertThat(registrada.getPropietario(), hasProperty("nombre", is("Dany")));
        assertThat(registrada.getPropietario(), hasProperty("ciudad", is("Lima")));
        assertThat(registrada.getPropietario(), hasProperty("telefono", is("987654321")));

        //Comprobar con más matchers de Hamcrest
        assertThat(registrada.getId(), is(greaterThanOrEqualTo(0)));
    }

    @Test
    @DisplayName("Registrar mascota con nombre vacío")
    void testRegistrarMascotaConNombreVacio() {
        // Arrange
        Mascota mascota = new Mascota();
        mascota.setNombre("");
        Propietario propietario = new Propietario("Dany", "Lima", "987654321");
        mascota.setPropietario(propietario);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mascotaService.registrarMascota(mascota);
        });
        assertThat(exception.getMessage(), is(equalTo("El nombre de la mascota no puede estar vacío.")));
    }

    @Test
    @DisplayName("Registrar mascota sin propietario")
    void testRegistrarMascotaSinPropietario() {
        // Arrange
        Mascota mascota = new Mascota();
        mascota.setNombre("Garfield");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mascotaService.registrarMascota(mascota);
        });
        assertThat(exception.getMessage(), is(equalTo("La mascota debe tener un propietario.")));
    }

    @Test
    @DisplayName("Registrar mascota con propietario sin teléfono")
    void testRegistrarMascotaConPropietarioSinTelefono() {
        // Arrange
        Propietario propietario = new Propietario("Dany", "Lima", "");
        Mascota mascota = new Mascota();
        mascota.setNombre("Garfield");
        mascota.setPropietario(propietario);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mascotaService.registrarMascota(mascota);
        });
        assertThat(exception.getMessage(), is(equalTo("El propietario debe tener un teléfono registrado.")));
    }

    @Test
    @DisplayName("Registrar mascota sin vacunas al día")
    void testRegistrarMascotaSinVacunas() {
        // Arrange
        Propietario propietario = new Propietario("Dany", "Lima", "987654321");
        Mascota mascota = new Mascota();
        mascota.setNombre("Garfield");
        mascota.setPropietario(propietario);

        // Act
        ExternalService externalService = new ExternalService() {
            @Override
            public boolean validarVacunas(Mascota mascota) {
                return false;
            }

            @Override
            public boolean verificarRegistroMunicipal(Mascota mascota) {
                return true;
            }
        };
        mascotaService = new MascotaService(new MascotaRepositoryImpl(), externalService);

        // Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            mascotaService.registrarMascota(mascota);
        });
        assertThat(exception.getMessage(), is(equalTo("La mascota no tiene todas las vacunas al día.")));
    }

    @Test
    @DisplayName("Registrar mascota no registrada en el municipio")
    void testRegistrarMascotaNoRegistradaMunicipio() {
        // Arrange
        Propietario propietario = new Propietario("Dany", "Lima", "987654321");
        Mascota mascota = new Mascota();
        mascota.setNombre("Garfield");
        mascota.setPropietario(propietario);

        // Act
        ExternalService externalService = new ExternalService() {
            @Override
            public boolean validarVacunas(Mascota mascota) {
                return true;
            }

            @Override
            public boolean verificarRegistroMunicipal(Mascota mascota) {
                return false;
            }
        };
        mascotaService = new MascotaService(new MascotaRepositoryImpl(), externalService);

        // Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            mascotaService.registrarMascota(mascota);
        });
        assertThat(exception.getMessage(), is(equalTo("La mascota no está registrada en el municipio.")));
    }

    @Test
    @DisplayName("Registrar mascota ya registrada")
    void testRegistrarMascotaYaRegistrada() {
        // Arrange
        MascotaRepository mascotaRepository = new MascotaRepositoryImpl();
        ExternalService externalService = new ExternalServiceImpl();
        mascotaService = new MascotaService(mascotaRepository, externalService);

        Propietario propietario = new Propietario("Dany", "Lima", "987654321");
        Mascota mascota = new Mascota();
        mascota.setNombre("Garfield");
        mascota.setPropietario(propietario);

        mascotaService.registrarMascota(mascota); // Registramos la mascota una vez

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            mascotaService.registrarMascota(mascota); // Intentamos registrarla de nuevo
        });
        assertThat(exception.getMessage(), is(equalTo("Esta mascota ya está registrada.")));
    }

    @Test
    @DisplayName("Buscar mascota por ID existente")
    void testBuscarMascotaPorIdExistente() {
        // Arrange
        MascotaRepository mascotaRepository = new MascotaRepositoryImpl();
        ExternalService externalService = new ExternalServiceImpl();
        mascotaService = new MascotaService(mascotaRepository, externalService);

        Propietario propietario = new Propietario("Dany", "Lima", "987654321");
        Mascota mascota = new Mascota();
        mascota.setNombre("Garfield");
        mascota.setPropietario(propietario);
        Mascota registrada = mascotaService.registrarMascota(mascota);

        // Act
        Optional<Mascota> resultado = mascotaService.buscarMascotaPorId(registrada.getId());

        // Assert
        assertThat(resultado.isPresent(), is(true));
        assertThat(resultado.get(), is(equalTo(registrada)));
    }

    @Test
    @DisplayName("Buscar mascota por ID inexistente")
    void testBuscarMascotaPorIdInexistente() {
        // Arrange
        MascotaRepository mascotaRepository = new MascotaRepositoryImpl();
        ExternalService externalService = new ExternalServiceImpl();
        mascotaService = new MascotaService(mascotaRepository, externalService);

        // Act
        Optional<Mascota> resultado = mascotaService.buscarMascotaPorId(999); // ID inexistente

        // Assert
        assertThat(resultado.isPresent(), is(false));
    }

    @Test
    @DisplayName("Eliminar mascota por ID existente")
    void testEliminarMascotaPorIdExistente() {
        // Arrange
        MascotaRepository mascotaRepository = new MascotaRepositoryImpl();
        ExternalService externalService = new ExternalServiceImpl();
        mascotaService = new MascotaService(mascotaRepository, externalService);

        Propietario propietario = new Propietario("Dany", "Lima", "987654321");
        Mascota mascota = new Mascota();
        mascota.setNombre("Garfield");
        mascota.setPropietario(propietario);
        Mascota registrada = mascotaService.registrarMascota(mascota);

        // Act
        mascotaService.eliminarMascotaPorId(registrada.getId());

        // Assert
        Optional<Mascota> resultado = mascotaService.buscarMascotaPorId(registrada.getId());
        assertThat(resultado.isPresent(), is(false));
    }

    @Test
    @DisplayName("Intentar eliminar mascota por ID inexistente")
    void testEliminarMascotaPorIdInexistente() {
        // Arrange
        MascotaRepository mascotaRepository = new MascotaRepositoryImpl();
        ExternalService externalService = new ExternalServiceImpl();
        mascotaService = new MascotaService(mascotaRepository, externalService);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mascotaService.eliminarMascotaPorId(999); // ID inexistente
        });
        assertThat(exception.getMessage(), is(equalTo("No se puede eliminar: No se encontró mascota con el ID proporcionado.")));
    }

    @Test
    @DisplayName("Registrar mascota con nombre nulo")
    void testRegistrarMascotaConNombreNulo() {
        // Arrange
        Propietario propietario = new Propietario("Dany", "Lima", "987654321");
        Mascota mascota = new Mascota();
        mascota.setNombre(null); // Nombre nulo
        mascota.setPropietario(propietario);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mascotaService.registrarMascota(mascota);
        });
        assertThat(exception.getMessage(), is(equalTo("El nombre de la mascota no puede estar vacío.")));
    }
    @Test
    @DisplayName("Registrar mascota con propietario con teléfono nulo")
    void testRegistrarMascotaConPropietarioConTelefonoNulo() {
        // Arrange
        Propietario propietario = new Propietario("Dany", "Lima", null);
        Mascota mascota = new Mascota();
        mascota.setNombre("Garfield");
        mascota.setPropietario(propietario);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mascotaService.registrarMascota(mascota);
        });
        assertThat(exception.getMessage(), is(equalTo("El propietario debe tener un teléfono registrado.")));
    }

}
