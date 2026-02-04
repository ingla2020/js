package js.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {


    public record Geo(String lat, String lng) {}

    public record Address(String street, String suite, String city, String zipcode, Geo geo) {}

    public record Company(String name, String catchPhrase, String bs) {}

    // El Usuario principal con todos los campos anidados
    public record User(
            Integer id,
            String name,
            String username,
            String email,
            Address address,
            String phone,
            String website,
            Company company
    ) {}

    private final List<User> usersList = new ArrayList<>();

    public UserController() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://jsonplaceholder.typicode.com/users";
        try {
            // Descargamos el JSON y lo convertimos a un Array de Users
            User[] externalUsers = restTemplate.getForObject(url, User[].class);

            if (externalUsers != null) {
                usersList.addAll(Arrays.asList(externalUsers));
                System.out.println("--> Datos cargados exitosamente de JSONPlaceholder: " + usersList.size() + " usuarios.");
            }
        } catch (Exception e) {
            System.err.println("Error conectando con la API externa: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        if (usersList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usersList);
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> user = usersList.stream()
                .filter(u -> u.id().equals(id))
                .findFirst();
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        // Generamos un nuevo ID (simulado)
        // Buscamos el ID más alto actual y sumamos 1
        int maxId = usersList.stream().mapToInt(User::id).max().orElse(0);
        int nextId = maxId + 1;

        // Creamos el usuario copiando todos los datos complejos pero con el nuevo ID
        User createdUser = new User(
                nextId,
                newUser.name(),
                newUser.username(),
                newUser.email(),
                newUser.address(),
                newUser.phone(),
                newUser.website(),
                newUser.company()
        );

        usersList.add(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }



    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User userDetails) {
        Optional<User> existingUserOpt = usersList.stream()
                .filter(u -> u.id().equals(id))
                .findFirst();

        if (existingUserOpt.isPresent()) {
            // Reemplazamos todo el objeto manteniendo el ID original
            User updatedUser = new User(
                    id,
                    userDetails.name(),
                    userDetails.username(),
                    userDetails.email(),
                    userDetails.address(),
                    userDetails.phone(),
                    userDetails.website(),
                    userDetails.company()
            );

            usersList.remove(existingUserOpt.get());
            usersList.add(updatedUser);
            // Reordenamos por ID para mantener la lista limpia
            usersList.sort((u1, u2) -> u1.id().compareTo(u2.id()));

            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        boolean removed = usersList.removeIf(u -> u.id().equals(id));

        if (removed) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
