package io.spigotrce.tinymappingexporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Tiny v1 mapping exporter. Currently, it supports class names and method names.
 *
 * @author SpigotRCE
 */
public class TinyV1Exporter {
    private final List<RemappedClass> classMappings = new ArrayList<>();
    private final List<RemappedMethod> methodMappings = new ArrayList<>();
    private final List<RemappedField> fieldMappings = new ArrayList<>();

    public void setClassMappings(List<RemappedClass> mappings) {
        classMappings.addAll(mappings);
    }

    public void setMethodMappings(List<RemappedMethod> mappings) {
        methodMappings.addAll(mappings);
    }

    public void setFieldMappings(List<RemappedField> mappings) {
        fieldMappings.addAll(mappings);
    }

    public void exportMappings(String namespaceFrom,
                               String namespaceTo,
                               File outputPath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath.toPath())) {
            writer.write("v1\t" + namespaceFrom + "\t" + namespaceTo + "\n");

            for (RemappedClass remappedClass : classMappings) {
                String oldName = remappedClass.oldName();
                String newName = remappedClass.newName();

                writer.write("CLASS\t" + newName + "\t" + oldName + "\n");
            }

            for (RemappedField remappedField : fieldMappings) {
                String className = remappedField.className();
                String oldName = remappedField.oldName();
                String description = remappedField.description();
                String newName = remappedField.newName();

                writer.write("FIELD\t" + className + "\t" + description + "\t" + newName + "\t" + oldName + "\n");
            }

            for (RemappedMethod remappedMethod : methodMappings) {
                String className = remappedMethod.className();
                String oldName = remappedMethod.oldName();
                String description = remappedMethod.description();
                String newName = remappedMethod.newName();

                writer.write("METHOD\t" + className + "\t" + description + "\t" + newName + "\t" + oldName + "\n");
            }
        }
    }
}
