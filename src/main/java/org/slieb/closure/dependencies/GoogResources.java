package org.slieb.closure.dependencies;

import com.google.javascript.jscomp.SourceFile;
import org.slieb.dependencies.DependencyParser;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;
import slieb.kute.resources.implementations.FileResource;
import slieb.kute.resources.providers.FileResourceProvider;
import slieb.kute.resources.providers.GroupResourceProvider;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

import static java.util.stream.Collectors.toSet;
import static slieb.kute.resources.ResourcePredicates.extensionFilter;

public class GoogResources {

    public static GoogDependencyCalculator getCalculator(ResourceProvider<Resource.Readable> resourceProvider,
                                                         DependencyParser<Resource.Readable, GoogDependencyNode> parser) {
        return new GoogDependencyCalculator(resourceProvider, parser);
    }

    public static GoogDependencyCalculator getCalculator(ResourceProvider<Resource.Readable> resourceProvider) {
        return new GoogDependencyCalculator(resourceProvider);
    }

    public static GoogDependencyCalculator getCalculatorCast(ResourceProvider<? extends Resource.Readable> resourceProvider) {
        return new GoogDependencyCalculator(Resources.mapResources(resourceProvider, a -> a));
    }

    public static GoogDependencyParser getDependencyParser() {
        return GoogDependencyCalculator.PARSER;
    }

    public static ResourceProvider<FileResource> getResourceProviderForSourceDirectories(Collection<File> directories) {
        return new GroupResourceProvider<>(directories.stream().distinct().map(FileResourceProvider::new).collect(toSet()));
    }

    public static ResourceProvider<FileResource> getResourceProviderForSourceDirectories(Collection<File> directories, String... extensions) {
        return Resources.filterResources(getResourceProviderForSourceDirectories(directories), extensionFilter(extensions));
    }

    public static SourceFile getSourceFileFromResource(Resource.Readable readable) {
        try (Reader reader = readable.getReader()) {
            return SourceFile.fromReader(readable.getPath(), reader);
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }


}
