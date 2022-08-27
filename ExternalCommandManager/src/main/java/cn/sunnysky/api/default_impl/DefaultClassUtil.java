package cn.sunnysky.api.default_impl;

import cn.sunnysky.util.ClassUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class DefaultClassUtil extends ClassUtil {

    @SuppressWarnings("NewApi")
    @Override
    public List<Class<?>> getClassesForPackage(String pkgName) throws IOException, URISyntaxException {
        final String pkgPath = pkgName.replace('.', '/');
        final URI pkg = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(pkgPath)).toURI();
        final ArrayList<Class<?>> allClasses = new ArrayList<Class<?>>();

        Path root;
        if (pkg.toString().startsWith("jar:")) {
            try {
                root = FileSystems.getFileSystem(pkg).getPath(pkgPath);
            } catch (final FileSystemNotFoundException e) {
                root = FileSystems.newFileSystem(pkg, Collections.emptyMap()).getPath(pkgPath);
            }
        } else {
            root = Paths.get(pkg);
        }

        final String extension = ".class";
        try (final Stream<Path> allPaths = Files.walk(root)) {
            allPaths.filter(Files::isRegularFile).forEach(file -> {
                try {
                    final String path = file.toString().replace('/', '.');
                    final String name = path.substring(path.indexOf(pkgName), path.length() - extension.length());
                    allClasses.add(Class.forName(name));
                } catch (final ClassNotFoundException | StringIndexOutOfBoundsException ignored) {
                }
            });
        }
        return allClasses;
    }
}
