package org.slieb.closure.dependencies;

import com.google.javascript.jscomp.SourceFile;
import org.slieb.dependencies.DependencyParser;
import org.slieb.dependencies.DependencyUtils;
import org.slieb.dependencies.ModuleResolver;
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.implementations.FileResource;
import slieb.kute.resources.providers.FileResourceProvider;
import slieb.kute.resources.providers.GroupResourceProvider;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static slieb.kute.resources.ResourcePredicates.extensionFilter;

public class GoogResources {

    public static GoogDependencyCalculator getCalculator(ResourceProvider<Resource.Readable> resourceProvider,
                                                         DependencyParser<Resource.Readable, GoogDependencyNode>
                                                                 parser) {
        return new GoogDependencyCalculator(resourceProvider, parser);
    }

    public static GoogDependencyCalculator getCalculator(ResourceProvider<Resource.Readable> resourceProvider) {

        return new GoogDependencyCalculator(resourceProvider);
    }
    

    public static GoogDependencyParser getDependencyParser() {
        return GoogDependencyCalculator.PARSER;
    }

    public static GoogDependencyNode parse(Resource.Readable resource) {
        return getDependencyParser().parse(resource);
    }

    public static ResourceProvider<FileResource> getResourceProviderForSourceDirectories(
            final Collection<File> directories) {
        return new GroupResourceProvider<>(directories.stream().distinct().map(
                FileResourceProvider::new).collect(toSet()));
    }

    public static ResourceProvider<FileResource> getResourceProviderForSourceDirectories(
            final Collection<File> directories,
            final String... extensions) {
        return Kute.filterResources(getResourceProviderForSourceDirectories(directories), extensionFilter(extensions));
    }

    public static SourceFile getSourceFileFromResource(Resource.Readable readable) {
        try (Reader reader = readable.getReader()) {
            return SourceFile.fromReader(readable.getPath(), reader);
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }

    public static ModuleResolver<GoogDependencyNode> getModuleResolver(ResourceProvider<Resource.Readable>
                                                                               resourceProvider,
                                                                       String commonModule) {
        final GoogDependencyHelper helper = getHelper();
        final List<GoogDependencyNode> deps = resourceProvider.stream().map(GoogResources::parse)
                .collect(Collectors.toList());
        final Map<GoogDependencyNode, Set<GoogDependencyNode>> dependencyMap =
                DependencyUtils.getDependencyMap(helper.getResolvableSet(deps));
        List<GoogDependencyNode> baseList = helper.getBaseList(deps);
        return new ModuleResolver<>(dependencyMap, commonModule, baseList);
    }

    public static Map<GoogDependencyNode, Set<GoogDependencyNode>> createDependencyMap(
            final ResourceProvider<Resource.Readable> provider) {
        return DependencyUtils.getDependencyMap(provider.stream()
                                                        .map(GoogResources::parse)
                                                        .collect(Collectors.toList()));
    }


    public static GoogDependencyHelper getHelper() {
        return new GoogDependencyHelper();
    }
}
