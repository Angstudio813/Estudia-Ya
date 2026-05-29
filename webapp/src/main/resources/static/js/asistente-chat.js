document.addEventListener("DOMContentLoaded", function () {
    // 1. Obtener el contenedor principal para extraer el usuarioId
    const container = document.getElementById("asistenteContainer");
    if (!container) return;

    // Extrae el ID que Spring Boot inyectó en el atributo 'data-usuario-id'
    const usuarioId = container.getAttribute("data-usuario-id");

    // 2. Capturar los elementos del DOM que actualizaremos con la IA
    const saludoUsuario = document.getElementById("saludoUsuario");
    const mensajePrincipalIA = document.getElementById("mensajePrincipalIA");
    const listaTemas = document.getElementById("listaTemas");
    const listaRecomendaciones = document.getElementById("listaRecomendaciones");

    // 3. Hacer la petición asíncrona a tu API REST de Spring Boot
    fetch(`/asistente-ia/api/${usuarioId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Error en la respuesta del servidor");
            }
            return response.json();
        })
        .then(data => {
            // Actualizar el saludo y el mensaje motivacional principal
            if (saludoUsuario) saludoUsuario.innerText = `Hola, ${data.nombreUsuario}`;
            if (mensajePrincipalIA) mensajePrincipalIA.innerText = data.mensajePrincipal;

            // Actualizar la lista de "Temas a reforzar"
            if (listaTemas) {
                listaTemas.innerHTML = ""; // Quitamos el mensaje de "Cargando..."
                
                if (!data.temasRefuerzo || data.temasRefuerzo.length === 0) {
                    listaTemas.innerHTML = `<p class="chat-muted">No hay temas críticos por ahora. ¡Sigue así! 🎉</p>`;
                } else {
                    data.temasRefuerzo.forEach(tema => {
                        const li = document.createElement("li");
                        li.innerText = tema;
                        listaTemas.appendChild(li);
                    });
                }
            }

            // Actualizar la lista de "Plan sugerido por IA"
            if (listaRecomendaciones) {
                listaRecomendaciones.innerHTML = ""; // Quitamos el mensaje de "Cargando..."
                
                if (!data.recomendaciones || data.recomendaciones.length === 0) {
                    listaRecomendaciones.innerHTML = `<li>No hay recomendaciones generadas todavía.</li>`;
                } else {
                    data.recomendaciones.forEach(rec => {
                        const li = document.createElement("li");
                        li.innerText = rec;
                        listaRecomendaciones.appendChild(li);
                    });
                }
            }
        })
        .catch(error => {
            console.error("Error al cargar la asistencia de la IA:", error);
            // Si la API falla, avisamos al alumno amigablemente en la pantalla
            if (mensajePrincipalIA) {
                mensajePrincipalIA.innerText = "No pudimos conectar con el tutor de IA en este momento. Por favor, intenta recargar la página.";
            }
        });
});

/**
 * Función global para los botones de sugerencias rápidas.
 * Copia el texto seleccionado en el input y le da el foco.
 */
function setSugerencia(texto) {
    const inputPregunta = document.getElementById("inputPregunta");
    if (inputPregunta) {
        inputPregunta.value = texto;
        inputPregunta.focus();
    }
}