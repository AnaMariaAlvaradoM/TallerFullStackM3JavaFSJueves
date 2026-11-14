# Bitácora de uso de IA

Registra cada vez que uses una IA en el taller. **Una respuesta de IA no es evidencia; la verificación sí.**

| Fecha | Fase | Qué intentaba resolver | Prompt que usé | Qué acepté o rechacé | Cómo lo comprobé | Qué aprendí |
|---|---|---|---|---|---|---|
| 5/11/2026 | fase 2: Backend  | Declarar el método para que solo se pueda tener tres Alquileres activos al tiempo y seCruzan | private void alquileresActivos(Authentication authentication, EstadoAlquiler estado){
    Usuario usuario = usuarioRepository.findBynombre(authentication.getName())
            .orElseThrow(()->new RecursoNoEncontradoException("No existe un usuario con ese nombre " + authentication.getName()));
    
    if ((estado == EstadoAlquiler.RESERVADO)|| estado ==EstadoAlquiler.ENTREGADO){
         
    }

    List<Alquiler> alquilerActivo = alquilerRepository.findByUsuarioAndEstado( usuario,estado);

    long cantidadAlquileres = alquilerRepository.countByUsuarioAndEstado(usuario,estado);

    if (cantidadAlquileres>=3){
        throw new ReglaNegocioException("No puedes tener más de tres alquileres al mismo tiempo. tienes: " + cantidadAlquileres );
    }
}
como voy ? | Acepté la creación de una lista dentro del metodo para validar la cantidad de alquileres que tenían el estado Activo como 'RESERVADO' o 'ENTREGAGO' y especificarla en los detalles del repositorio |Una vez que terminé de escribir el metodo, corrí la aplicación en swagger e hice pruebas manuales, luego ejecuté npm run calificar -- 2 para ver si satisfacía los test  | Aprendí a implementar métodos de conteo en SpringData, como establecer las reglas de negocio dentro del service. Utilizo la Ia (Claude) como una guía, no le pido que me desarrolle código, sino que me explique y oriente para llegar a la solución.  |
