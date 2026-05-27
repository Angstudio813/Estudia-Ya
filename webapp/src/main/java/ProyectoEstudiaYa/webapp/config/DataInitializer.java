package ProyectoEstudiaYa.webapp.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner loadInitialData(JdbcTemplate jdbc, PasswordEncoder passwordEncoder) {
        return args -> {
            Long totalUsuarios = jdbc.queryForObject("SELECT COUNT(*) FROM usuarios", Long.class);
            if (totalUsuarios != null && totalUsuarios > 0) {
                return;
            }

            // USUARIOS
            jdbc.update("""
                INSERT INTO usuarios
                (id, nombre, apellido, email, password, nivel, grado, xp_total, nivel_juego, racha_actual, racha_mas_alta, fecha_registro, ultimo_acceso)
                VALUES
                (1, 'Carlos', 'Lopez', 'carlos@estudiaya.pe', ?, 'SECUNDARIA', 3, 620, 8, 7, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """, passwordEncoder.encode("123456"));

            // CURSOS
            jdbc.update("""
                INSERT INTO cursos (id, nombre, nivel, grado, descripcion, color_hex, icono) VALUES
                (1, 'Matematica', 'SECUNDARIA', 3, 'Curso de matematica para tercero de secundaria', '#2563EB', '📐'),
                (2, 'Comunicacion', 'SECUNDARIA', 3, 'Curso de comunicacion y comprension lectora', '#0EA5E9', '📚')
            """);

            // TEMAS
            jdbc.update("""
                INSERT INTO temas (id, nombre, descripcion, orden, curso_id) VALUES
                (1, 'Fracciones', 'Operaciones con fracciones', 1, 1),
                (2, 'Decimales', 'Operaciones con numeros decimales', 2, 1),
                (3, 'Comprension lectora', 'Analisis de textos', 1, 2)
            """);

            // EJERCICIOS
            jdbc.update("""
                INSERT INTO ejercicios
                (id, pregunta, opciona, opcionb, opcionc, opciond, respuesta_correcta, explicacion, dificultad, generado_poria, tema_id)
                VALUES
                (1, 'Cuanto es 1/2 + 1/4?', '1/8', '3/4', '2/6', '1', 'B', 'Se usa denominador comun: 2/4 + 1/4 = 3/4', 'FACIL', FALSE, 1),
                (2, 'Cual es la idea principal del texto?', 'Detalle secundario', 'Opinion del lector', 'Mensaje central', 'Dato aislado', 'C', 'La idea principal resume el mensaje central', 'MEDIO', TRUE, 3)
            """);

            // USUARIO_CURSOS
            jdbc.update("""
                INSERT INTO usuario_cursos
                (id, usuario_id, curso_id, porcentaje_completado, fecha_inscripcion, ultima_practica)
                VALUES
                (1, 1, 1, 65, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                (2, 1, 2, 42, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """);

            // PROGRESOS
            jdbc.update("""
                INSERT INTO progresos
                (id, usuario_id, tema_id, ejercicios_intentados, ejercicios_correctos, porcentaje_acierto, ultima_practica, necesita_refuerzo)
                VALUES
                (1, 1, 1, 20, 13, 65.0, CURRENT_TIMESTAMP, FALSE),
                (2, 1, 2, 18, 8, 44.4, CURRENT_TIMESTAMP, TRUE),
                (3, 1, 3, 16, 11, 68.7, CURRENT_TIMESTAMP, FALSE)
            """);

            // INTENTOS_EJERCICIO
            jdbc.update("""
                INSERT INTO intentos_ejercicio
                (id, usuario_id, ejercicio_id, respuesta_elegida, es_correcta, fecha_intento, tiempo_segundos)
                VALUES
                (1, 1, 1, 'B', TRUE, CURRENT_TIMESTAMP, 42),
                (2, 1, 2, 'A', FALSE, CURRENT_TIMESTAMP, 58)
            """);

            // TAREAS
            jdbc.update("""
                INSERT INTO tareas
                (id, titulo, descripcion, estado, fecha_vencimiento, fecha_creacion, fecha_completado, usuario_id, curso_id)
                VALUES
                (1, 'Practicar 10 ejercicios de fracciones', 'Completar practica del tema fracciones', 'EN_PROGRESO', CURRENT_DATE + 1, CURRENT_TIMESTAMP, NULL, 1, 1),
                (2, 'Resumen de texto argumentativo', 'Leer y resumir texto de 2 paginas', 'PENDIENTE', CURRENT_DATE + 2, CURRENT_TIMESTAMP, NULL, 1, 2)
            """);

            // LOGROS
            jdbc.update("""
                INSERT INTO logros
                (id, nombre, descripcion, icono, tipo, fecha_desbloqueado, usuario_id)
                VALUES
                (1, 'Primera racha', 'Completaste 3 dias seguidos de practica', '🏆', 'RACHA', CURRENT_TIMESTAMP, 1),
                (2, 'Punteria perfecta', '5 respuestas correctas consecutivas', '🎯', 'EJERCICIOS', CURRENT_TIMESTAMP, 1)
            """);

            // SESIONES_AUDITORIA
            jdbc.update("""
                INSERT INTO sesiones_auditoria
                (id, usuario_id, modulo, tipo_evento, descripcion, ip_address, fecha_evento)
                VALUES
                (1, 1, 'DASHBOARD', 'VISITA_MODULO', 'Ingreso al dashboard principal', '127.0.0.1', CURRENT_TIMESTAMP),
                (2, 1, 'PRACTICA', 'INICIO_EJERCICIO', 'Inicio de practica de fracciones', '127.0.0.1', CURRENT_TIMESTAMP)
            """);
        };
    }
}
