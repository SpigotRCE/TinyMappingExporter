package io.spigotrce.tinymappingexporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Tiny v1 mapping exporter. Currently, it supports class names and method names.
 *
 * @author SpigotRCE
 */
public class TinyV1Exporter {
    // key: old class name
    // value: new class name
    private final Map<String, String> classMappings = new HashMap<>();

    // key: owner.name+description
    // value: new name
    private final Map<String, String> methodMappings = new HashMap<>();

    public TinyV1Exporter(Map<String, String> classMappings, Map<String, String> methodMappings) {
        this.classMappings.putAll(classMappings);
        this.methodMappings.putAll(methodMappings);
    }

    public void setClassMappings(Map<String, String> mapping) {
        classMappings.putAll(mapping);
    }

    public void setMethodMappings(Map<String, String> mapping) {
        methodMappings.putAll(mapping);
    }

    public void exportMappings(String namespaceFrom,
                                      String namespaceTo,
                                      File outputPath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath.toPath())) {
            writer.write("v1\t" + namespaceFrom + "\t" + namespaceTo + "\n");

            for (Map.Entry<String, String> classEntry : classMappings.entrySet()) {
                String originalClassName = classEntry.getKey();
                String obfuscatedClassName = classEntry.getValue();

                writer.write("CLASS\t" + obfuscatedClassName + "\t" + originalClassName + "\n");

                for (Map.Entry<String, String> methodEntry : methodMappings.entrySet()) {
                    String fullKey = methodEntry.getKey();
                    String obfuscatedMethodName = methodEntry.getValue();

                    int lastDot = fullKey.lastIndexOf('.');
                    if (lastDot == -1) {
                        throw new IllegalArgumentException("Malformed method key (no dot): " + fullKey);
                    }

                    String owner = fullKey.substring(0, lastDot);
                    if (!owner.equals(obfuscatedClassName)) {
                        continue;
                    }

                    String nameAndDesc = fullKey.substring(lastDot + 1);
                    int descStart = nameAndDesc.indexOf('(');
                    if (descStart == -1) {
                        throw new IllegalArgumentException("Malformed method key (no descriptor): " + fullKey);
                    }

                    String originalMethodName = nameAndDesc.substring(0, descStart);
                    String descriptor = nameAndDesc.substring(descStart);

                    writer.write("METHOD\t" + obfuscatedClassName + "\t" + descriptor + "\t" +
                            obfuscatedMethodName + "\t" + originalMethodName + "\n");
                }
            }
        }
    }
}
