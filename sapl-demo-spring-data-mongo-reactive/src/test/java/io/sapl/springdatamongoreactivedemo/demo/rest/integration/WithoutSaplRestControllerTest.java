package io.sapl.springdatamongoreactivedemo.demo.rest.integration;

import io.sapl.springdatamongoreactivedemo.demo.repository.Role;
import io.sapl.springdatamongoreactivedemo.demo.repository.User;
import io.sapl.springdatamongoreactivedemo.demo.rest.WithoutSaplRestController;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.List;

import static io.sapl.springdatamongoreactivedemo.demo.repository.Role.USER;

@SpringBootTest
class WithoutSaplRestControllerTest {

    @Autowired
    WithoutSaplRestController withoutSaplRestController;

    @Test
    void when_findAllByAgeAfter_then_getDataWithoutAnyManipulation() {
        // GIVEN
        var expectedPersons = List.of(
                new User(new ObjectId("64de3bd9fbf82799677ed338"), "Terrel", "Woodings", 96, USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed33a"), "Konstantine", "Hampton", 96, USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed346"), "Kacey", "Angell", 94, USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09cccf7"), "Tabby", "Skittreal", 93, Role.ADMIN, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd03"), "Claudianus", "Courtois", 98, USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd0d"), "Lexine", "Blakden", 92, USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd0e"), "Petra", "Shackleford", 97, Role.ADMIN, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd15"), "Henderson", "Enticknap", 94, USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd24"), "Deina", "Watting", 92, Role.ADMIN, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd28"), "Jonie", "Delle", 95, Role.ADMIN, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd2f"), "Amy", "Gurnett", 99, Role.ADMIN, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd33"), "Edi", "Giacopelo", 94, Role.ADMIN, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd3c"), "Jany", "Lambourne", 92, Role.ADMIN, Boolean.FALSE));

        // WHEN
        var result = withoutSaplRestController.findAllByAgeAfter(90).collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();
    }

    @Test
    void when_customRepositoryMethod_then_getDataWithoutAnyManipulation() {
        // GIVEN
        var expectedPersons = List.of(
                new User(new ObjectId("64de2fb8375aabd24878daa4"), "Malinda", "Perrot", 53, Role.ADMIN, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed336"), "Emerson", "Rowat", 82, USER, Boolean.FALSE),
                new User(new ObjectId("64de3bd9fbf82799677ed337"), "Yul", "Barukh", 79, USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed338"), "Terrel", "Woodings", 96, USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed339"), "Martino", "Bartolijn", 33, USER, Boolean.FALSE),
                new User(new ObjectId("64de3bd9fbf82799677ed33a"), "Konstantine", "Hampton", 96, USER, Boolean.FALSE),
                new User(new ObjectId("64de3bd9fbf82799677ed33b"), "Cathleen", "Simms", 25, Role.ADMIN, Boolean.FALSE),
                new User(new ObjectId("64de3bd9fbf82799677ed33c"), "Adolphe", "Streeton", 46, USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed33d"), "Alessandro", "Tomaskov", 64, USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed33e"), "Hobie", "Maddinon", 32, USER, Boolean.FALSE));

        // WHEN
        var result = withoutSaplRestController.customRepositoryMethod().collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();
    }

    @Test
    void when_fetchingByQueryMethodLastnameContains_then_getDataWithoutAnyManipulation() {
        // GIVEN
        var expectedPersons = List.of(
                new User(new ObjectId("64de3bd9fbf82799677ed344"), "Tuckie", "Morfell", 35, USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed346"), "Kacey", "Angell", 94, USER, Boolean.FALSE),
                new User(new ObjectId("64de3bd9fbf82799677ed348"), "Perren", "Durtnall", 75, USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09cccfd"), "Hedwig", "Berrill", 66, Role.ADMIN, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd07"), "Winna", "Shellshear", 46, Role.ADMIN, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd11"), "Dianemarie", "Howgill", 69, USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd14"), "Titus", "Gillbard", 38, USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd28"), "Jonie", "Delle", 95, Role.ADMIN, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd2b"), "Gray", "Ashall", 39, USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd2d"), "Pryce", "Mosedill", 46, USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd31"), "Casper", "Upstell", 85, USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd32"), "Lynnett", "Malloch", 62, Role.ADMIN, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd39"), "Darwin", "Atwill", 28, USER, Boolean.TRUE));

        // WHEN
        var result = withoutSaplRestController.fetchingByQueryMethodLastnameContains("ll").collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();
    }

    @Test
    void when_findAllByAgeAfterAndRole_then_getDataWithoutAnyManipulation() {
        // GIVEN
        var expectedPersons = List.of(
                new User(new ObjectId("64de594d36d30786c09cccf7"), "Tabby", "Skittreal", 93, Role.ADMIN, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd0e"), "Petra", "Shackleford", 97, Role.ADMIN, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd24"), "Deina", "Watting", 92, Role.ADMIN, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd28"), "Jonie", "Delle", 95, Role.ADMIN, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd2f"), "Amy", "Gurnett", 99, Role.ADMIN, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd33"), "Edi", "Giacopelo", 94, Role.ADMIN, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd3c"), "Jany", "Lambourne", 92, Role.ADMIN, Boolean.FALSE));

        // WHEN
        var result = withoutSaplRestController.findAllByAgeAfterAndRole(90, "ADMIN").collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();
    }
    
    @Test
    void when_findAllByAgeAfterAndRoleIsNotProtectedSo_then_returnNoAccessDeniedException() {
        // GIVEN
        var expectedPersons = List.of(
                new User(new ObjectId("64de3bd9fbf82799677ed336"), "Emerson", "Rowat", 82, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de3bd9fbf82799677ed337"), "Yul", "Barukh", 79, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed338"), "Terrel", "Woodings", 96, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed339"), "Martino", "Bartolijn", 33, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de3bd9fbf82799677ed33a"), "Konstantine", "Hampton", 96, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed33c"), "Adolphe", "Streeton", 46, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed33d"), "Alessandro", "Tomaskov", 64, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed33e"), "Hobie", "Maddinon", 32, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de3bd9fbf82799677ed340"), "Giraldo", "Scade", 83, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed342"), "Mario", "Albinson", 54, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de3bd9fbf82799677ed343"), "Olav", "Hoopper", 32, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed344"), "Tuckie", "Morfell", 35, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed345"), "Sylas", "Bickerstasse", 66, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed346"), "Kacey", "Angell", 94, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de3bd9fbf82799677ed347"), "Dame", "Negri", 67, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed348"), "Perren", "Durtnall", 75, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09cccf6"), "Mac", "Deetlof", 55, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09cccf8"), "Adriano", "Tennet", 60, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09cccf9"), "Cameron", "Garnham", 39, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09cccfc"), "Reynolds", "Buesnel", 55, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09cccff"), "Thaddeus", "Machin", 50, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd03"), "Claudianus", "Courtois", 98, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd04"), "Imelda", "Gilkes", 65, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd08"), "Patty", "O Mahoney", 52, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd0a"), "Fielding", "MacGibbon", 53, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd0b"), "Tuckie", "Hugett", 29, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd0c"), "Penrod", "Munehay", 70, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd0d"), "Lexine", "Blakden", 92, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd0f"), "Glenn", "Stennes", 45, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd10"), "Morry", "Wolfer", 23, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd11"), "Dianemarie", "Howgill", 69, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd13"), "Lou", "Kiossel", 62, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd14"), "Titus", "Gillbard", 38, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd15"), "Henderson", "Enticknap", 94, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd18"), "Forrest", "Izzett", 62, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd1a"), "Hazel", "Alston", 77, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd1b"), "Aldrich", "Maymond", 24, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd1c"), "Harmon", "Foulis", 30, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd20"), "Hyatt", "Cron", 43, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd22"), "Carleton", "Keyson", 79, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd23"), "Byran", "Dumbare", 22, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd25"), "Thacher", "Folca", 18, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd26"), "Gayle", "Orneles", 82, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd29"), "Lin", "Burleigh", 72, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd2b"), "Gray", "Ashall", 39, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd2c"), "Lorant", "Busch", 87, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd2d"), "Pryce", "Mosedill", 46, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd31"), "Casper", "Upstell", 85, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd34"), "Bryanty", "Arnaud", 84, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd36"), "Jodi", "Corah", 58, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd39"), "Darwin", "Atwill", 28, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd3b"), "Redford", "Palphreyman", 90, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd3d"), "Tod", "Siddaley", 69, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd3f"), "Hobart", "Strand", 54, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de594d36d30786c09ccd40"), "Erastus", "Spoure", 49, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de594d36d30786c09ccd42"), "Bastian", "Dearden", 32, Role.USER, Boolean.FALSE));
        
        // WHEN     
        var result = withoutSaplRestController.findAllByAgeAfterAndRole(17, "USER").collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();
    }

}