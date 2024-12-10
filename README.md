# Memoria - Text Extractor 

## Introducción
Text Extractor consiste en una aplicación móvil que permite escanear el texto de una imagen y realizar diversas 
funciones con el mismo.

## Objetivos
El objetivo de este proyecto consiste en la realización de una aplicación móvil con diversas funcionalidades
en la que se aproveche alguna de las características del mismo. A su vez, también era de interés la integración de una base de datos
y un inicio de sesión.

## Estructura
El proyecto presenta la siguiente estructura principal de carpetas:
- En la carpeta **manifest** se encuentra el archivo AndroidManifest.xml, donde se definen los aspectos visuales como el 
nombre de la aplicación, el nombre así como los componentes principales de la misma.
- En **kotlin+java/com.example.textextractor** se encuentra el código fuente de las Activities y las clases que usan.
- La carpeta **res** alberga todos los recursos como imágenes, iconos

## Diseño
El diseño de la aplicación se ha vis

---

## Funcionalidades principales

1. **Escaneo de texto**:
    - Obtiene el texto de una imagen.
    - Puede ser de una foto tomada al momento o de una alojada en el dispositivo.

2. **Copiar el texto**:
    - Permite copiar el texto escaneado.
    - Se puede pegar en cualquier lugar.

3. **Guardar el texto**:
    - Permite guardar el texto en Firebase para poder tener un registro de todos los que el usuario haya guardado.

4. **Historial**:
    - Muestra un registro de todos los textos que el usuario haya guardado en su usuario.

5. **Ajustes**:

6. **Ayuda**:
    - Muestra una lista de preguntas y respuestas frecuentes para orientar al usuario en el funcionamiento básico de la aplicación.

7. **Registro de usuarios**
- Registro mediante email y contraseña o a través de una cuenta de Google.
- Inicio de sesión seguro gracias a Firebase.

## Arquitectura

