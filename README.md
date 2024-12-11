# Memoria del proyecto de final de curso - Text Extractor 

## Introducción
Text Extractor consiste en una aplicación móvil que permite escanear el texto de una imagen y realizar diversas 
funciones como copiarlo o guardarlo. 

## Objetivos
El objetivo de este proyecto consiste en la realización de una aplicación móvil con diversas funcionalidades
en la que se aproveche alguna de las características propias del mismo. A su vez, también era de interés la integración de una base de datos
y un inicio de sesión.

## Estructura
El proyecto presenta la siguiente estructura principal de carpetas:
- En la carpeta **manifest** se encuentra el archivo AndroidManifest.xml, donde se definen los aspectos visuales como el 
nombre de la aplicación, el nombre así como los componentes principales de la misma.
- En **com.example.textextractor** se encuentra el código fuente de las Activities y las clases que usan.
- La carpeta **res** alberga todos los recursos como imágenes e iconos (carpeta drawable) y opciones de menú. Ciertos recursos están separados
por idioma o tema del dispositivo (values). En las carpetas mipmap se encuentran distintas versiones del icono de la aplicación.

## Diseño
El diseño de la aplicación se ha visto modificado con respecto al primer diseño realizado. Los cambios principales han sido:
- La cámara no se activará hasta que se pulse el botón de 'Capturar imagen'.
- La imagen se mostrará más pequeña una vez se tome la fotografía.
- Se han añadido dos botones en la parte inferior para tomar y seleccionar fotografías.
- En la parte superior, la opción de historial ha sido introducida dentro de las opciones del menú desplegable.
- La opción de flash y de girar la cámara se han eliminado, ya que están incluidas dentro de la propia cámara al tomar la fotografía.
- Se ha incluido una vista nueva para manejar el registro e inicio de sesión de los usuarios. 
---

## Funcionalidades principales

1. **Escaneo de texto**:
   - Obtiene el texto de una imagen.
   - Puede ser de una foto tomada al momento o de una alojada en el dispositivo.

2. **Copiar el texto**:
   - Permite copiar el texto escaneado al portapapeles.
   - Se puede pegar en cualquier lugar.

3. **Guardar el texto**:
   - Permite guardar el texto en Firebase para poder tener un registro de todos los que el usuario haya guardado.

4. **Historial**:
   - Muestra un registro de todos los textos que el usuario haya guardado en su usuario ordenados por fecha.
   - Se puede copiar su texto o eliminarlos.

5. **Ajustes**:
   - Permite alternar entre tema oscuro o claro.
   - Permite cambiar el idioma de la aplicación a inglés o español.

6. **Ayuda**:
   - Muestra una lista de preguntas y respuestas frecuentes para orientar al usuario en el funcionamiento básico de la aplicación.

7. **Registro de usuarios**:
   - Registro mediante email y contraseña o a través de una cuenta de Google.
   - Inicio de sesión seguro gracias a Firebase.

8. **Idioma predeterminado**
   - La aplicación toma como predeterminado el idioma del dispositivo.

## Arquitectura


