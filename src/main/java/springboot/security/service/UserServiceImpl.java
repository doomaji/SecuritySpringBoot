package springboot.security.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.security.jpa.RoleRepository;
import springboot.security.jpa.UserRepository;
import springboot.security.model.Role;
import springboot.security.model.User;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    @Override
    public void saveUser(User incoming) {

        if (incoming.getId() == null) {
            incoming.setPassword(passwordEncoder.encode(incoming.getPassword()));
            userRepository.save(incoming);
            return;
        }

        User db = userRepository.findById(incoming.getId())
                .orElseThrow(() -> new RuntimeException("User not found: " + incoming.getId()));

        db.setFirstName(incoming.getFirstName());
        db.setLastName(incoming.getLastName());
        db.setEmail(incoming.getEmail());
        db.setUsername(incoming.getUsername());
        db.setRoles(incoming.getRoles());

        if (incoming.getPassword() != null && !incoming.getPassword().isBlank()) {
            db.setPassword(passwordEncoder.encode(incoming.getPassword()));
        }
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}