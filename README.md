# Memoria del Proyecto de Final de Curso - Text Extractor

## Introducción
Text Extractor es una aplicación móvil diseñada para escanear texto de imágenes y ofrecer varias funcionalidades, como copiarlo o guardarlo. Este proyecto busca aprovechar las características nativas de los dispositivos móviles, integrando tecnologías modernas y servicios en la nube.

---

## Objetivos

El proyecto tiene los siguientes objetivos principales:

1. Desarrollar una aplicación móvil funcional y fácil de usar que aproveche capacidades nativas como la cámara y la gestión de almacenamiento.
2. Implementar un sistema de registro e inicio de sesión seguro mediante Firebase Authentication.
3. Diseñar e integrar una base de datos en la nube para almacenar el historial de textos escaneados.
4. Ofrecer una experiencia multilingüe con soporte para inglés y español.
5. Incorporar un diseño personalizable.
6. Proveer opciones de personalización y ayuda para mejorar la experiencia del usuario.

---

## Estructura del Proyecto

El proyecto sigue una estructura organizada en las siguientes carpetas principales:

- **manifest**: Contiene el archivo `AndroidManifest.xml`, donde se definen configuraciones esenciales de la aplicación, como el nombre, permisos requeridos y componentes principales.

- **com.example.textextractor**: Carpeta principal del código fuente, que incluye:
   - **Activities**: Controlan las pantallas de la aplicación.
   - **Clases auxiliares**: Manejan lógicas específicas, como la integración con Firebase o la detección de texto.

- **res**: Reúne todos los recursos visuales y de configuración:
   - **drawable**: Imágenes e íconos utilizados en la interfaz.
   - **menu**: Opciones para los diversos menús.
   - **values**: Archivos de configuración para idiomas y estilos de la aplicación.
      - **colors**: Definiciones de los colores usados en la aplicación.
      - **strings**: Estructura del texto de la aplicación en diferentes idiomas.
      - **themes**: Definición del tema principal de la app, así como su versión oscura.
   - **mipmap**: Versiones del ícono de la aplicación adaptadas a diferentes densidades de pantalla.

---

## Diseño de la Aplicación

Durante el desarrollo, el diseño de Text Extractor ha sido modificado con respecto al diseño inicial con el fin de mejorar la experiencia del usuario:

1. **Cámara**:
   - La cámara solo se activa al presionar el botón “Capturar imagen”.
   - Las imágenes capturadas se muestran en miniatura para optimizar el espacio en pantalla.

2. **Interfaz**:
   - Se introdujeron dos botones en la parte inferior para capturar o seleccionar imágenes del dispositivo.
   - El acceso al historial se movió al menú desplegable en la parte superior.
   - Se eliminaron las opciones de flash y rotar cámara, ya que están integradas en la interfaz nativa de la cámara.

3. **Registro e Inicio de Sesión**:
   - Se añadió una vista exclusiva para gestionar el registro e inicio de sesión de los usuarios.

---

## Funcionalidades Principales

1. **Escaneo de texto**:
   - Detecta texto en imágenes capturadas con la cámara o seleccionadas del almacenamiento.
   - Utiliza bibliotecas de reconocimiento óptico de caracteres (OCR) para garantizar resultados precisos.

2. **Copiar texto**:
   - Permite copiar el texto escaneado al portapapeles para reutilizarlo en otras aplicaciones.

3. **Guardar texto**:
   - Los textos escaneados se pueden almacenar en Firebase, creando un registro accesible desde cualquier dispositivo asociado al usuario.

4. **Historial**:
   - Presenta una lista de textos guardados, ordenados por fecha.
   - Ofrece opciones para copiar o eliminar entradas del historial.

5. **Ajustes**:
   - Permite cambiar entre tema oscuro o claro.
   - Ofrece configuración de idioma (Inglés, Español, Francés, Italiano, Alemán y Portugués).

6. **Ayuda**:
   - Incluye una sección de preguntas frecuentes para resolver dudas comunes sobre el uso de la aplicación.

7. **Registro de usuarios**:
   - Registro mediante correo electrónico y contraseña.
   - Integración con cuentas de Google para un acceso más rápido.
   - Autenticación segura mediante Firebase Authentication.

8. **Idioma predeterminado**:
   - La aplicación detecta y adopta el idioma configurado en el dispositivo del usuario, si este se encuentra en la selección disponible dentro de la app.

---

## Arquitectura Implementada

La aplicación desarrollada utiliza una arquitectura basada en los principios de MVVM (Model-View-ViewModel), aunque no implementa un ViewModel de manera explícita. En lugar de ello, se apoya en algunos de los conceptos fundamentales de MVVM, como la separación de responsabilidades y la gestión del estado de la UI.

---

## Tecnologías Utilizadas

- **Plataforma**:
   - Android Studio para el desarrollo de la aplicación.
   - Firebase para autenticación y almacenamiento en la nube.

- **Bibliotecas**:
   - ML Kit para reconocimiento de texto.

- **Lenguajes**:
   - Kotlin para el código principal.
   - XML para recursos visuales.

---

## Conclusiones

Text Extractor es una aplicación versátil que combina tecnología avanzada con un diseño intuitivo. Durante el desarrollo, se logró superar los desafíos de integración de tecnologías y optimización de la interfaz, resultando en un producto que cumple con los objetivos planteados. La aplicación se encuentra lista para ser utilizada y ampliada en futuras iteraciones, según las necesidades de los usuarios.

---

