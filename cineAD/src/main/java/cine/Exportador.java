package cine;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

public class Exportador {

    // XML con JAXB
    public static void exportarXml(Catalogo catalogo, String ruta) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(Catalogo.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        // Se indica UTF-8 al crear el fichero para que coincida con lo que declara el XML
        try (Writer w = new BufferedWriter(new FileWriter(ruta, StandardCharsets.UTF_8))) {
            m.marshal(catalogo, w);
        }
    }

    // JSON con Gson: {"catalogo": {"peliculas": [ ... ]}}
    public static void exportarJson(Catalogo catalogo, String ruta) throws IOException {
        // disableHtmlEscaping evita que los apóstrofos salgan como \u0027
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        // El Map hace de objeto raíz para obtener la clave "catalogo"
        Map<String, Catalogo> raiz = new HashMap<>();
        raiz.put("catalogo", catalogo);

        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(ruta))) {
            gson.toJson(raiz, bw);
        }
    }

    // Binario con ObjectOutputStream (Catalogo, Pelicula y Persona son Serializable)
    public static void exportarBinario(Catalogo catalogo, String ruta) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(catalogo);
        }
    }
}