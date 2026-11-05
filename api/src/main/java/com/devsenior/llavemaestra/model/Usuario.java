package com.devsenior.llavemaestra.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

// Una cuenta del sistema (Hernan o un cliente).
// Implementa UserDetails para que Spring Security la entienda.
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    // Se guarda cifrada con BCrypt, nunca en texto plano
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    public Usuario() {
    }

    public Usuario(String nombre, String email, String password, Rol rol, boolean activo) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
    }

    // ---------- Lo que pide Spring Security ----------

    // Las "authorities" son los permisos del rol: HERRAMIENTAS_VER, ALQUILERES_CREAR...
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return rol.getPermisos().stream()
                .map(permiso -> new SimpleGrantedAuthority(permiso.name()))
                .toList();
    }

    // Los nombres de los permisos, ordenados, para meterlos en el token
    public List<String> getNombresPermisos() {
        return rol.getPermisos().stream().map(Enum::name).sorted().toList();
    }

    public boolean tienePermiso(Permiso permiso) {
        return rol.getPermisos().contains(permiso);
    }

    // Para Spring Security el "username" es el email
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    // Una cuenta bloqueada (activo = false) no puede autenticarse
    @Override
    public boolean isEnabled() {
        return activo;
    }

    // ---------- Getters y setters ----------

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
}
