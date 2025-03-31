package com.amaris.sensorprocessor.service;

import com.amaris.sensorprocessor.entity.User;
import com.amaris.sensorprocessor.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    @Autowired
    public CustomUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Charge un utilisateur à partir de son nom d'utilisateur et retourne un {@link UserDetails} pour l'authentification.
     * Cette méthode utilise un {@link UserDao} pour rechercher l'utilisateur en base de données,
     * puis crée un {@link org.springframework.security.core.userdetails.User} avec son nom d'utilisateur,
     * son mot de passe et ses rôles/permissions.
     *
     * @param username Le nom d'utilisateur de l'utilisateur à charger.
     * @return Un objet {@link UserDetails} contenant les informations de l'utilisateur.
     * @throws UsernameNotFoundException si l'utilisateur n'est pas trouvé dans la base de données.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username).orElseThrow();

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getGrantedAuthorities(user.getRole()));
    }

    /**
     * Génère une liste d'autorités (rôles) à partir du rôle d'un utilisateur.
     * Cette méthode convertit un rôle utilisateur en une autorité de sécurité avec le préfixe "ROLE_".
     *
     * @param role le rôle de l'utilisateur (par exemple, "ADMIN", "SUPERUSER", "USER").
     * @return une liste de {@link GrantedAuthority} représentant les rôles de l'utilisateur.
     */
    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return authorities;
    }

}
