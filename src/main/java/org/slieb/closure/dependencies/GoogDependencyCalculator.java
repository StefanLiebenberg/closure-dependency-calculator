package org.slieb.closure.dependencies;

import org.slieb.dependencies.DependenciesHelper;
import org.slieb.dependencies.DependencyCalculator;
import org.slieb.dependencies.DependencyParser;
import slieb.kute.api.Resource;

import java.util.Iterator;


public class GoogDependencyCalculator extends DependencyCalculator<Resource.Readable, GoogDependencyNode> {

    public static final GoogDependencyHelper HELPER = new GoogDependencyHelper();

    public static final GoogDependencyParser PARSER = new GoogDependencyParser();

    public GoogDependencyCalculator(Iterable<Resource.Readable> resources, DependencyParser<Resource.Readable, GoogDependencyNode> parser, DependenciesHelper<GoogDependencyNode> helper) {
        super(resources, parser, helper);
    }

    public GoogDependencyCalculator(Iterable<Resource.Readable> resources, DependencyParser<Resource.Readable, GoogDependencyNode> parser) {
        this(resources, parser, HELPER);
    }

    public GoogDependencyCalculator(Iterable<Resource.Readable> resources) {
        this(resources, PARSER, HELPER);
    }

    public static Iterable<Resource.Readable> wrapIterable(Iterable<? extends Resource.Readable> iterable) {
        return new WrapperIterable(iterable);
    }

}

// temp solution to the generics problem.
class WrapperIterable implements Iterable<Resource.Readable> {

    private final Iterable<? extends Resource.Readable> iterable;

    public WrapperIterable(Iterable<? extends Resource.Readable> iterable) {
        this.iterable = iterable;
    }

    @Override
    public Iterator<Resource.Readable> iterator() {
        return new WrapperIterator(iterable.iterator());
    }
}

class WrapperIterator implements Iterator<Resource.Readable> {

    private final Iterator<? extends Resource.Readable> iterator;

    public WrapperIterator(Iterator<? extends Resource.Readable> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Resource.Readable next() {
        return iterator.next();
    }
}