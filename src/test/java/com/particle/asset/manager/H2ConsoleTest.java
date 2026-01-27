package com.particle.asset.manager;

//import com.particle.asset.manager.config.H2ConsoleConfig;

import com.particle.asset.manager.models.BusinessUnit;
import com.particle.asset.manager.models.AssetType;
import com.particle.asset.manager.repositories.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
//@Import(H2ConsoleConfig.class)  // ← Forza il caricamento
@TestInstance(TestInstance.Lifecycle.PER_CLASS)  // Una sola istanza per tutti i test
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)  // Controlla l'ordine di esecuzione
class H2ConsoleTest {

    @Autowired
    private BusinessUnitRepository businessUnitRepository;

    @Autowired
    private AssetTypeRepository assetTypeRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovementRepository movementRepository;

    @Test
    void openH2Console() throws Exception
    {
        System.out.println("===============================================");
        System.out.println("H2 Console: http://localhost:8080/h2-console");
        System.out.println("JDBC URL: jdbc:h2:mem:testdb");
        System.out.println("Username: sa");
        System.out.println("Password: (vuota)");
        System.out.println("===============================================");

        Thread.sleep(600000);
    }

    @Test
    @Order(1) // Annotazione per dare un'ordine ai vari metodi di testing (in questo caso)
    // {1, 2, ...} --> saranno effettuati in questo ordine qua
    void testInsertBusinessUnit()
    {
        BusinessUnit bu = new BusinessUnit();
        bu.setName("Test BU");
        BusinessUnit savedBu = businessUnitRepository.save(bu);

        assertEquals("Test BU", savedBu.getName());
    }

    @Test
    @Order(2)
    void testFindBusinessUnitById()
    {
        BusinessUnit foundBu = businessUnitRepository.findById(1L).orElseThrow();

        assertEquals("A", foundBu.getName());
    }

    @Test
    @Order(3)
    void testUpdateBusinessUnitByName()
    {
        Optional<BusinessUnit> bu = businessUnitRepository.findByName("B");

        BusinessUnit updatedFoundBu = bu.get();

        updatedFoundBu.setName("Particle");
        businessUnitRepository.save(updatedFoundBu);

        bu = businessUnitRepository.findByName("Particle");
        updatedFoundBu = bu.get();

        assertEquals("Particle", updatedFoundBu.getName());
    }

    @Test
    @Order(4)
    void testDeleteBusinessUnitById()
    {
        BusinessUnit deletedBu = businessUnitRepository.findById(3L).orElseThrow();
        businessUnitRepository.delete(deletedBu);

        assertTrue(businessUnitRepository.findById(3L).isEmpty());
    }

    @Test
    @Order(5)
    void testInsertType()
    {
        AssetType assetType = new AssetType();
        assetType.setName("Test Type");
        AssetType savedAssetType = assetTypeRepository.save(assetType);

        assertEquals("Test Type", savedAssetType.getName());
    }

    @Test
    @Order(6)
    void testFindTypeByName()
    {
        Optional<AssetType> type = assetTypeRepository.findByName("Monitor");

        AssetType foundAssetType = type.get();

        assertEquals("Monitor", foundAssetType.getName());
    }

    @Test
    @Order(7)
    void testUpdateById()
    {
        AssetType updatedFoundAssetType = assetTypeRepository.findById(1L).orElseThrow();
        updatedFoundAssetType.setName("Scheda Video");
        assetTypeRepository.save(updatedFoundAssetType);

        Optional<AssetType> type = assetTypeRepository.findByName("Scheda Video");
        updatedFoundAssetType = type.get();

        assertEquals("Scheda Video", updatedFoundAssetType.getName());
    }

    @Test
    @Order(8)
    void testDeleteByName()
    {
        Optional<AssetType> type = assetTypeRepository.findByName("Stampante");
        AssetType deletedAssetType = type.get();
        assetTypeRepository.delete(deletedAssetType);

        type = assetTypeRepository.findByName("Stampante");

        assertThrows(NoSuchElementException.class, type::get);
    }
}