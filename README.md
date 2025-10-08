📌 IDENTIFICACIÓN DE LA ACTIVIDAD: Tabla_Personas_JavaFXML_NIZAM
**📖 Descripción**

Aplicación de escritorio en **JavaFX** para gestionar información de personas, con soporte de **base de datos MariaDB** y registro de **logs**.
Permite **visualizar, agregar y eliminar datos** de manera interactiva mediante una tabla.
La aplicación ahora maneja las operaciones de base de datos de forma **asincrónica**, evitando que la interfaz se congele al cargar o modificar datos.
Se empaqueta como un **JAR ejecutable** listo para usar.

📂 Estructura del proyecto
**1. Código fuente**

📁 /src/main/java/org/example

✅ App.java → Clase principal que inicia la aplicación JavaFX y crea la carpeta de logs si no existe.

✅ Lanzador.java → Clase que lanza la aplicación desde Maven o IDE.

✅ bbdd/Conexion.java → Clase para la conexión a la base de datos.

✅ Controlador/Controller.java → Controlador de la interfaz FXML, ahora con operaciones asincrónicas.

✅ dao/DaoPersonas.java → Clase que gestiona las operaciones CRUD sobre los datos de personas.

✅ Modelo/Persona.java → Clase que representa la entidad Persona.

✅ module-info.java → Declaración de módulos y dependencias del proyecto.

**2. Recursos**

📁 /src/main/resources

✅ icono/icono.jpg → Icono de la aplicación.

✅ org.example.FXML/Pers.fxml → Archivo FXML que define la interfaz gráfica.

✅ SQL/logback.xml → Configuración de logging para SLF4J y Logback.

✅ META-INF/ → Archivos de metadatos si aplica.

**3. Bibliotecas y dependencias**

✅ JavaFX → Para la interfaz gráfica.

✅ SLF4J y Logback → Para registro de logs, ahora compatible con operaciones asincrónicas.

✅ MariaDB JDBC → Para conexión a la base de datos.

**⚠️ Solución de problemas conocidos**

❌ Error: Javadoc fallaba por module-info.java y módulos externos.

✅ Solución: Configuración del maven-javadoc-plugin para ignorar module-info.java, generando documentación correctamente.

**⚙️ Requisitos de ejecución**

✅ Lenguaje: Java SE 17 o superior

✅ Compilador: javac (incluido en JDK)

✅ IDE recomendado: IntelliJ IDEA, Eclipse o cualquier editor compatible con Maven

✅ Sistema operativo probado: Windows 10/11 y Linux Ubuntu 20.04

✅ Dependencias externas: JavaFX, SLF4J, Logback y MariaDB JDBC

**🚀 Instalación y ejecución**

Desde terminal (Windows/Linux/macOS) usando Maven:

**Limpiar y compilar el proyecto:**

    mvn clean package


**Ejecutar la aplicación:**

    java -jar target/TablaDePersonas.jar

**Resultado esperado**

    Se abre la ventana de la aplicación con la tabla de personas.

    Se pueden agregar, eliminar y visualizar personas sin que la interfaz se bloquee.

    Se crea automáticamente la carpeta logs si no existía.

    La aplicación mantiene un registro de operaciones en logs y se conecta a la base de datos para guardar/leer datos de forma asincrónica.

**✨ Autor/a**

👤 Nizam Abdel-Ghaffar
