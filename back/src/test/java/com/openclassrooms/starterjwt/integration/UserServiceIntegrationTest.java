package com.openclassrooms.starterjwt.integration;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    public void testFindUserById() {
        // When
        User user = userService.findById(1L);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isNotNull();
        assertThat(user.getFirstName()).isNotNull();
        assertThat(user.getLastName()).isNotNull();
        assertThat(user.getPassword()).isNotNull();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    public void testFindUserByIdNotFound() {
        User user = userService.findById(999L);
        assertThat(user).isNull();
    }

    @Test
    public void testDeleteUser() {
        // Préparation - Vérifier que l'utilisateur existe
        User user = userService.findById(1L);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);

        // Exécution - Supprimer l'utilisateur
        userService.delete(1L);

        // Vérification - Vérifier que l'utilisateur est supprimé
        User deletedUser = userService.findById(1L);
        assertThat(deletedUser).isNull();
    }
}

