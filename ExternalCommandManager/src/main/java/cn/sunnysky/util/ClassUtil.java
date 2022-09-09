package cn.sunnysky.util;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ClassUtil {

    @SuppressWarnings("NewApi")
    public abstract @NotNull List<Class<?>> getClassesForPackage(final String pkgName) throws IOException, URISyntaxException;
}
